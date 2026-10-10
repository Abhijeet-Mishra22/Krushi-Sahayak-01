package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Language
import com.example.model.Translations
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    language: Language,
    isOnline: Boolean,
    onLaunchCamera: () -> Unit,
    onGalleryImageSelected: (Uri) -> Unit,
    onDirectDiagnose: (List<Uri>, String?) -> Unit = { _, _ -> },
    onVoiceAsked: () -> Unit,
    onQuickQuestionSent: (String) -> Unit,
    onOpenPreventionLibrary: () -> Unit = {},
    onOpenPesticideCalculator: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var attachedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showRationaleDialog by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Voice recognition launcher for the embedded speech button
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                val updated = if (queryText.isBlank()) spoken else "$queryText, $spoken"
                queryText = updated
            }
        }
    }

    fun launchVoiceRecognitionInside() {
        val prompt = if (language == Language.GUJARATI) "પાકના લક્ષણો કે પ્રશ્ન બોલો..."
        else if (language == Language.HINDI) "फसल के लक्षण या सवाल बोलें..."
        else "Speak symptoms or question about your crop..."
        val intent = com.example.speech.SpeechRecognizerHelper.createSpeechIntent(language, prompt)
        try {
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            onVoiceAsked()
        }
    }

    // Direct image capture for attaching to the unified input
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val attachCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            attachedImages = (attachedImages + tempCameraUri!!).distinct().take(3)
        }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Attach to the unified card
            attachedImages = (attachedImages + uri).distinct().take(3)
        }
    }

    fun takePictureDirectly() {
        if (cameraPermissionState.status.isGranted) {
            try {
                val file = File(context.cacheDir, "crop_snap_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                tempCameraUri = uri
                attachCameraLauncher.launch(uri)
            } catch (_: Exception) {
                onLaunchCamera()
            }
        } else if (cameraPermissionState.status.shouldShowRationale) {
            showRationaleDialog = true
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    fun takePictureSafely() {
        if (cameraPermissionState.status.isGranted) {
            onLaunchCamera()
        } else if (cameraPermissionState.status.shouldShowRationale) {
            showRationaleDialog = true
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (language == Language.GUJARATI) "કેમેરા પરવાનગી જરૂરી છે"
                    else if (language == Language.HINDI) "कैमरा अनुमति आवश्यक है"
                    else "Camera Permission Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = if (language == Language.GUJARATI)
                        "પાકના રોગ અને જીવાતની સચોટ તપાસ માટે પાંદડાનો ફોટો પાડવા કેમેરાની પરવાનગી આપવી જરૂરી છે."
                    else if (language == Language.HINDI)
                        "फसल के रोग और कीट की सटीक जांच हेतु प्रभावित पत्ते की फोटो खींचने के लिए कैमरा अनुमति आवश्यक है।"
                    else
                        "Camera permission is required to capture photos of affected crop leaves for disease and pest diagnosis.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRationaleDialog = false
                        cameraPermissionState.launchPermissionRequest()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == Language.GUJARATI) "પરવાનગી આપો"
                        else if (language == Language.HINDI) "अनुमति दें"
                        else "Allow Permission",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text(
                        text = if (language == Language.GUJARATI) "રદ કરો"
                        else if (language == Language.HINDI) "रद्द करें"
                        else "Cancel"
                    )
                }
            }
        )
    }

    // Submit handler: Supports photo-only, text-only, voice-only, or ANY combination
    fun handleUnifiedSubmission() {
        val hasPhotos = attachedImages.isNotEmpty()
        val text = queryText.trim()
        val hasText = text.isNotBlank()

        if (hasPhotos) {
            // Diagnose with photos + optional text precision notes
            onDirectDiagnose(attachedImages, text.ifBlank { null })
            attachedImages = emptyList()
            queryText = ""
        } else if (hasText) {
            // Pure question chat
            onQuickQuestionSent(text)
            queryText = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // 1. BRAND HERO CARD
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFE8F5E9),
                                    Color(0xFFFFFFFF)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.krushi_sahayak_logo),
                                contentDescription = Translations.appTitle(language),
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(2.dp, Color(0xFF135D2B).copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = Translations.appTitle(language),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF135D2B),
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = Translations.appSubtitle(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Benefit Pills: Clean crisp typography, no duplicate emoji prefixes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF135D2B).copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (language == Language.GUJARATI) "10+ મુખ્ય પાક" else if (language == Language.HINDI) "10+ मुख्य फसलें" else "10+ Crops",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF135D2B),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE65100).copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (language == Language.GUJARATI) "100% ઑફલાઇન" else if (language == Language.HINDI) "100% ऑफलाइन" else "100% Offline",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF00695C).copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (language == Language.GUJARATI) "CIBRC પ્રમાણિત" else if (language == Language.HINDI) "CIBRC प्रमाणित" else "CIBRC Verified",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00695C),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. UNIFIED CROP DOCTOR SCANNER & QUERY SUITE
            // Farmers can use Camera, Gallery, Voice, Typing, or ANY combination or ALL of them!
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_unified_doctor_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF135D2B).copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = Color(0xFF135D2B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Translations.checkYourCrop(language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = Translations.checkYourCropGuide(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Combination Mode Badges: Illustrates photo + gallery + voice + combination
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (attachedImages.isNotEmpty()) Color(0xFF135D2B).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (attachedImages.isNotEmpty()) Color(0xFF135D2B) else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF135D2B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "ફોટો" else if (language == Language.HINDI) "फोटो" else "Photo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF135D2B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color(0xFF135D2B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "ગેલેરી" else if (language == Language.HINDI) "गैलरी" else "Gallery",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF135D2B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (queryText.isNotBlank()) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (queryText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "અવાજ" else if (language == Language.HINDI) "आवाज" else "Voice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE65100).copy(alpha = 0.12f),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "બધા ભેગા" else if (language == Language.HINDI) "सब मिलकर" else "Combine All",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Attached Images Row (if user selected from gallery or snapped photo for combination)
                    if (attachedImages.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            attachedImages.forEach { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.5.dp, Color(0xFF135D2B), RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Attached crop image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { attachedImages = attachedImages - uri },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            if (attachedImages.size < 3) {
                                OutlinedButton(
                                    onClick = {
                                        galleryPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(64.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Add another photo",
                                        tint = Color(0xFF135D2B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Multi-input Text Field: Type symptoms, questions, or field observations
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = {
                            Text(
                                text = if (language == Language.GUJARATI) "પાકના લક્ષણો લખો, અથવા ફોટો પાડો / ગેલેરીમાંથી પસંદ કરો / બોલો..."
                                else if (language == Language.HINDI) "फसल के लक्षण लिखें, या फोटो लें / गैलरी से चुनें / बोलें..."
                                else "Describe symptoms, or take photo / pick gallery / speak...",
                                fontSize = 13.5.sp,
                                lineHeight = 18.sp
                            )
                        },
                        trailingIcon = {
                            // Voice Dictation Button right inside the input field!
                            IconButton(
                                onClick = { launchVoiceRecognitionInside() },
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .testTag("home_box_mic_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_unified_input_field"),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Input Mode Buttons: [Take Photo] [Gallery] [Voice]
                    // Each button has EXACTLY ONE clean graphical icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Direct Camera Capture (Snaps and attaches)
                        OutlinedButton(
                            onClick = { takePictureDirectly() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_take_photo_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF135D2B).copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color(0xFF135D2B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Translations.takePhoto(language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF135D2B),
                                maxLines = 1
                            )
                        }

                        // Option 2: Gallery Picker
                        OutlinedButton(
                            onClick = {
                                galleryPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_gallery_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF135D2B).copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color(0xFF135D2B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Translations.chooseGallery(language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF135D2B),
                                maxLines = 1
                            )
                        }

                        // Option 3: Direct Voice
                        OutlinedButton(
                            onClick = { launchVoiceRecognitionInside() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_ask_by_voice_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Translations.askByVoice(language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PRIMARY SUBMIT ACTION: Diagnoses with photo, gallery, text, voice, or all combined!
                    // Uses CheckCircle or Send icon (NEVER duplicates Camera icon!)
                    val canSubmit = attachedImages.isNotEmpty() || queryText.isNotBlank()
                    Button(
                        onClick = {
                            if (canSubmit) {
                                handleUnifiedSubmission()
                            } else {
                                takePictureSafely()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("home_unified_submit_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF135D2B),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (canSubmit) Icons.AutoMirrored.Filled.Send else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (attachedImages.isNotEmpty() && queryText.isNotBlank()) {
                                if (language == Language.GUJARATI) "ફોટો અને વિગત સાથે સચોટ તપાસ કરો"
                                else if (language == Language.HINDI) "फोटो एवं विवरण के साथ सटीक जांच करें"
                                else "Diagnose Photo + Notes Combined"
                            } else if (attachedImages.isNotEmpty()) {
                                if (language == Language.GUJARATI) "જોડેલા ફોટાની તપાસ કરો"
                                else if (language == Language.HINDI) "जुड़ी हुई फोटो की जांच करें"
                                else "Analyze Attached Photos"
                            } else if (queryText.isNotBlank()) {
                                if (language == Language.GUJARATI) "પ્રશ્ન પૂછો / સલાહ મેળવો"
                                else if (language == Language.HINDI) "सवाल पूछें / सलाह पाएं"
                                else "Ask Question / Get Advice"
                            } else {
                                if (language == Language.GUJARATI) "પાક તપાસ શરૂ કરો"
                                else if (language == Language.HINDI) "फसल जांच शुरू करें"
                                else "Check Your Crop Now"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Optional live AI Camera Scanner shortcut
                    TextButton(
                        onClick = { takePictureSafely() },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (language == Language.GUJARATI) "લાઇવ કેમેરા સ્કેનર ખોલો ➔"
                            else if (language == Language.HINDI) "लाइव कैमरा स्कैनर खोलें ➔"
                            else "Open Live Camera Scanner ➔",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF135D2B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. FEATURED TOOLS SECTION: Dual Action Cards
            Text(
                text = if (language == Language.GUJARATI) "ખેડૂત સાધનો અને માર્ગદર્શિકા"
                else if (language == Language.HINDI) "कृषि उपकरण और गाइड"
                else "Agricultural Tools & Guides",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tool 1: Prevention Library
            Surface(
                onClick = onOpenPreventionLibrary,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_prevention_guide_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF135D2B).copy(alpha = 0.12f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF135D2B),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.GUJARATI) "રોગ નિવારણ પુસ્તિકા"
                                else if (language == Language.HINDI) "रोग रोकथाम गाइड"
                                else "Disease Prevention Library",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF198754).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "100% Offline",
                                    color = Color(0xFF198754),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "ઇન્ટરનેટ વગર પાક રોગના જૈવિક અને કુદરતી ઉપાયો જુઓ"
                            else if (language == Language.HINDI) "बिना इंटरनेट फसलों के जैविक व प्राकृतिक रोकथाम उपाय देखें"
                            else "Browse organic IPM and biological prevention advice offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool 2: Pesticide Dosage Calculator
            Surface(
                onClick = onOpenPesticideCalculator,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_pesticide_calculator_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE65100).copy(alpha = 0.12f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = Translations.pesticideCalculatorTitle(language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE65100).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "CIBRC",
                                    color = Color(0xFFE65100),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "વીઘા/એકર અને પંપ મુજબ દવા અને પાણીની ચોક્કસ ગણતરી"
                            else if (language == Language.HINDI) "बीघा/एकड़ और पंप अनुसार दवा एवं पानी की सटीक गणना"
                            else "Accurate chemical, water & pump calibration for your field",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. BOTTOM DOCKED BAR: Single Clean Mic + Text + Send
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice button
                IconButton(
                    onClick = onVoiceAsked,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("home_bottom_mic_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Query text field
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = {
                        Text(
                            text = Translations.askAboutCropPlaceholder(language),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_bottom_query_input"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                IconButton(
                    onClick = {
                        if (queryText.isNotBlank() || attachedImages.isNotEmpty()) {
                            handleUnifiedSubmission()
                        }
                    },
                    enabled = queryText.isNotBlank() || attachedImages.isNotEmpty(),
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (queryText.isNotBlank() || attachedImages.isNotEmpty()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .testTag("home_bottom_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (queryText.isNotBlank() || attachedImages.isNotEmpty()) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
