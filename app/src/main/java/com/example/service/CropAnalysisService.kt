package com.example.service

import android.graphics.Bitmap
import com.example.model.Crop
import com.example.model.DiagnosisResult
import com.example.model.Language

interface CropAnalysisService {
    /**
     * Analyzes a crop image to identify the crop and detect any disease or pest problems.
     * Operates seamlessly across both online (Gemini AI) and offline (local on-device vision + database) scenarios.
     */
    suspend fun analyzeCrop(
        bitmap: Bitmap,
        imageUris: List<String> = emptyList(),
        manualCropOverride: Crop? = null,
        language: Language = Language.GUJARATI,
        forceOffline: Boolean = false,
        farmerDescription: String? = null
    ): DiagnosisResult

    /**
     * Answers follow-up questions from the farmer regarding the diagnosed crop or treatment.
     */
    suspend fun askFollowUp(
        question: String,
        currentDiagnosis: DiagnosisResult?,
        language: Language = Language.GUJARATI,
        attachedBitmap: Bitmap? = null
    ): String
}
