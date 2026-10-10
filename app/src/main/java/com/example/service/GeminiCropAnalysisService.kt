package com.example.service

import android.graphics.Bitmap
import android.util.LruCache
import com.example.data.local.AgriculturalKnowledgeBase
import com.example.data.remote.*
import com.example.engine.CropAndDiseaseDetector
import com.example.engine.LocalChatEngine
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiCropAnalysisService(
    private val networkMonitor: NetworkMonitor
) : CropAnalysisService {

    // In-memory cache for recent crop diagnoses (keyed by image bitmap hash + language)
    private val diagnosisCache = LruCache<String, DiagnosisResult>(25)

    override suspend fun analyzeCrop(
        bitmap: Bitmap,
        imageUris: List<String>,
        manualCropOverride: Crop?,
        language: Language,
        forceOffline: Boolean,
        farmerDescription: String?
    ): DiagnosisResult = withContext(Dispatchers.IO) {
        val cacheKey = generateCacheKey(bitmap, language, manualCropOverride?.id, farmerDescription)

        // Return cached diagnosis if available and valid
        diagnosisCache.get(cacheKey)?.let { cachedResult ->
            return@withContext cachedResult.copy(
                imageUris = if (imageUris.isNotEmpty()) imageUris else cachedResult.imageUris
            )
        }

        val isOnline = !forceOffline && networkMonitor.checkCurrentOnlineStatus()
        val apiKey = GeminiClient.getApiKey()

        // 1. ONLINE SCENARIO (Gemini Multimodal AI)
        if (isOnline && apiKey.isNotBlank() && !apiKey.contains("PLACEHOLDER") && !apiKey.contains("MY_GEMINI")) {
            try {
                val onlineResult = performGeminiMultimodalAnalysis(
                    bitmap = bitmap,
                    imageUris = imageUris,
                    manualCropOverride = manualCropOverride,
                    language = language,
                    farmerDescription = farmerDescription
                )
                diagnosisCache.put(cacheKey, onlineResult)
                return@withContext onlineResult
            } catch (e: Exception) {
                // Graceful fallback to offline logic on any network or API issue
            }
        }

        // 2. OFFLINE / FALLBACK SCENARIO (Local On-Device Vision + Agricultural Database)
        val offlineResult = performOfflineAnalysis(
            bitmap = bitmap,
            imageUris = imageUris,
            manualCropOverride = manualCropOverride,
            language = language,
            farmerDescription = farmerDescription
        )
        diagnosisCache.put(cacheKey, offlineResult)
        offlineResult
    }

    private suspend fun performGeminiMultimodalAnalysis(
        bitmap: Bitmap,
        imageUris: List<String>,
        manualCropOverride: Crop?,
        language: Language,
        farmerDescription: String?
    ): DiagnosisResult {
        val base64Image = GeminiClient.bitmapToBase64(bitmap)
        val prompt = buildAnalysisPrompt(manualCropOverride, language, farmerDescription)

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt),
                        GeminiPart(
                            inlineData = GeminiInlineData(
                                mimeType = "image/jpeg",
                                data = base64Image
                            )
                        )
                    )
                )
            ),
            generationConfig = GeminiGenConfig(
                temperature = 0.2f, // low temperature for accurate agricultural guidance
                topP = 0.95f
            )
        )

        val response = GeminiClient.service.generateContent(
            apiKey = GeminiClient.getApiKey(),
            request = request
        )

        val responseText = response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text ?: throw IllegalStateException("Empty response from Gemini API")

        // Parse and correlate with local verified knowledge base
        return correlateGeminiResponseWithKnowledgeBase(
            geminiText = responseText,
            bitmap = bitmap,
            imageUris = imageUris,
            manualCropOverride = manualCropOverride,
            language = language,
            farmerDescription = farmerDescription
        )
    }

    private fun performOfflineAnalysis(
        bitmap: Bitmap,
        imageUris: List<String>,
        manualCropOverride: Crop?,
        language: Language,
        farmerDescription: String? = null
    ): DiagnosisResult {
        return CropAndDiseaseDetector.detectOffline(
            bitmap = bitmap,
            manualCropOverride = manualCropOverride,
            language = language,
            imageUris = imageUris,
            farmerDescription = farmerDescription
        )
    }

    private fun buildAnalysisPrompt(
        manualCrop: Crop?,
        language: Language,
        farmerDescription: String?
    ): String {
        val langInstruction = when (language) {
            Language.GUJARATI -> "Respond strictly in simple Gujarati (ગુજરાતી) suitable for an Indian farmer."
            Language.HINDI -> "Respond strictly in simple Hindi (हिन्दी) suitable for an Indian farmer."
            Language.ENGLISH -> "Respond strictly in simple, clear English suitable for an Indian farmer."
        }

        return buildString {
            append("You are an expert Indian agricultural scientist, agronomist, and crop doctor. ")
            append("Examine this photo with extreme botanical care.\n\n")

            append("CRITICAL STEP 0 - BOTANICAL IDENTIFICATION CHECK:\n")
            append("Determine first if this image contains a real agricultural plant, leaf, crop stem, or fruit. ")
            append("If this image is a human hand, human skin, person, body part, clothing, shoe, animal, vehicle, indoor room, floor, or non-botanical object: ")
            append("You MUST start your response with 'STATUS: NOT_A_CROP' on the first line. ")
            append("Then explain politely to the farmer in $langInstruction that the uploaded image is a hand or non-crop object, and instruct them to photograph an actual crop leaf in clear daylight.\n\n")

            append("STEP 1 - IF A CROP/PLANT IS PRESENT:\n")
            if (manualCrop != null) {
                append("The farmer specified that the crop is ${manualCrop.nameEn} (${manualCrop.scientificName}). ")
            } else {
                append("Identify the crop. If it is not in the common 10 catalog crops (Cotton, Tomato, Wheat, Groundnut, Rice, Chilli, Potato, Maize, Soybean, Onion), provide the real crop name or write 'Unknown Crop'. NEVER pretend it is a Tomato or Cotton if it is not.\n")
            }

            if (!farmerDescription.isNullOrBlank()) {
                append("Farmer's field note / symptoms described: \"$farmerDescription\". Use this observation for maximum precision.\n")
            }

            append(langInstruction)
            append("\nProvide a structured evaluation with:\n")
            append("1. Detected Crop name (e.g. 'Crop: ...' or 'Unknown Crop').\n")
            append("2. Detected Disease / Problem name (e.g. 'Problem: ...' or 'Healthy').\n")
            append("3. Confidence (High, Medium, or Low).\n")
            append("4. What is happening? (Detailed symptom explanation).\n")
            append("5. Why it happens? (Causes, fungi/bacteria/virus/pest, weather condition).\n")
            append("6. What to look for? (Specific visible signs on leaves/stems).\n")
            append("7. How to prevent it? (Cultural and agronomic practices).\n")
            append("8. Non-chemical control (Biological / IPM / Neem oil / traps / beneficial insects).\n")
            append("9. Chemical control: Name the recommended active ingredient (e.g., Imidacloprid, Mancozeb, Chlorantraniliprole), safe dilution rate per 15L pump, and Pre-Harvest Interval (PHI). DO NOT invent unverified dosages.")
        }
    }

    private fun correlateGeminiResponseWithKnowledgeBase(
        geminiText: String,
        bitmap: Bitmap,
        imageUris: List<String>,
        manualCropOverride: Crop?,
        language: Language,
        farmerDescription: String? = null
    ): DiagnosisResult {
        // Run local detector as baseline
        val localBaseline = CropAndDiseaseDetector.detectOffline(
            bitmap = bitmap,
            manualCropOverride = manualCropOverride,
            language = language,
            imageUris = imageUris,
            farmerDescription = farmerDescription
        )

        // 1. NON-CROP CHECK (e.g. User uploaded hand, furniture, animal, non-plant)
        val indicatesNonCrop = geminiText.contains("NOT_A_CROP", ignoreCase = true) ||
                geminiText.contains("not a crop", ignoreCase = true) ||
                geminiText.contains("not a plant", ignoreCase = true) ||
                geminiText.contains("human hand", ignoreCase = true) ||
                geminiText.contains("human skin", ignoreCase = true) ||
                geminiText.contains("मानव हाथ", ignoreCase = true) ||
                geminiText.contains("हाथ की फोटो", ignoreCase = true) ||
                geminiText.contains("પાક કે છોડ નથી", ignoreCase = true) ||
                geminiText.contains("કોઈ પાક નથી", ignoreCase = true) ||
                geminiText.contains("કોઈ છોડ નથી", ignoreCase = true) ||
                geminiText.contains("यह हाथ है", ignoreCase = true) ||
                localBaseline.healthStatus == HealthStatus.NOT_A_CROP

        if (indicatesNonCrop && manualCropOverride == null) {
            val nonCropTitle = when (language) {
                Language.GUJARATI -> "પાક કે છોડ નથી"
                Language.HINDI -> "फसल या पौधा नहीं है"
                Language.ENGLISH -> "Not a Crop / Plant"
            }
            val audioExp = when (language) {
                Language.GUJARATI -> "ફોટોમાં પાક કે છોડ ઓળખાયો નથી. કૃપા કરીને ખેતરમાં પાકના પાંદડાનો ફોટો પાડો."
                Language.HINDI -> "फोटो में फसल या पौधा नहीं पहचाना गया। कृपया फसल के पत्ते की फोटो लें।"
                Language.ENGLISH -> "No crop or plant detected in the photo. Please capture a clear crop leaf photo."
            }

            return DiagnosisResult(
                id = System.currentTimeMillis(),
                timestamp = System.currentTimeMillis(),
                imageUris = imageUris,
                crop = Crop.NON_CROP,
                plantPart = PlantPart.WHOLE_PLANT,
                healthStatus = HealthStatus.NOT_A_CROP,
                problemName = nonCropTitle,
                confidenceLevel = ConfidenceLevel.HIGH,
                confidencePercent = 95,
                whatIsHappening = geminiText.replace("STATUS: NOT_A_CROP", "").trim(),
                whyItHappens = when (language) {
                    Language.GUJARATI -> "રોગ નિદાન માટે વાસ્તવિક પાકના પાંદડા કે છોડની છબી જરૂરી છે."
                    Language.HINDI -> "रोग निदान के लिए वास्तविक फसल के पत्ते या पौधे की छवि आवश्यक है।"
                    Language.ENGLISH -> "Diagnosis requires a live agricultural leaf or plant specimen."
                },
                whatToLookFor = when (language) {
                    Language.GUJARATI -> "પાક પરના રોગિષ્ઠ પાન, જીવાત કે પીળાશ."
                    Language.HINDI -> "फसल पर रोगग्रस्त पत्ते, कीट अथवा पीलापन।"
                    Language.ENGLISH -> "Look for diseased leaves, pests, or discoloration on the real crop."
                },
                howToPrevent = when (language) {
                    Language.GUJARATI -> "કેમેરાને પાંદડાથી 15-20 સેમી દૂર રાખીને દિવસના અજવાળામાં ફોટો પાડો."
                    Language.HINDI -> "कैमरे को पत्ते से 15-20 सेमी दूर रखकर दिन के उजाले में फोटो लें।"
                    Language.ENGLISH -> "Hold phone steady 15-20 cm from the crop leaf in good daylight."
                },
                nonChemicalControl = "",
                verifiedPesticide = null,
                isOnlineAnalysis = true,
                audioExplanation = audioExp,
                notes = farmerDescription
            )
        }

        // 2. CHECK IF KNOWN CROP IN CATALOG
        val catalogCrop = manualCropOverride ?: AgriculturalKnowledgeBase.crops.find { crop ->
            geminiText.contains(crop.nameEn, ignoreCase = true) ||
                    geminiText.contains(crop.nameHi, ignoreCase = true) ||
                    geminiText.contains(crop.nameGu, ignoreCase = true)
        }

        // 3. UNKNOWN CROP HANDLING (Requirement 6)
        // If crop is not in database, do not show a fake crop name like Tomato!
        // Show disease name and "Unknown Crop", with full online solution!
        val (finalCrop, finalProblemName) = if (catalogCrop != null) {
            val matchedDisease = AgriculturalKnowledgeBase.diseasesAndPests.find { disease ->
                disease.cropId == catalogCrop.id && (
                        geminiText.contains(disease.nameEn, ignoreCase = true) ||
                                geminiText.contains(disease.nameHi, ignoreCase = true) ||
                                geminiText.contains(disease.nameGu, ignoreCase = true) ||
                                geminiText.contains(disease.id, ignoreCase = true)
                        )
            }
            val probName = matchedDisease?.getLocalizedName(language)
                ?: extractSection(geminiText, listOf("Problem", "Disease", "રોગ", "रोग"))
                ?: (catalogCrop.getLocalizedName(language) + " " + when (language) {
                    Language.GUJARATI -> "રોગ લક્ષણ"
                    Language.HINDI -> "रोग लक्षण"
                    Language.ENGLISH -> "Disease Symptoms"
                })
            Pair(catalogCrop, probName)
        } else {
            // Check if Gemini detected a specific named non-catalog crop or Unknown Crop
            val detectedCropName = extractCropNameFromText(geminiText, language)
            val cropEntity = if (detectedCropName != null && !detectedCropName.contains("unknown", ignoreCase = true)) {
                Crop(
                    id = "discovered_${detectedCropName.lowercase().replace(" ", "_")}",
                    nameEn = detectedCropName,
                    nameHi = detectedCropName,
                    nameGu = detectedCropName,
                    scientificName = "Plantae",
                    emoji = "🌱"
                ).also { AgriculturalKnowledgeBase.registerDynamicCrop(it) }
            } else {
                Crop.UNKNOWN_CROP
            }

            val extractedDisease = extractSection(geminiText, listOf("Problem", "Disease", "રોગ", "रोग"))
                ?: when (language) {
                    Language.GUJARATI -> "પાનનો રોગ / લક્ષણ"
                    Language.HINDI -> "पत्ती का रोग / लक्षण"
                    Language.ENGLISH -> "Foliar Disease / Symptoms"
                }

            Pair(cropEntity, extractedDisease)
        }

        // Match or build verified pesticide recommendation
        val catalogDisease = AgriculturalKnowledgeBase.diseasesAndPests.find {
            it.cropId == finalCrop.id && finalProblemName.contains(it.nameEn, ignoreCase = true)
        }
        val verifiedPesticide = catalogDisease?.let { AgriculturalKnowledgeBase.getPesticideForDisease(it.id) }

        val whatIsHappening = extractSection(geminiText, listOf("What is happening", "Symptoms", "લક્ષણો", "लक्षण"))
            ?: geminiText.take(450)
        val whyItHappens = extractSection(geminiText, listOf("Why it happens", "Causes", "કારણો", "कारण"))
            ?: when (language) {
                Language.GUJARATI -> "વાતાવરણમાં ભેજ, તાપમાન અને ફૂગ/જીવાતના કારણે."
                Language.HINDI -> "मौसम में नमी, तापमान और फफूंद/कीट के कारण।"
                Language.ENGLISH -> "Caused by pathogen pressure and environmental conditions."
            }
        val whatToLookFor = extractSection(geminiText, listOf("What to look for", "Signs", "શું જોવું", "क्या देखें"))
            ?: when (language) {
                Language.GUJARATI -> "પાન પર ડાઘ, પીળાશ અને સુકારો."
                Language.HINDI -> "पत्तियों पर धब्बे, पीलापन और झुलसा।"
                Language.ENGLISH -> "Leaf lesions, chlorosis, and margin necrosis."
            }
        val howToPrevent = extractSection(geminiText, listOf("How to prevent", "Prevention", "બચાવ", "बचाव"))
            ?: when (language) {
                Language.GUJARATI -> "ખેતરમાં ચોખ્ખાઈ રાખવી અને રોગિષ્ઠ ભાગ દૂર કરવો."
                Language.HINDI -> "खेत में स्वच्छता रखें और रोगग्रस्त भाग नष्ट करें।"
                Language.ENGLISH -> "Maintain field sanitation and balanced fertilization."
            }
        val nonChemical = extractSection(geminiText, listOf("Non-chemical", "Biological", "દેશી ઉપાય", "जैविक"))
            ?: when (language) {
                Language.GUJARATI -> "લીમડાનું તેલ 10,000 ppm (30 મિલી / 15 લિટર પંપ) છાંટવું."
                Language.HINDI -> "नीम तेल 10,000 ppm (30 मिली / 15 ली. पंप) का छिड़काव करें।"
                Language.ENGLISH -> "Spray 10,000 ppm Neem oil (30 ml / 15 L water)."
            }

        val audioExplanation = buildString {
            when (language) {
                Language.GUJARATI -> {
                    append("તમારા ${finalCrop.nameGu}ના પાકમાં ${finalProblemName}ના લક્ષણો જણાયા છે. ")
                    append(whatIsHappening.take(120)).append(". ")
                    append("બચાવ માટે: ").append(howToPrevent.take(120))
                }
                Language.HINDI -> {
                    append("आपकी ${finalCrop.nameHi} की फसल में ${finalProblemName} के लक्षण मिले हैं। ")
                    append(whatIsHappening.take(120)).append(". ")
                    append("बचाव के लिए: ").append(howToPrevent.take(120))
                }
                Language.ENGLISH -> {
                    append("In your ${finalCrop.nameEn} crop, we detected ${finalProblemName}. ")
                    append(whatIsHappening.take(120)).append(". ")
                    append("Prevention: ").append(howToPrevent.take(120))
                }
            }
        }

        return DiagnosisResult(
            id = System.currentTimeMillis(),
            timestamp = System.currentTimeMillis(),
            imageUris = imageUris,
            crop = finalCrop,
            plantPart = localBaseline.plantPart,
            healthStatus = if (geminiText.contains("Healthy", ignoreCase = true) || geminiText.contains("સ્વસ્થ", ignoreCase = true)) HealthStatus.HEALTHY else HealthStatus.DISEASE,
            problemName = finalProblemName,
            confidenceLevel = ConfidenceLevel.HIGH,
            confidencePercent = 92,
            whatIsHappening = whatIsHappening,
            whyItHappens = whyItHappens,
            whatToLookFor = whatToLookFor,
            howToPrevent = howToPrevent,
            nonChemicalControl = nonChemical,
            verifiedPesticide = verifiedPesticide,
            isOnlineAnalysis = true,
            audioExplanation = audioExplanation,
            notes = farmerDescription
        )
    }

    private fun extractSection(text: String, headers: List<String>): String? {
        val lines = text.lines()
        for (i in lines.indices) {
            val line = lines[i]
            for (header in headers) {
                if (line.contains(header, ignoreCase = true)) {
                    val contentAfterColon = line.substringAfter(":", "").substringAfter("-", "").trim()
                    if (contentAfterColon.length > 5) {
                        return contentAfterColon
                    }
                    if (i + 1 < lines.size) {
                        val nextLine = lines[i + 1].trim()
                        if (nextLine.isNotEmpty()) return nextLine
                    }
                }
            }
        }
        return null
    }

    private fun extractCropNameFromText(text: String, language: Language): String? {
        val lines = text.lines()
        for (line in lines) {
            if (line.contains("Crop", ignoreCase = true) || line.contains("પાક", ignoreCase = true) || line.contains("फसल", ignoreCase = true)) {
                val cleaned = line.substringAfter(":").trim().trim('*', '#', ' ')
                if (cleaned.isNotBlank() && cleaned.length < 40) {
                    return cleaned
                }
            }
        }
        return null
    }

    private fun generateCacheKey(bitmap: Bitmap, language: Language, manualCropId: String?, farmerDescription: String?): String {
        val w = bitmap.width
        val h = bitmap.height
        val centerPixel = if (w > 2 && h > 2) bitmap.getPixel(w / 2, h / 2) else 0
        return "${w}_${h}_${centerPixel}_${language.code}_${manualCropId ?: "auto"}_${farmerDescription?.hashCode() ?: 0}"
    }

    override suspend fun askFollowUp(
        question: String,
        currentDiagnosis: DiagnosisResult?,
        language: Language,
        attachedBitmap: Bitmap?
    ): String = withContext(Dispatchers.IO) {
        val isOnline = networkMonitor.checkCurrentOnlineStatus()
        val apiKey = GeminiClient.getApiKey()

        if (isOnline && apiKey.isNotBlank() && !apiKey.contains("PLACEHOLDER") && !apiKey.contains("MY_GEMINI")) {
            try {
                val prompt = buildString {
                    append("You are an agricultural advisor for an Indian farmer. ")
                    if (currentDiagnosis != null) {
                        append("Current diagnosed crop: ${currentDiagnosis.crop.nameEn}, problem: ${currentDiagnosis.problemName}. ")
                    }
                    val langDesc = when (language) {
                        Language.GUJARATI -> "Gujarati (ગુજરાતી)"
                        Language.HINDI -> "Hindi (हिन्दी)"
                        Language.ENGLISH -> "English"
                    }
                    append("Respond strictly in simple, supportive $langDesc using short bullet points. ")
                    append("Farmer's question: \"$question\". ")
                    append("Never invent unverified chemical rates.")
                }

                val parts = mutableListOf(GeminiPart(text = prompt))
                if (attachedBitmap != null) {
                    val base64 = GeminiClient.bitmapToBase64(attachedBitmap)
                    parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64)))
                }

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = parts))
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!reply.isNullOrBlank()) {
                    return@withContext reply
                }
            } catch (e: Exception) {
                // fallback to local chat engine on failure
            }
        }

        // Offline Fallback
        LocalChatEngine.generateReply(question, currentDiagnosis, language)
    }
}
