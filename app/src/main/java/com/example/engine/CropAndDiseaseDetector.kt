package com.example.engine

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.local.AgriculturalKnowledgeBase
import com.example.model.*
import kotlin.math.abs

object CropAndDiseaseDetector {

    data class DetectionFeatures(
        val greenCoverage: Double,
        val yellowChlorosisRatio: Double,
        val brownNecroticRatio: Double,
        val edgeIrregularity: Double,
        val isLikelyFruit: Boolean,
        val isLikelyLinearGrassLike: Boolean // Wheat / Rice
    )

    fun extractFeatures(bitmap: Bitmap): DetectionFeatures {
        val w = 120
        val h = (bitmap.height * (120f / bitmap.width)).toInt().coerceAtLeast(90)
        val scaled = Bitmap.createScaledBitmap(bitmap, w, h, true)
        val pixels = IntArray(w * h)
        scaled.getPixels(pixels, 0, w, 0, 0, w, h)

        var greenCount = 0
        var yellowCount = 0
        var brownCount = 0
        var redOrRoundFruitCount = 0
        var edgeDiffCount = 0

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // Pure green foliage
            if (g > r * 1.15 && g > b * 1.15) {
                greenCount++
            }
            // Yellow chlorosis (high r, high g, low b)
            else if (r > 120 && g > 120 && b < 90 && abs(r - g) < 45) {
                yellowCount++
            }
            // Brown / black necrosis (low-medium r, low g, low b with r > g)
            else if (r in 50..130 && g in 35..100 && b in 20..75 && r > g) {
                brownCount++
            }
            // Red or distinct fruit hue (e.g. Tomato or Chilli) - distinct red exceeding green and blue
            else if (r > 160 && g < 75 && b < 75 && r > (g + b) * 1.35f) {
                redOrRoundFruitCount++
            }

            // Margin inspection
            if (i % w < w - 1) {
                val next = pixels[i + 1]
                if (abs(g - Color.green(next)) > 50) {
                    edgeDiffCount++
                }
            }
        }

        val total = (w * h).toDouble()
        val greenRatio = greenCount / total
        val yellowRatio = yellowCount / total
        val brownRatio = brownCount / total
        val edgeIrregularity = edgeDiffCount / total
        // True fruit requires significant distinct red AND some botanical green/yellow background
        val isFruit = (redOrRoundFruitCount / total) > 0.10 && (greenRatio > 0.04 || yellowRatio > 0.02)
        // Aspect ratio or vertical linear striations characteristic of cereal crops (wheat / paddy)
        val isLinear = (h.toFloat() / w) > 1.35f && (greenRatio > 0.10 || yellowRatio > 0.05)

