package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.data.local.*
import com.example.data.remote.NetworkMonitor
import com.example.engine.ImageQualityChecker
import com.example.model.*
import com.example.service.CropAnalysisService
import com.example.service.GeminiCropAnalysisService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class KisanRepository(
    private val context: Context,
    private val database: KisanDatabase,
    private val networkMonitor: NetworkMonitor,
    private val analysisService: CropAnalysisService = GeminiCropAnalysisService(networkMonitor)
) {
    private val diagnosisDao = database.diagnosisDao()
    private val chatDao = database.chatDao()
    private val preventionTipDao = database.preventionTipDao()
    private val cachedAdviceDao = database.cachedAdviceDao()

    val history: Flow<List<DiagnosisEntity>> = diagnosisDao.getAllDiagnoses()
    val isOnline: Flow<Boolean> = networkMonitor.isOnline
    val allPreventionTips: Flow<List<PreventionTipEntity>> = preventionTipDao.getAllTips()
    val allCachedAdvice: Flow<List<CachedAgriculturalAdviceEntity>> = cachedAdviceDao.getAllAdvice()

    suspend fun checkImageQuality(uri: Uri): ImageQualityResult = withContext(Dispatchers.IO) {
        val bitmap = loadBitmapFromUri(uri)
            ?: return@withContext ImageQualityResult(
                isClear = false,
                blurScore = 0.0,
                brightnessScore = 0.0,
                hasPlantCharacteristics = false,
                errorMessageEn = "Could not load image.",
                errorMessageHi = "फोटो लोड नहीं हो सकी।",
                errorMessageGu = "ફોટો લોડ થઈ શકી નથી."
            )
        ImageQualityChecker.analyze(bitmap)
    }

    suspend fun diagnoseImage(
        uri: Uri,
        additionalUris: List<Uri> = emptyList(),
        manualCropOverride: Crop? = null,
        language: Language = Language.GUJARATI,
        forceOffline: Boolean = false,
        farmerDescription: String? = null
    ): DiagnosisResult = withContext(Dispatchers.IO) {
        val bitmap = loadBitmapFromUri(uri)
            ?: throw IllegalArgumentException("Failed to decode image bitmap")

        val allUriStrings = listOf(uri.toString()) + additionalUris.map { it.toString() }

        analysisService.analyzeCrop(
            bitmap = bitmap,
            imageUris = allUriStrings,
            manualCropOverride = manualCropOverride,
            language = language,
            forceOffline = forceOffline,
            farmerDescription = farmerDescription
        )
    }

    suspend fun saveDiagnosis(result: DiagnosisResult, language: Language): Long = withContext(Dispatchers.IO) {
        val entity = DiagnosisEntity(
            id = result.id,
            timestamp = result.timestamp,
            imageUri = result.imageUris.firstOrNull() ?: "",
            cropId = result.crop.id,
            cropName = result.crop.getLocalizedName(language),
            problemName = result.problemName,
            healthStatus = result.healthStatus.name,
            confidenceLevel = result.confidenceLevel.name,
            confidencePercent = result.confidencePercent,
            isOnline = result.isOnlineAnalysis,
            languageCode = language.code,
            whatIsHappening = result.whatIsHappening,
            whyItHappens = result.whyItHappens,
            whatToLookFor = result.whatToLookFor,
            howToPrevent = result.howToPrevent,
            nonChemicalControl = result.nonChemicalControl,
            pesticideActiveIngredient = result.verifiedPesticide?.activeIngredient,
            pesticideDilution = result.verifiedPesticide?.getLocalizedApplicationRate(language),
            pesticidePhi = result.verifiedPesticide?.preHarvestIntervalDays,
            pesticideAuthority = result.verifiedPesticide?.verifiedAuthority,
            audioExplanation = result.audioExplanation,
            notes = result.notes
        )
        val insertedId = diagnosisDao.insertDiagnosis(entity)

        // Requirement 6: Add discovered crops and advice to Room database
        if (result.crop.id != "non_crop") {
            try {
                val adviceEntity = CachedAgriculturalAdviceEntity(
                    cropId = result.crop.id,
                    cropName = result.crop.getLocalizedName(language),
                    diseaseOrPestId = result.problemName,
                    topic = "${result.crop.getLocalizedName(language)} - ${result.problemName}",
                    adviceCategory = "TREATMENT",
                    titleEn = "${result.crop.nameEn}: ${result.problemName}",
                    titleHi = "${result.crop.nameHi}: ${result.problemName}",
                    titleGu = "${result.crop.nameGu}: ${result.problemName}",
                    contentEn = "${result.whatIsHappening}\nPrevention: ${result.howToPrevent}\nBiological: ${result.nonChemicalControl}",
                    contentHi = "${result.whatIsHappening}\nरोकथाम: ${result.howToPrevent}\nजैविक: ${result.nonChemicalControl}",
                    contentGu = "${result.whatIsHappening}\nબચાવ: ${result.howToPrevent}\nદેશી ઉપાય: ${result.nonChemicalControl}",
                    expertSource = if (result.isOnlineAnalysis) "Krushi Sahayak AI / CIBRC" else "Local Agricultural Knowledge Base",
                    cachedTimestamp = System.currentTimeMillis()
                )
                cachedAdviceDao.insertAdvice(adviceEntity)

                val tipEntity = PreventionTipEntity(
                    cropId = result.crop.id,
                    cropNameEn = result.crop.nameEn,
                    cropNameHi = result.crop.nameHi,
                    cropNameGu = result.crop.nameGu,
                    diseaseId = result.problemName.lowercase().replace(" ", "_"),
                    diseaseNameEn = result.problemName,
                    diseaseNameHi = result.problemName,
                    diseaseNameGu = result.problemName,
                    category = "CULTURAL",
                    symptomsSummaryEn = result.whatIsHappening,
                    symptomsSummaryHi = result.whatIsHappening,
                    symptomsSummaryGu = result.whatIsHappening,
                    preventionEn = result.howToPrevent,
                    preventionHi = result.howToPrevent,
                    preventionGu = result.howToPrevent,
                    biologicalIpmEn = result.nonChemicalControl,
                    biologicalIpmHi = result.nonChemicalControl,
                    biologicalIpmGu = result.nonChemicalControl,
                    culturalPracticesEn = result.howToPrevent,
                    culturalPracticesHi = result.howToPrevent,
                    culturalPracticesGu = result.howToPrevent
                )
                preventionTipDao.insertTip(tipEntity)
            } catch (_: Exception) {}
        }

        insertedId
    }

    suspend fun updateDiagnosisNotes(id: Long, notes: String) = withContext(Dispatchers.IO) {
        diagnosisDao.updateNotes(id, notes)
    }

    suspend fun getDiagnosisById(id: Long, language: Language): DiagnosisResult? = withContext(Dispatchers.IO) {
        val entity = diagnosisDao.getDiagnosisById(id) ?: return@withContext null
        val crop = if (entity.cropId == "non_crop") Crop.NON_CROP
        else if (entity.cropId == "unknown_crop") Crop.UNKNOWN_CROP
        else AgriculturalKnowledgeBase.getCropById(entity.cropId)

        val pesticide = if (!entity.pesticideActiveIngredient.isNullOrBlank()) {
            VerifiedPesticide(
                id = "${entity.cropId}_stored_pesticide",
                cropId = entity.cropId,
                diseasePestId = entity.problemName,
                targetNameEn = entity.problemName,
                activeIngredient = entity.pesticideActiveIngredient,
                formulation = "EC / WP / SC",
                applicationRateEn = entity.pesticideDilution ?: "Dilute as per label",
                applicationRateHi = entity.pesticideDilution ?: "लेबल अनुसार प्रयोग करें",
                applicationRateGu = entity.pesticideDilution ?: "લેબલ મુજબ પ્રમાણ વાપરવું",
                preHarvestIntervalDays = entity.pesticidePhi ?: "7-14",
                verifiedAuthority = entity.pesticideAuthority ?: "CIBRC Verified",
                safetyWarningsEn = listOf("Wear protective gloves and mask.", "Spray in morning/evening."),
                safetyWarningsHi = listOf("दवा छिड़कते समय मास्क और दस्ताने पहनें।", "सुबह या शाम को छिड़काव करें।"),
                safetyWarningsGu = listOf("છંટકાવ વખતે માસ્ક અને મોજા પહેરવા.", "સવારે અથવા સાંજે છંટકાવ કરવો.")
            )
        } else {
            val disease = AgriculturalKnowledgeBase.diseasesAndPests.find { it.cropId == entity.cropId }
            AgriculturalKnowledgeBase.getPesticideForDisease(disease?.id ?: "")
        }

        DiagnosisResult(
            id = entity.id,
            timestamp = entity.timestamp,
            imageUris = listOf(entity.imageUri).filter { it.isNotBlank() },
            crop = crop,
            plantPart = PlantPart.LEAF,
            healthStatus = try { HealthStatus.valueOf(entity.healthStatus) } catch (e: Exception) { HealthStatus.DISEASE },
            problemName = entity.problemName,
            confidenceLevel = try { ConfidenceLevel.valueOf(entity.confidenceLevel) } catch (e: Exception) { ConfidenceLevel.HIGH },
            confidencePercent = entity.confidencePercent,
            whatIsHappening = entity.whatIsHappening,
            whyItHappens = entity.whyItHappens,
            whatToLookFor = entity.whatToLookFor,
            howToPrevent = entity.howToPrevent,
            nonChemicalControl = entity.nonChemicalControl,
            verifiedPesticide = pesticide,
            isOnlineAnalysis = entity.isOnline,
            audioExplanation = entity.audioExplanation,
            notes = entity.notes
        )
    }

    fun getChatMessages(diagnosisId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForDiagnosis(diagnosisId)
    }

    suspend fun sendChatMessage(
        diagnosisId: Long,
        question: String,
        currentDiagnosis: DiagnosisResult?,
        language: Language,
        attachedImageUri: String? = null
    ): String = withContext(Dispatchers.IO) {
        // Save user message
        chatDao.insertMessage(
            ChatMessageEntity(
                diagnosisId = diagnosisId,
                text = question,
                isUser = true,
                attachedImageUri = attachedImageUri
            )
        )

        val attachedBitmap = attachedImageUri?.let { uriStr ->
            try {
                loadBitmapFromUri(Uri.parse(uriStr))
            } catch (e: Exception) {
                null
            }
        }

        val replyText = analysisService.askFollowUp(
            question = question,
            currentDiagnosis = currentDiagnosis,
            language = language,
            attachedBitmap = attachedBitmap
        )

        // Save assistant reply
        chatDao.insertMessage(
            ChatMessageEntity(
                diagnosisId = diagnosisId,
                text = replyText,
                isUser = false
            )
        )

        replyText
    }

    suspend fun deleteDiagnosis(id: Long) = withContext(Dispatchers.IO) {
        diagnosisDao.deleteDiagnosisById(id)
        chatDao.deleteMessagesForDiagnosis(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        diagnosisDao.clearAll()
    }

    fun getPreventionTipsByCrop(cropId: String): Flow<List<PreventionTipEntity>> {
        return preventionTipDao.getTipsByCrop(cropId)
    }

    fun searchPreventionTips(query: String): Flow<List<PreventionTipEntity>> {
        return preventionTipDao.searchTips(query)
    }

    fun getBookmarkedPreventionTips(): Flow<List<PreventionTipEntity>> {
        return preventionTipDao.getBookmarkedTips()
    }

    suspend fun toggleTipBookmark(id: Long, currentBookmarked: Boolean) = withContext(Dispatchers.IO) {
        preventionTipDao.updateBookmark(id, !currentBookmarked)
    }

    suspend fun seedDefaultTipsIfEmpty() = withContext(Dispatchers.IO) {
        val count = preventionTipDao.getCount()
        if (count == 0) {
            val defaults = AgriculturalKnowledgeBase.generateDefaultPreventionTips()
            preventionTipDao.insertTips(defaults)
        }
    }

    // Cached Agricultural Advice Operations
    fun getCachedAdviceByCrop(cropId: String): Flow<List<CachedAgriculturalAdviceEntity>> {
        return cachedAdviceDao.getAdviceByCrop(cropId)
    }

    fun getCachedAdviceByCategory(category: String): Flow<List<CachedAgriculturalAdviceEntity>> {
        return cachedAdviceDao.getAdviceByCategory(category)
    }

    fun searchCachedAdvice(query: String): Flow<List<CachedAgriculturalAdviceEntity>> {
        return cachedAdviceDao.searchAdvice(query)
    }

    fun getBookmarkedCachedAdvice(): Flow<List<CachedAgriculturalAdviceEntity>> {
        return cachedAdviceDao.getBookmarkedAdvice()
    }

    suspend fun cacheAdvice(advice: CachedAgriculturalAdviceEntity): Long = withContext(Dispatchers.IO) {
        cachedAdviceDao.insertAdvice(advice)
    }

    suspend fun toggleAdviceBookmark(id: Long, currentBookmarked: Boolean) = withContext(Dispatchers.IO) {
        cachedAdviceDao.updateBookmark(id, !currentBookmarked)
    }

    suspend fun seedDefaultCachedAdviceIfEmpty() = withContext(Dispatchers.IO) {
        val count = cachedAdviceDao.getAdviceCount()
        if (count == 0) {
            val defaults = AgriculturalKnowledgeBase.generateDefaultCachedAdvice()
            cachedAdviceDao.insertAll(defaults)
        }
    }

    // Detection Result Operations
    fun getBookmarkedDiagnoses(): Flow<List<DiagnosisEntity>> {
        return diagnosisDao.getBookmarkedDiagnoses()
    }

    suspend fun toggleDiagnosisBookmark(id: Long, currentBookmarked: Boolean) = withContext(Dispatchers.IO) {
        diagnosisDao.updateBookmark(id, !currentBookmarked)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val input: InputStream? = context.contentResolver.openInputStream(uri)
            val bmp = BitmapFactory.decodeStream(input)
            input?.close()
            bmp
        } catch (e: Exception) {
            null
        }
    }

    fun saveTemporaryImage(bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "crop_photo_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        return Uri.fromFile(file)
    }
}
