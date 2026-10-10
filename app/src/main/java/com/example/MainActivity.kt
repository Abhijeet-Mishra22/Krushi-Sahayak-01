package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Language
import com.example.model.VerifiedPesticide
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.KisanTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KisanTheme {
                KisanApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KisanApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(
        initialValue = if (uiState.isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )

    // Speech to text launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendChatMessage(spokenText)
                viewModel.openChat()
            }
        }
    }

    fun launchVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val langTag = when (uiState.language) {
                Language.GUJARATI -> "gu-IN"
                Language.HINDI -> "hi-IN"
                Language.ENGLISH -> "en-IN"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak about your crop / પાક વિશે બોલો...")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            // Speech recognizer not installed on emulator
        }
    }

    // Sync drawerState with uiState.isDrawerOpen
    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen && drawerState.isClosed) {
            drawerState.open()
        } else if (!uiState.isDrawerOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen != uiState.isDrawerOpen) {
            viewModel.toggleDrawer(drawerState.isOpen)
        }
    }

    // Handle Back Press
    BackHandler(enabled = uiState.currentScreen != AppScreen.HOME || drawerState.isOpen) {
        if (drawerState.isOpen) {
            viewModel.toggleDrawer(false)
        } else {
            viewModel.navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    historyList = uiState.historyList,
                    language = uiState.language,
                    onNewDiagnosis = {
                        viewModel.startNewDiagnosis()
                    },
                    onItemClick = { entity ->
                        viewModel.loadDiagnosisFromHistory(entity)
                    },
                    onViewAllHistory = {
                        viewModel.openHistory()
                    },
                    onOpenPreventionLibrary = {
                        viewModel.openPreventionLibrary()
                    },
                    onOpenPesticideCalculator = {
                        viewModel.openPesticideCalculator()
                    },
                    onDeleteItem = { id ->
                        viewModel.deleteDiagnosis(id)
                    },
                    onClearHistory = {
                        viewModel.clearAllHistory()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (uiState.currentScreen == AppScreen.HOME) {
                    TopBar(
                        language = uiState.language,
                        isOnline = uiState.isOnline,
                        onMenuClick = { viewModel.toggleDrawer(true) },
                        onLanguageSelected = { viewModel.setLanguage(it) }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
            ) {
                when (uiState.currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            language = uiState.language,
                            isOnline = uiState.isOnline,
                            onLaunchCamera = { viewModel.openCamera() },
                            onGalleryImageSelected = { uri -> viewModel.onImageSelected(uri) },
                            onDirectDiagnose = { uris, notes ->
                                viewModel.onImagesAndNotesSubmitted(uris, notes)
                            },
                            onVoiceAsked = { launchVoiceRecognition() },
                            onQuickQuestionSent = { question ->
                                viewModel.sendChatMessage(question)
                                viewModel.openChat()
                            },
                            onOpenPreventionLibrary = { viewModel.openPreventionLibrary() },
                            onOpenPesticideCalculator = { viewModel.openPesticideCalculator() }
                        )
                    }

                    AppScreen.CAMERA -> {
                        CameraCaptureScreen(
                            language = uiState.language,
                            onImagesAndNotesSubmitted = { uris, notes ->
                                viewModel.onImagesAndNotesSubmitted(uris, notes)
                            },
                            onGallerySelected = { uri -> viewModel.onImageSelected(uri) },
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.HISTORY -> {
                        HistoryScreen(
                            historyList = uiState.historyList,
                            language = uiState.language,
                            onBackClick = { viewModel.navigateBack() },
                            onItemClick = { entity -> viewModel.loadDiagnosisFromHistory(entity) },
                            onDeleteItem = { id -> viewModel.deleteDiagnosis(id) },
                            onClearAllHistory = { viewModel.clearAllHistory() }
                        )
                    }

                    AppScreen.PREVENTION_LIBRARY -> {
                        PreventionLibraryScreen(
                            tipsList = uiState.allPreventionTips,
                            language = uiState.language,
                            searchQuery = uiState.preventionSearchQuery,
                            selectedCropId = uiState.selectedPreventionCrop,
                            selectedCategory = uiState.selectedPreventionCategory,
                            showOnlyBookmarked = uiState.showOnlyBookmarkedPrevention,
                            isSpeaking = uiState.isSpeaking,
                            activeUtterance = uiState.activeUtterance,
                            onSearchQueryChange = { query -> viewModel.setPreventionSearchQuery(query) },
                            onCropSelect = { cropId -> viewModel.setPreventionFilterCrop(cropId) },
                            onCategorySelect = { category -> viewModel.setPreventionFilterCategory(category) },
                            onToggleBookmarkOnly = { viewModel.togglePreventionBookmarkOnly() },
                            onToggleBookmark = { id, current -> viewModel.toggleTipBookmark(id, current) },
                            onSpeakTip = { tip -> viewModel.speakPreventionTip(tip) },
                            onStopSpeaking = { viewModel.stopSpeaking() },
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.PESTICIDE_CALCULATOR -> {
                        PesticideCalculatorScreen(
                            language = uiState.language,
                            initialCropId = uiState.calculatorCropId,
                            initialPresetId = uiState.calculatorPresetId,
                            initialVerifiedPesticide = uiState.calculatorVerifiedPesticide,
                            isSpeaking = uiState.isSpeaking,
                            onBackClick = { viewModel.navigateBack() },
                            onSpeakText = { text -> viewModel.speakText(text) },
                            onStopSpeaking = { viewModel.stopSpeaking() }
                        )
                    }

                    AppScreen.QUALITY_WARNING -> {
                        uiState.imageQualityResult?.let { quality ->
                            QualityWarningDialog(
                                qualityResult = quality,
                                language = uiState.language,
                                onRetake = { viewModel.retakePhoto() },
                                onContinue = { viewModel.proceedDespiteQuality() }
                            )
                        }
                    }

                    AppScreen.DIAGNOSIS_RESULT -> {
                        uiState.currentDiagnosis?.let { diag ->
                            DiagnosisResultScreen(
                                diagnosis = diag,
                                language = uiState.language,
                                isSpeaking = uiState.isSpeaking,
                                activeUtterance = uiState.activeUtterance,
                                onBackClick = { viewModel.navigateBack() },
                                onListenClick = { viewModel.toggleSpeakDiagnosis() },
                                onSpeakSection = { title, content -> viewModel.speakSection(title, content) },
                                onSaveVoiceNote = { spokenNote -> viewModel.addVoiceNoteToCurrentDiagnosis(spokenNote) },
                                onViewPreventionLibrary = { cropId -> viewModel.openPreventionLibrary(cropId) },
                                onOpenDosageCalculator = { pesticide ->
                                    viewModel.openPesticideCalculator(
                                        cropId = diag.crop.id,
                                        verifiedPesticide = pesticide
                                    )
                                },
                                onAskMoreClick = { viewModel.openChat() },
                                onChangeCropClick = { viewModel.toggleCropSelector(true) }
                            )
                        }
                    }

                    AppScreen.CHAT -> {
                        ChatScreen(
                            diagnosis = uiState.currentDiagnosis,
                            chatMessages = uiState.chatMessages,
                            language = uiState.language,
                            onBackClick = { viewModel.navigateBack() },
                            onSendMessage = { text, uri -> viewModel.sendChatMessage(text, uri) },
                            onSpeakMessage = { text -> viewModel.speakText(text) },
                            onVoiceInput = { launchVoiceRecognition() }
                        )
                    }
                }

                // Loading Overlay
                if (uiState.isLoading) {
                    Surface(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(54.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = uiState.loadingMessage,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (uiState.isOnline) "🟢 Analyzing with AI & Verified Database..."
                                else "🟠 Offline On-Device Analysis...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Manual Crop Picker Bottom Sheet
                if (uiState.showCropSelector) {
                    ManualCropPickerSheet(
                        language = uiState.language,
                        selectedCrop = uiState.currentDiagnosis?.crop ?: uiState.manualCropOverride,
                        onCropSelected = { crop -> viewModel.setManualCrop(crop) },
                        onDismiss = { viewModel.toggleCropSelector(false) }
                    )
                }
            }
        }
    }
}