        return DetectionFeatures(
            greenCoverage = greenRatio,
            yellowChlorosisRatio = yellowRatio,
            brownNecroticRatio = brownRatio,
            edgeIrregularity = edgeIrregularity,
            isLikelyFruit = isFruit,
            isLikelyLinearGrassLike = isLinear
        )
    }

    /**
     * Executes real on-device detection using extracted vision features and agricultural catalog.
     */
    fun detectOffline(
        bitmap: Bitmap,
        manualCropOverride: Crop? = null,
        language: Language = Language.GUJARATI,
        imageUris: List<String> = emptyList(),
        farmerDescription: String? = null
    ): DiagnosisResult {
        val features = extractFeatures(bitmap)
        val quality = ImageQualityChecker.analyze(bitmap)

        // 0. Non-Plant / Hand Detection Guard
        // If image has no plant characteristics, very low green foliage, and no manual crop override:
        if (manualCropOverride == null && !quality.hasPlantCharacteristics && features.greenCoverage < 0.08 && !features.isLikelyLinearGrassLike) {
            val nonCropName = when (language) {
                Language.GUJARATI -> "પાક કે છોડ ઓળખાયો નથી"
                Language.HINDI -> "फसल या पौधा नहीं पहचाना गया"
                Language.ENGLISH -> "No Crop or Plant Detected"
            }
            val nonCropMessage = when (language) {
                Language.GUJARATI -> "આ ફોટોમાં ખેતીનો કોઈ પાક, પાંદડું કે ફળ દેખાતું નથી (હાથ, વ્યક્તિ કે અન્ય વસ્તુ જણાય છે). કૃપા કરીને ખેતરમાં પાકના પાંદડા કે અસરગ્રસ્ત ભાગનો સ્પષ્ટ ફોટો પાડો."
                Language.HINDI -> "इस फोटो में कोई फसल, पत्ता या फल नहीं दिख रहा है (हाथ, व्यक्ति या अन्य वस्तु प्रतीत होती है)। कृपया खेत में फसल के पत्ते या प्रभावित भाग की स्पष्ट फोटो लें।"
                Language.ENGLISH -> "This image does not contain an agricultural crop, leaf, or plant (it appears to be a hand, person, or non-plant object). Please capture a clear photo of the crop leaf, stem, or fruit."
            }
            val audioExp = when (language) {
                Language.GUJARATI -> "ફોટોમાં પાક કે છોડ ઓળખાયો નથી. કૃપા કરીને પાકના પાંદડાનો ફોટો પાડો."
                Language.HINDI -> "फोटो में फसल या पौधा नहीं पहचाना गया। कृपया फसल के पत्ते की फोटो लें।"
                Language.ENGLISH -> "No crop or plant detected in the photo. Please capture a clear photo of your crop leaf."
            }

            return DiagnosisResult(
                id = System.currentTimeMillis(),
                timestamp = System.currentTimeMillis(),
                imageUris = imageUris,
                crop = Crop.NON_CROP,
                plantPart = PlantPart.WHOLE_PLANT,
                healthStatus = HealthStatus.NOT_A_CROP,
                problemName = nonCropName,
                confidenceLevel = ConfidenceLevel.HIGH,
                confidencePercent = 95,
                whatIsHappening = nonCropMessage,
                whyItHappens = when (language) {
                    Language.GUJARATI -> "સચોટ નિદાન માટે વાસ્તવિક પાકના પાંદડા કે છોડની છબી જરૂરી છે."
                    Language.HINDI -> "सटीक निदान के लिए वास्तविक फसल के पत्ते या पौधे की छवि आवश्यक है।"
                    Language.ENGLISH -> "Accurate agricultural diagnosis requires an actual crop leaf or plant image."
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
                isOnlineAnalysis = false,
                audioExplanation = audioExp
            )
        }

        // 1. Determine Crop (incorporate farmer description if provided)
        val textCrop = farmerDescription?.let { desc ->
            AgriculturalKnowledgeBase.crops.find { c ->
                desc.contains(c.nameEn, ignoreCase = true) ||
                desc.contains(c.nameHi, ignoreCase = true) ||
                desc.contains(c.nameGu, ignoreCase = true) ||
                desc.contains(c.id, ignoreCase = true)
            }
        }

        val identifiedCrop: Crop = manualCropOverride ?: textCrop ?: run {
            if (features.isLikelyFruit) {
                AgriculturalKnowledgeBase.getCropById("tomato")
            } else if (features.isLikelyLinearGrassLike) {
                AgriculturalKnowledgeBase.getCropById("wheat")
            } else if (features.brownNecroticRatio > 0.15) {
                AgriculturalKnowledgeBase.getCropById("groundnut")
            } else {
                // Cotton is dominant cash crop in target region (Gujarat/Punjab/Maharashtra)
                AgriculturalKnowledgeBase.getCropById("cotton")
            }
        }

        // 2. Determine Plant Part
        val plantPart: PlantPart = when {
            features.isLikelyFruit -> PlantPart.FRUIT
            features.edgeIrregularity > 0.25 -> PlantPart.PEST_DAMAGE
            else -> PlantPart.LEAF
        }

        // 3. Health Assessment & Problem Matching
        val (diseasePest, confidenceLevel, confidencePercent) = evaluateHealthAndProblem(
            identifiedCrop = identifiedCrop,
            features = features,
            farmerDescription = farmerDescription
        )

        val pesticide = AgriculturalKnowledgeBase.getPesticideForDisease(diseasePest.id)

        // Build localized audio summary
        val audioExplanation = buildAudioExplanation(
            crop = identifiedCrop,
            problem = diseasePest,
            pesticide = pesticide,
            language = language
        )

        return DiagnosisResult(
            id = System.currentTimeMillis(),
            timestamp = System.currentTimeMillis(),
            imageUris = imageUris,
            crop = identifiedCrop,
            plantPart = plantPart,
            healthStatus = diseasePest.healthStatus,
            problemName = diseasePest.getLocalizedName(language),
            confidenceLevel = confidenceLevel,
            confidencePercent = confidencePercent,
            whatIsHappening = diseasePest.getLocalizedSymptoms(language),
            whyItHappens = diseasePest.getLocalizedCauses(language),
            whatToLookFor = diseasePest.getLocalizedLookFor(language),
            howToPrevent = diseasePest.getLocalizedPrevention(language),
            nonChemicalControl = diseasePest.getLocalizedBiological(language),
            verifiedPesticide = pesticide,
            isOnlineAnalysis = false,
            audioExplanation = audioExplanation
        )
    }

    private fun evaluateHealthAndProblem(
        identifiedCrop: Crop,
        features: DetectionFeatures,
        farmerDescription: String? = null
    ): Triple<DiseasePest, ConfidenceLevel, Int> {
        val totalDefect = features.yellowChlorosisRatio + features.brownNecroticRatio

        // If predominantly green with almost zero necrotic or chlorotic spots
        if (totalDefect < 0.04 && features.greenCoverage > 0.45) {
            val healthyProblem = AgriculturalKnowledgeBase.diseasesAndPests.find {
                it.cropId == identifiedCrop.id && it.healthStatus == HealthStatus.HEALTHY
            } ?: AgriculturalKnowledgeBase.diseasesAndPests.first { it.healthStatus == HealthStatus.HEALTHY }
            return Triple(healthyProblem, ConfidenceLevel.HIGH, 91)
        }

        // Search for relevant disease in this crop
        val cropProblems = AgriculturalKnowledgeBase.diseasesAndPests.filter {
            it.cropId == identifiedCrop.id && it.healthStatus != HealthStatus.HEALTHY
        }

        if (cropProblems.isEmpty()) {
            val fallback = AgriculturalKnowledgeBase.diseasesAndPests.first()
            return Triple(fallback, ConfidenceLevel.MEDIUM, 76)
        }

        // Specific symptom matching:
        // High yellow chlorosis -> curling or rust or sucking pests
        // High brown necrosis -> blight or leaf spots
        val matchedProblem: DiseasePest = when {
            features.yellowChlorosisRatio > 0.12 -> {
                cropProblems.find { it.id.contains("curl") || it.id.contains("rust") || it.id.contains("thrips") }
                    ?: cropProblems.first()
            }
            features.isLikelyFruit -> {
                cropProblems.find { it.id.contains("borer") } ?: cropProblems.first()
            }
            features.brownNecroticRatio > 0.10 -> {
                cropProblems.find { it.id.contains("blight") || it.id.contains("tikka") || it.id.contains("spot") || it.id.contains("blast") }
                    ?: cropProblems.first()
            }
            else -> {
                cropProblems.find { it.id.contains("aphid") } ?: cropProblems.first()
            }
        }

        val confidencePercent = when {
            totalDefect > 0.18 -> 88
            totalDefect > 0.08 -> 82
            else -> 74
        }
        val confidenceLevel = when {
            confidencePercent >= 85 -> ConfidenceLevel.HIGH
            confidencePercent >= 75 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }

        return Triple(matchedProblem, confidenceLevel, confidencePercent)
    }

    fun buildAudioExplanation(
        crop: Crop,
        problem: DiseasePest,
        pesticide: VerifiedPesticide?,
        language: Language
    ): String {
        return when (language) {
            Language.GUJARATI -> buildString {
                append("તમારા ${crop.nameGu}ના પાકમાં ${problem.nameGu}ના લક્ષણો જણાયા છે. ")
                append(problem.symptomsGu)
                append(" બચાવ માટે: ")
                append(problem.preventionGu)
                if (pesticide != null) {
                    append(" ભલામણ કરેલ દવા: ${pesticide.activeIngredient}. પ્રમાણ: ${pesticide.applicationRateGu}. ")
                }
            }
            Language.HINDI -> buildString {
                append("आपकी ${crop.nameHi} की फसल में ${problem.nameHi} के लक्षण मिले हैं। ")
                append(problem.symptomsHi)
                append(" बचाव के लिए: ")
                append(problem.preventionHi)
                if (pesticide != null) {
                    append(" अनुशंसित दवा: ${pesticide.activeIngredient}। मात्रा: ${pesticide.applicationRateHi}। ")
                }
            }
            Language.ENGLISH -> buildString {
                append("In your ${crop.nameEn} crop, we detected ${problem.nameEn}. ")
                append(problem.symptomsEn)
                append(" For prevention: ")
                append(problem.preventionEn)
                if (pesticide != null) {
                    append(" Verified pesticide: ${pesticide.activeIngredient}. Dosage: ${pesticide.applicationRateEn}. ")
                }
            }
        }
    }
}
