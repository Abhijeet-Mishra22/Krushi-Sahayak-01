package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DiagnosisEntity
import com.example.data.local.KisanDatabase
import com.example.data.local.PreventionTipEntity
import com.example.data.remote.NetworkMonitor
import com.example.data.repository.KisanRepository
import com.example.model.*
import com.example.speech.TtsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    CAMERA,
    HISTORY,
    PREVENTION_LIBRARY,
    PESTICIDE_CALCULATOR,
    QUALITY_WARNING,
    DIAGNOSIS_RESULT,
    CHAT
}

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val language: Language = Language.GUJARATI,
    val isOnline: Boolean = false,
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val imageQualityResult: ImageQualityResult? = null,
    val currentDiagnosis: DiagnosisResult? = null,
    val manualCropOverride: Crop? = null,
    val showCropSelector: Boolean = false,
    val chatMessages: List<ChatMessageEntity> = emptyList(),
    val isSpeaking: Boolean = false,
    val activeUtterance: String? = null,
    val isDrawerOpen: Boolean = false,
    val historyList: List<DiagnosisEntity> = emptyList(),
    val allPreventionTips: List<PreventionTipEntity> = emptyList(),
    val preventionSearchQuery: String = "",
    val selectedPreventionCrop: String? = null,
    val selectedPreventionCategory: String? = null,
    val showOnlyBookmarkedPrevention: Boolean = false,
    val calculatorCropId: String? = null,
    val calculatorPresetId: String? = null,
    val calculatorVerifiedPesticide: VerifiedPesticide? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = KisanDatabase.getDatabase(application)
    private val networkMonitor = NetworkMonitor(application)
    private val repository = KisanRepository(application, database, networkMonitor)
    val ttsManager = TtsManager(application)
    val speechHelper = com.example.speech.SpeechRecognizerHelper(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Observe network state
        viewModelScope.launch {
            repository.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }

        // Observe TTS state
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
            }
        }

        viewModelScope.launch {
            ttsManager.activeUtterance.collect { utterance ->
                _uiState.update { it.copy(activeUtterance = utterance) }
            }
        }

        // Observe history
        viewModelScope.launch {
            repository.history.collect { list ->
                _uiState.update { it.copy(historyList = list) }
            }
        }

        // Initialize and observe offline prevention tips library and cached advice
        viewModelScope.launch {
            repository.seedDefaultTipsIfEmpty()
            repository.seedDefaultCachedAdviceIfEmpty()
        }

        viewModelScope.launch {
            repository.allPreventionTips.collect { tips ->
                _uiState.update { it.copy(allPreventionTips = tips) }
            }
        }
    }

    fun openPreventionLibrary(filterCropId: String? = null) {
        ttsManager.stop()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PREVENTION_LIBRARY,
                selectedPreventionCrop = filterCropId,
                preventionSearchQuery = "",
                showOnlyBookmarkedPrevention = false,
                isDrawerOpen = false
            )
        }
    }

    fun setPreventionSearchQuery(query: String) {
        _uiState.update { it.copy(preventionSearchQuery = query) }
    }

    fun setPreventionFilterCrop(cropId: String?) {
        _uiState.update { it.copy(selectedPreventionCrop = cropId) }
    }

    fun setPreventionFilterCategory(category: String?) {
        _uiState.update { it.copy(selectedPreventionCategory = category) }
    }

    fun togglePreventionBookmarkOnly() {
        _uiState.update { it.copy(showOnlyBookmarkedPrevention = !it.showOnlyBookmarkedPrevention) }
    }

    fun toggleTipBookmark(tipId: Long, currentBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleTipBookmark(tipId, currentBookmarked)
        }
    }

    fun speakPreventionTip(tip: PreventionTipEntity) {
        val lang = _uiState.value.language
        val diseaseName = when (lang) {
            Language.GUJARATI -> tip.diseaseNameGu
            Language.HINDI -> tip.diseaseNameHi
            else -> tip.diseaseNameEn
        }
        val prevention = when (lang) {
            Language.GUJARATI -> tip.preventionGu
            Language.HINDI -> tip.preventionHi
            else -> tip.preventionEn
        }
        val bioIpm = when (lang) {
            Language.GUJARATI -> tip.biologicalIpmGu
            Language.HINDI -> tip.biologicalIpmHi
            else -> tip.biologicalIpmEn
        }
        val cultural = when (lang) {
            Language.GUJARATI -> tip.culturalPracticesGu
            Language.HINDI -> tip.culturalPracticesHi
            else -> tip.culturalPracticesEn
        }

        val speechText = "$diseaseName. $prevention. $bioIpm. $cultural"
        ttsManager.speak(speechText, lang, "tip_${tip.id}")
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun setLanguage(lang: Language) {
        _uiState.update { it.copy(language = lang) }
        ttsManager.stop()
    }

    fun toggleDrawer(open: Boolean? = null) {
        _uiState.update { it.copy(isDrawerOpen = open ?: !it.isDrawerOpen) }
    }

    fun startNewDiagnosis() {
        ttsManager.stop()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.HOME,
                selectedImageUris = emptyList(),
                imageQualityResult = null,
                currentDiagnosis = null,
                manualCropOverride = null,
                showCropSelector = false,
                isDrawerOpen = false
            )
        }
    }

    fun onImageSelected(uri: Uri) {
        val currentUris = _uiState.value.selectedImageUris
        val updatedUris = (currentUris + uri).distinct().take(3)
        _uiState.update { it.copy(selectedImageUris = updatedUris, isLoading = true, loadingMessage = "Checking image quality...") }

        viewModelScope.launch {
            val quality = repository.checkImageQuality(uri)
            if (!quality.isClear) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        imageQualityResult = quality,
                        currentScreen = AppScreen.QUALITY_WARNING
                    )
                }
            } else {
                _uiState.update { it.copy(imageQualityResult = quality) }
                performAnalysis(updatedUris)
            }
        }
    }

    fun onImagesAndNotesSubmitted(uris: List<Uri>, notes: String?) {
        if (uris.isEmpty()) return
        _uiState.update {
            it.copy(
                selectedImageUris = uris,
                isLoading = true,
                loadingMessage = "Checking image quality..."
            )
        }

        viewModelScope.launch {
            val quality = repository.checkImageQuality(uris.first())
            if (!quality.isClear) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        imageQualityResult = quality,
                        currentScreen = AppScreen.QUALITY_WARNING
                    )
                }
            } else {
                _uiState.update { it.copy(imageQualityResult = quality) }
                performAnalysis(uris, notes = notes)
            }
        }
    }

    fun proceedDespiteQuality() {
        val uris = _uiState.value.selectedImageUris
        if (uris.isNotEmpty()) {
            performAnalysis(uris)
        }
    }

    fun openCamera() {
        _uiState.update { it.copy(currentScreen = AppScreen.CAMERA) }
    }

    fun retakePhoto() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.CAMERA,
                selectedImageUris = emptyList(),
                imageQualityResult = null
            )
        }
    }

    fun setManualCrop(crop: Crop) {
        _uiState.update { it.copy(manualCropOverride = crop, showCropSelector = false) }
        val uris = _uiState.value.selectedImageUris
        if (uris.isNotEmpty()) {
            performAnalysis(uris, crop)
        }
    }

    fun toggleCropSelector(show: Boolean) {
        _uiState.update { it.copy(showCropSelector = show) }
    }

    private fun performAnalysis(uris: List<Uri>, manualCrop: Crop? = null, notes: String? = null) {
        val mainUri = uris.firstOrNull() ?: return
        val addUris = if (uris.size > 1) uris.drop(1) else emptyList()
        val lang = _uiState.value.language
        val crop = manualCrop ?: _uiState.value.manualCropOverride

        _uiState.update {
            it.copy(
                isLoading = true,
                loadingMessage = Translations.analyzingCrop(lang)
            )
        }

        viewModelScope.launch {
            try {
                val result = repository.diagnoseImage(
                    uri = mainUri,
                    additionalUris = addUris,
                    manualCropOverride = crop,
                    language = lang,
                    farmerDescription = notes
                )

                // Save to local Room history (and dynamic advice cache)
                repository.saveDiagnosis(result, lang)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentDiagnosis = result,
                        currentScreen = AppScreen.DIAGNOSIS_RESULT
                    )
                }

                // Load chat messages for this diagnosis
                loadChatMessages(result.id)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun loadDiagnosisFromHistory(entity: DiagnosisEntity) {
        viewModelScope.launch {
            val result = repository.getDiagnosisById(entity.id, _uiState.value.language)
            if (result != null) {
                _uiState.update {
                    it.copy(
                        currentDiagnosis = result,
                        currentScreen = AppScreen.DIAGNOSIS_RESULT,
                        isDrawerOpen = false
                    )
                }
                loadChatMessages(result.id)
            }
        }
    }

    private fun loadChatMessages(diagnosisId: Long) {
        viewModelScope.launch {
            repository.getChatMessages(diagnosisId).collect { msgs ->
                _uiState.update { it.copy(chatMessages = msgs) }
            }
        }
    }

    fun sendChatMessage(text: String, attachedUri: Uri? = null) {
        if (text.isBlank() && attachedUri == null) return
        val currentDiag = _uiState.value.currentDiagnosis
        val diagId = currentDiag?.id ?: 0L
        val lang = _uiState.value.language

        viewModelScope.launch {
            repository.sendChatMessage(
                diagnosisId = diagId,
                question = text,
                currentDiagnosis = currentDiag,
                language = lang,
                attachedImageUri = attachedUri?.toString()
            )
        }
    }

    fun openChat() {
        _uiState.update { it.copy(currentScreen = AppScreen.CHAT) }
    }

    fun openHistory() {
        _uiState.update { it.copy(currentScreen = AppScreen.HISTORY, isDrawerOpen = false) }
    }

    fun openPesticideCalculator(
        cropId: String? = null,
        presetId: String? = null,
        verifiedPesticide: VerifiedPesticide? = null
    ) {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PESTICIDE_CALCULATOR,
                isDrawerOpen = false,
                calculatorCropId = cropId ?: it.currentDiagnosis?.crop?.id,
                calculatorPresetId = presetId,
                calculatorVerifiedPesticide = verifiedPesticide ?: it.currentDiagnosis?.verifiedPesticide
            )
        }
    }

    fun navigateBack() {
        when (_uiState.value.currentScreen) {
            AppScreen.CAMERA -> _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            AppScreen.HISTORY -> _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            AppScreen.PREVENTION_LIBRARY -> _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            AppScreen.PESTICIDE_CALCULATOR -> {
                if (_uiState.value.currentDiagnosis != null) {
                    _uiState.update { it.copy(currentScreen = AppScreen.DIAGNOSIS_RESULT) }
                } else {
                    _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
                }
            }
            AppScreen.CHAT -> _uiState.update { it.copy(currentScreen = AppScreen.DIAGNOSIS_RESULT) }
            AppScreen.DIAGNOSIS_RESULT -> _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            AppScreen.QUALITY_WARNING -> _uiState.update { it.copy(currentScreen = AppScreen.CAMERA) }
            AppScreen.HOME -> { /* at root */ }
        }
        ttsManager.stop()
    }

    fun toggleSpeakDiagnosis() {
        if (_uiState.value.isSpeaking && _uiState.value.activeUtterance == "full_diagnosis") {
            ttsManager.stop()
        } else {
            val diag = _uiState.value.currentDiagnosis ?: return
            ttsManager.speak(diag.audioExplanation, _uiState.value.language, "full_diagnosis")
        }
    }

    fun speakSection(title: String, text: String) {
        if (_uiState.value.isSpeaking && _uiState.value.activeUtterance == title) {
            ttsManager.stop()
        } else {
            ttsManager.speak(text, _uiState.value.language, title)
        }
    }

    fun speakText(text: String) {
        if (_uiState.value.isSpeaking) {
            ttsManager.stop()
        } else {
            ttsManager.speak(text, _uiState.value.language)
        }
    }

    fun deleteDiagnosis(id: Long) {
        viewModelScope.launch {
            repository.deleteDiagnosis(id)
        }
    }

    fun addVoiceNoteToCurrentDiagnosis(spokenNote: String) {
        val diag = _uiState.value.currentDiagnosis ?: return
        val existingNotes = diag.notes ?: ""
        val updatedNotes = if (existingNotes.isBlank()) spokenNote else "$existingNotes\n• $spokenNote"
        val updatedDiag = diag.copy(notes = updatedNotes)
        _uiState.update { it.copy(currentDiagnosis = updatedDiag) }
        viewModelScope.launch {
            repository.updateDiagnosisNotes(diag.id, updatedNotes)
        }
    }

    fun updateDiagnosisNotes(notes: String) {
        val diag = _uiState.value.currentDiagnosis ?: return
        val updatedDiag = diag.copy(notes = notes)
        _uiState.update { it.copy(currentDiagnosis = updatedDiag) }
        viewModelScope.launch {
            repository.updateDiagnosisNotes(diag.id, notes)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
