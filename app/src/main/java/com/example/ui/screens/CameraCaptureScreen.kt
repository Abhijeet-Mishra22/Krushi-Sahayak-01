package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.model.Language
import com.example.model.Translations
import com.example.speech.SpeechRecognizerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Framing modes tailored for agricultural photography
 */
enum class CropFramingMode {
    LEAF,       // Single leaf close-up (lesions, rust, blight)
    PLANT,      // Whole plant / crop stand (wilt, canopy, stunting)
    PEST_FRUIT  // Macro fruit rot or insect pests
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureScreen(
    language: Language,
    onImagesAndNotesSubmitted: (List<Uri>, String?) -> Unit,
    onGallerySelected: (Uri) -> Unit,
    onBackClick: () -> Unit,
    onImageCaptured: ((Uri) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    var capturedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isReviewing by remember { mutableStateOf(false) }
    var farmerNotesText by remember { mutableStateOf("") }

    // Intercept back key when reviewing
    BackHandler(enabled = isReviewing) {
        isReviewing = false
    }

    // Gallery picker shortcut
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            capturedPhotos = (capturedPhotos + uri).distinct().take(3)
            isReviewing = true
            onGallerySelected(uri)
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        // Safe Accompanist permission request flow
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("camera_perm_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = when (language) {
                        Language.GUJARATI -> "કેમેરા પરવાનગી જરૂરી છે"
                        Language.HINDI -> "कैमरा अनुमति आवश्यक है"
                        else -> "Camera Permission Required"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (language) {
                        Language.GUJARATI -> "પાંદડાની તસવીર ખેંચી પાક રોગનું સચોટ નિદાન કરવા માટે કેમેરાની પરવાનગી આપો."
                        Language.HINDI -> "पत्ते की फोटो खींचकर फसल रोग की सटीक पहचान करने के लिए कैमरा एक्सेस की अनुमति दें।"
                        else -> "To capture leaves and diagnose crop diseases accurately, please grant camera access."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("camera_grant_perm_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = when (language) {
                            Language.GUJARATI -> "પરવાનગી આપો"
                            Language.HINDI -> "अनुमति दें"
                            else -> "Grant Camera Permission"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("camera_perm_pick_gallery_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            Language.GUJARATI -> "ગેલેરીમાંથી પસંદ કરો"
                            Language.HINDI -> "गैलरी से चुनें"
                            else -> "Choose from Gallery"
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val haptic = LocalHapticFeedback.current

    var isCapturing by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }

    // Overlay customization state
    var framingMode by remember { mutableStateOf(CropFramingMode.LEAF) }
    var showGrid by remember { mutableStateOf(false) }
    var showTipsDialog by remember { mutableStateOf(false) }

    // Tap to focus state
    var tapFocusPoint by remember { mutableStateOf<Offset?>(null) }
    val focusPulseAnim = remember { Animatable(0f) }

    // Sensor-based phone alignment tracking (spirit level & stability)
    var rollAngle by remember { mutableFloatStateOf(0f) }
    var pitchAngle by remember { mutableFloatStateOf(45f) }
    var isSteady by remember { mutableStateOf(true) }
    var wasAligned by remember { mutableStateOf(false) }

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    }

    DisposableEffect(sensorManager) {
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            private var lastX = 0f
            private var lastY = 0f
            private var lastZ = 0f

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]

                // Roll (tilt left/right) in degrees: 0° is perfectly level
                val roll = Math.toDegrees(
                    atan2(-ax.toDouble(), sqrt((ay * ay + az * az).toDouble()))
                ).toFloat()

                // Pitch (forward/back tilt) in degrees
                val pitch = Math.toDegrees(
                    atan2(ay.toDouble(), az.toDouble())
                ).toFloat()

                rollAngle = roll
                pitchAngle = pitch

                // Stability detection: delta of acceleration vector
                val delta = abs(ax - lastX) + abs(ay - lastY) + abs(az - lastZ)
                isSteady = delta < 1.1f

                lastX = ax
                lastY = ay
                lastZ = az
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager?.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Determine if phone alignment is optimal for disease photography
    val isAngleLevel = abs(rollAngle) <= 6.5f
    val isPitchComfortable = pitchAngle in 15f..80f
    val isPerfectAlignment = isAngleLevel && isSteady

    // Trigger subtle haptic click when farmer successfully levels phone into optimal alignment
    LaunchedEffect(isPerfectAlignment) {
        if (isPerfectAlignment && !wasAligned) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        wasAligned = isPerfectAlignment
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()
    }

    LaunchedEffect(flashMode) {
        imageCapture.flashMode = flashMode
    }

    // Flash animation on shutter press
    val shutterAlpha = remember { Animatable(0f) }

    fun capturePhoto() {
        if (isCapturing) return
        isCapturing = true

        val photoFile = File(context.cacheDir, "crop_camerax_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    isCapturing = false
                    val savedUri = Uri.fromFile(photoFile)
                    capturedPhotos = (capturedPhotos + savedUri).distinct().take(3)
                    isReviewing = true
                    onImageCaptured?.invoke(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                }
            }
        )
    }

    // Tap to focus action
    fun onPreviewTapped(offset: Offset) {
        tapFocusPoint = offset
        val preview = previewViewInstance ?: return
        val cam = camera ?: return

        try {
            val factory = preview.meteringPointFactory
            val point = factory.createPoint(offset.x, offset.y)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build()
            cam.cameraControl.startFocusAndMetering(action)
        } catch (_: Exception) {}
    }

    // Animate tap focus ring
    LaunchedEffect(tapFocusPoint) {
        if (tapFocusPoint != null) {
            focusPulseAnim.snapTo(1f)
            focusPulseAnim.animateTo(0f, animationSpec = tween(900, easing = FastOutSlowInEasing))
            tapFocusPoint = null
        }
    }

    if (isReviewing && capturedPhotos.isNotEmpty()) {
        ScanReviewAndDetailsView(
            capturedPhotos = capturedPhotos,
            farmerNotesText = farmerNotesText,
            language = language,
            onNotesChanged = { farmerNotesText = it },
            onTakeAnotherPhoto = { isReviewing = false },
            onRemovePhoto = { uri ->
                capturedPhotos = capturedPhotos - uri
                if (capturedPhotos.isEmpty()) isReviewing = false
            },
            onRetakeAll = {
                capturedPhotos = emptyList()
                farmerNotesText = ""
                isReviewing = false
            },
            onSubmit = {
                onImagesAndNotesSubmitted(capturedPhotos, farmerNotesText.trim().ifBlank { null })
            },
            onBack = {
                if (capturedPhotos.isNotEmpty()) {
                    isReviewing = false
                } else {
                    onBackClick()
                }
            },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
        // Live CameraX Preview with Tap-to-Focus Detection
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                previewViewInstance = previewView

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (_: Exception) {}
                }, mainExecutor)
                previewView
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onPreviewTapped(offset)
                    }
                }
        )

        // Dynamic Visual Framing & Alignment Overlay
        DynamicVisualAlignmentOverlay(
            framingMode = framingMode,
            rollAngle = rollAngle,
            pitchAngle = pitchAngle,
            isSteady = isSteady,
            isPerfectAlignment = isPerfectAlignment,
            showGrid = showGrid,
            tapFocusPoint = tapFocusPoint,
            focusPulseAlpha = focusPulseAnim.value,
            modifier = Modifier.fillMaxSize()
        )

        // Screen flash animation on photo capture
        if (shutterAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = shutterAlpha.value))
            )
        }

        // Top Controls Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .testTag("camera_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Quick Actions: Grid toggle & Tips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Grid Toggle
                IconButton(
                    onClick = { showGrid = !showGrid },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (showGrid) Color(0xFF2E7D32).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.55f))
                        .testTag("camera_grid_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (showGrid) Icons.Default.GridOn else Icons.Default.GridOff,
                        contentDescription = "Toggle Grid",
                        tint = if (showGrid) Color(0xFFFFD54F) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tips Button
                IconButton(
                    onClick = { showTipsDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("camera_tips_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Photo Tips",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Flash Mode Toggle Button
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("camera_flash_toggle_btn")
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Toggle Flash",
                        tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) Color(0xFFFFD54F) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Dynamic Real-time Alignment Status Banner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 58.dp)
        ) {
            DynamicAlignmentPill(
                language = language,
                isPerfectAlignment = isPerfectAlignment,
                isAngleLevel = isAngleLevel,
                rollAngle = rollAngle,
                isSteady = isSteady,
                framingMode = framingMode
            )
        }

        // Bottom Controls Section: Framing Modes + Shutter + Gallery
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crop Framing Mode Selector Pills
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FramingModeTab(
                    selected = framingMode == CropFramingMode.LEAF,
                    title = when (language) {
                        Language.GUJARATI -> "🌿 પાન"
                        Language.HINDI -> "🌿 पत्ता"
                        else -> "🌿 Leaf"
                    },
                    onClick = { framingMode = CropFramingMode.LEAF },
                    testTag = "mode_leaf_btn"
                )
                FramingModeTab(
                    selected = framingMode == CropFramingMode.PLANT,
                    title = when (language) {
                        Language.GUJARATI -> "🌾 આખો છોડ"
                        Language.HINDI -> "🌾 पौधा"
                        else -> "🌾 Crop"
                    },
                    onClick = { framingMode = CropFramingMode.PLANT },
                    testTag = "mode_plant_btn"
                )
                FramingModeTab(
                    selected = framingMode == CropFramingMode.PEST_FRUIT,
                    title = when (language) {
                        Language.GUJARATI -> "🐛 ફળ / જીવાત"
                        Language.HINDI -> "🐛 फल / कीट"
                        else -> "🐛 Pest/Fruit"
                    },
                    onClick = { framingMode = CropFramingMode.PEST_FRUIT },
                    testTag = "mode_pest_btn"
                )
            }

            // Primary Shutter Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gallery shortcut button
                IconButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("camera_gallery_shortcut_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Shutter Button with Dynamic Alignment Glow Ring
                val shutterBorderColor by animateColorAsState(
                    targetValue = if (isPerfectAlignment) Color(0xFF00E676) else Color.White,
                    animationSpec = tween(300),
                    label = "shutterGlow"
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, shutterBorderColor, CircleShape)
                        .clickable(enabled = !isCapturing) {
                            capturePhoto()
                        }
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCapturing) Color.Gray
                            else if (isPerfectAlignment) Color(0xFFF1F8E9)
                            else Color.White
                        )
                        .testTag("camera_shutter_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else if (isPerfectAlignment) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Balance spacer for symmetric design
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Tap to focus",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
    }

    // Photo Alignment & Capture Tips Dialog
    if (showTipsDialog) {
        CameraPhotoTipsDialog(
            language = language,
            onDismiss = { showTipsDialog = false }
        )
    }
}

/**
 * Framing mode tab selector chip
 */
@Composable
private fun FramingModeTab(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = if (selected) Color(0xFF2E7D32) else Color.Transparent
    val textColor = if (selected) Color.White else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Real-time dynamic guidance status pill reflecting phone alignment and stability
 */
@Composable
private fun DynamicAlignmentPill(
    language: Language,
    isPerfectAlignment: Boolean,
    isAngleLevel: Boolean,
    rollAngle: Float,
    isSteady: Boolean,
    framingMode: CropFramingMode
) {
    val (bgColor, icon, text) = remember(isPerfectAlignment, isAngleLevel, rollAngle, isSteady, language, framingMode) {
        when {
            isPerfectAlignment -> {
                val t = when (language) {
                    Language.GUJARATI -> "✓ સચોટ એંગલ! ફોટો ક્લિક કરો"
                    Language.HINDI -> "✓ सटीक कोण! फोटो खींचें"
                    else -> "✓ Perfect Angle! Ready to snap"
                }
                Triple(Color(0xFF1B5E20).copy(alpha = 0.9f), Icons.Default.CheckCircle, t)
            }
            !isSteady -> {
                val t = when (language) {
                    Language.GUJARATI -> "સ્થિર રાખો • હલનચલન રોકો"
                    Language.HINDI -> "स्थिर रखें • फोन न हिलाएं"
                    else -> "Hold Steady • Reduce motion"
                }
                Triple(Color(0xFFE65100).copy(alpha = 0.85f), Icons.Default.WarningAmber, t)
            }
            rollAngle < -6.5f -> {
                val t = when (language) {
                    Language.GUJARATI -> "જમણી બાજુ ઝુકાવો (${abs(rollAngle).toInt()}°)"
                    Language.HINDI -> "दाईं ओर सीधा करें (${abs(rollAngle).toInt()}°)"
                    else -> "Tilt phone right (${abs(rollAngle).toInt()}°)"
                }
                Triple(Color(0xFF37474F).copy(alpha = 0.85f), Icons.Default.ScreenRotation, t)
            }
            rollAngle > 6.5f -> {
                val t = when (language) {
                    Language.GUJARATI -> "ડાબી બાજુ ઝુકાવો (${abs(rollAngle).toInt()}°)"
                    Language.HINDI -> "बाईं ओर सीधा करें (${abs(rollAngle).toInt()}°)"
                    else -> "Tilt phone left (${abs(rollAngle).toInt()}°)"
                }
                Triple(Color(0xFF37474F).copy(alpha = 0.85f), Icons.Default.ScreenRotation, t)
            }
            else -> {
                val t = when (framingMode) {
                    CropFramingMode.LEAF -> when (language) {
                        Language.GUJARATI -> "🌿 પાંદડાને કેન્દ્રમાં ગોઠવો"
                        Language.HINDI -> "🌿 पत्ते को बीच में रखें"
                        else -> "🌿 Center affected leaf in frame"
                    }
                    CropFramingMode.PLANT -> when (language) {
                        Language.GUJARATI -> "🌾 સમગ્ર છોડ ફ્રેમમાં રાખો"
                        Language.HINDI -> "🌾 पूरे पौधे को फ्रेम में रखें"
                        else -> "🌾 Frame the whole crop plant"
                    }
                    CropFramingMode.PEST_FRUIT -> when (language) {
                        Language.GUJARATI -> "🐛 જીવાત કે ફળની નજીક લાવો"
                        Language.HINDI -> "🐛 कीट या फल के करीब लाएं"
                        else -> "🐛 Get close to pest or fruit"
                    }
                }
                Triple(Color.Black.copy(alpha = 0.75f), Icons.Default.Spa, t)
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = bgColor,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPerfectAlignment) Color(0xFF69F0AE) else Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Dynamic Visual Alignment Overlay Canvas
 * Features:
 * - Animated Laser/Scan line moving smoothly across framing area
 * - Real-time Spirit Level / Horizon gauge reflecting rollAngle
 * - Biological framing silhouettes (Leaf veins, plant outline, pest reticle)
 * - Animated corner brackets that light up green on optimal alignment
 * - Rule-of-thirds alignment grid
 * - Tap-to-focus animated ripple at target coordinates
 */
@Composable
private fun DynamicVisualAlignmentOverlay(
    framingMode: CropFramingMode,
    rollAngle: Float,
    pitchAngle: Float,
    isSteady: Boolean,
    isPerfectAlignment: Boolean,
    showGrid: Boolean,
    tapFocusPoint: Offset?,
    focusPulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    // Continuous subtle scan line animation inside the target reticle
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanProgress"
    )

    // Corner bracket color animation
    val reticleColor by animateColorAsState(
        targetValue = if (isPerfectAlignment) Color(0xFF00E676) else Color(0xDD81C784),
        animationSpec = tween(250),
        label = "reticleColor"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Determine framing boundary based on selected mode
        val frameWidth: Float
        val frameHeight: Float
        when (framingMode) {
            CropFramingMode.LEAF -> {
                frameWidth = width * 0.78f
                frameHeight = frameWidth * 1.18f
            }
            CropFramingMode.PLANT -> {
                frameWidth = width * 0.82f
                frameHeight = frameWidth * 1.35f
            }
            CropFramingMode.PEST_FRUIT -> {
                frameWidth = width * 0.74f
                frameHeight = frameWidth
            }
        }

        val left = (width - frameWidth) / 2f
        val top = (height - frameHeight) / 2.35f
        val right = left + frameWidth
        val bottom = top + frameHeight
        val centerX = left + frameWidth / 2f
        val centerY = top + frameHeight / 2f

        // 1. Rule-of-Thirds Grid (when enabled)
        if (showGrid) {
            val gridColor = Color.White.copy(alpha = 0.22f)
            val strokeGrid = 1.dp.toPx()

            // Vertical 1/3 and 2/3
            val v1 = left + frameWidth / 3f
            val v2 = left + (frameWidth * 2f) / 3f
            drawLine(gridColor, Offset(v1, top), Offset(v1, bottom), strokeGrid)
            drawLine(gridColor, Offset(v2, top), Offset(v2, bottom), strokeGrid)

            // Horizontal 1/3 and 2/3
            val h1 = top + frameHeight / 3f
            val h2 = top + (frameHeight * 2f) / 3f
            drawLine(gridColor, Offset(left, h1), Offset(right, h1), strokeGrid)
            drawLine(gridColor, Offset(left, h2), Offset(right, h2), strokeGrid)
        }

        // 2. Corner Bracket Reticle Accents
        val strokeWidth = if (isPerfectAlignment) 3.5.dp.toPx() else 2.5.dp.toPx()
        val cornerLength = 38.dp.toPx()

        // Top-left
        drawLine(reticleColor, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
        drawLine(reticleColor, Offset(left, top), Offset(left, top + cornerLength), strokeWidth)

        // Top-right
        drawLine(reticleColor, Offset(right, top), Offset(right - cornerLength, top), strokeWidth)
        drawLine(reticleColor, Offset(right, top), Offset(right, top + cornerLength), strokeWidth)

        // Bottom-left
        drawLine(reticleColor, Offset(left, bottom), Offset(left + cornerLength, bottom), strokeWidth)
        drawLine(reticleColor, Offset(left, bottom), Offset(left, bottom - cornerLength), strokeWidth)

        // Bottom-right
        drawLine(reticleColor, Offset(right, bottom), Offset(right - cornerLength, bottom), strokeWidth)
        drawLine(reticleColor, Offset(right, bottom), Offset(right, bottom - cornerLength), strokeWidth)

        // 3. Subtle Dashed Outline for the boundary
        val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
        drawRoundRect(
            color = reticleColor.copy(alpha = if (isPerfectAlignment) 0.35f else 0.18f),
            topLeft = Offset(left, top),
            size = Size(frameWidth, frameHeight),
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx(), pathEffect = dashedEffect)
        )

        // 4. Mode-Specific Alignment Silhouettes
        when (framingMode) {
            CropFramingMode.LEAF -> {
                // Organic leaf silhouette with main vein and side veins
                val leafPath = Path().apply {
                    val leafTopY = top + frameHeight * 0.15f
                    val leafBottomY = bottom - frameHeight * 0.15f
                    val leafLeftX = left + frameWidth * 0.22f
                    val leafRightX = right - frameWidth * 0.22f

                    moveTo(centerX, leafTopY)
                    // Left curve of leaf
                    cubicTo(
                        leafLeftX, top + frameHeight * 0.35f,
                        leafLeftX, bottom - frameHeight * 0.35f,
                        centerX, leafBottomY
                    )
                    // Right curve of leaf
                    cubicTo(
                        leafRightX, bottom - frameHeight * 0.35f,
                        leafRightX, top + frameHeight * 0.35f,
                        centerX, leafTopY
                    )
                }

                drawPath(
                    path = leafPath,
                    color = reticleColor.copy(alpha = if (isPerfectAlignment) 0.25f else 0.12f),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Central midrib vein
                val midribTopY = top + frameHeight * 0.18f
                val midribBottomY = bottom - frameHeight * 0.18f
                drawLine(
                    color = reticleColor.copy(alpha = 0.25f),
                    start = Offset(centerX, midribTopY),
                    end = Offset(centerX, midribBottomY),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Lateral secondary veins
                val veinOffsets = listOf(0.32f, 0.45f, 0.58f, 0.70f)
                veinOffsets.forEach { factor ->
                    val vy = top + frameHeight * factor
                    val span = frameWidth * 0.16f * (1f - abs(factor - 0.5f) * 1.5f).coerceAtLeast(0.4f)
                    drawLine(
                        color = reticleColor.copy(alpha = 0.2f),
                        start = Offset(centerX, vy),
                        end = Offset(centerX - span, vy - 14.dp.toPx()),
                        strokeWidth = 1.2.dp.toPx()
                    )
                    drawLine(
                        color = reticleColor.copy(alpha = 0.2f),
                        start = Offset(centerX, vy),
                        end = Offset(centerX + span, vy - 14.dp.toPx()),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }
            }

            CropFramingMode.PLANT -> {
                // Whole plant / stalk guide
                val stemYTop = top + frameHeight * 0.2f
                val stemYBottom = bottom - frameHeight * 0.1f

                // Central main stem
                drawLine(
                    color = reticleColor.copy(alpha = 0.3f),
                    start = Offset(centerX, stemYTop),
                    end = Offset(centerX, stemYBottom),
                    strokeWidth = 2.dp.toPx()
                )

                // Branches / Foliage levels
                val branchLevels = listOf(0.35f, 0.50f, 0.65f)
                branchLevels.forEachIndexed { i, factor ->
                    val by = top + frameHeight * factor
                    val branchWidth = frameWidth * (0.28f + i * 0.05f)
                    drawLine(
                        color = reticleColor.copy(alpha = 0.22f),
                        start = Offset(centerX - branchWidth, by + 10.dp.toPx()),
                        end = Offset(centerX, by),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = reticleColor.copy(alpha = 0.22f),
                        start = Offset(centerX, by),
                        end = Offset(centerX + branchWidth, by + 10.dp.toPx()),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Soil baseline indicator
                val groundY = bottom - frameHeight * 0.1f
                drawLine(
                    color = Color(0xFFA1887F).copy(alpha = 0.45f),
                    start = Offset(left + frameWidth * 0.2f, groundY),
                    end = Offset(right - frameWidth * 0.2f, groundY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            CropFramingMode.PEST_FRUIT -> {
                // Concentric macro target rings for insects, fruit spot lesions
                val radiusOuter = frameWidth * 0.36f
                val radiusInner = frameWidth * 0.18f

                drawCircle(
                    color = reticleColor.copy(alpha = 0.28f),
                    radius = radiusOuter,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                drawCircle(
                    color = reticleColor.copy(alpha = 0.45f),
                    radius = radiusInner,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashedEffect)
                )

                // 4 macro crosshair ticks
                val tickLength = 14.dp.toPx()
                drawLine(reticleColor.copy(alpha = 0.6f), Offset(centerX - radiusOuter - tickLength, centerY), Offset(centerX - radiusOuter, centerY), 1.5.dp.toPx())
                drawLine(reticleColor.copy(alpha = 0.6f), Offset(centerX + radiusOuter, centerY), Offset(centerX + radiusOuter + tickLength, centerY), 1.5.dp.toPx())
                drawLine(reticleColor.copy(alpha = 0.6f), Offset(centerX, centerY - radiusOuter - tickLength), Offset(centerX, centerY - radiusOuter), 1.5.dp.toPx())
                drawLine(reticleColor.copy(alpha = 0.6f), Offset(centerX, centerY + radiusOuter), Offset(centerX, centerY + radiusOuter + tickLength), 1.5.dp.toPx())
            }
        }

        // 5. Animated Diagnostic Scan Beam (sweeping up/down)
        val scanY = top + (frameHeight * scanProgress)
        val beamBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                reticleColor.copy(alpha = 0.65f),
                Color.White.copy(alpha = 0.85f),
                reticleColor.copy(alpha = 0.65f),
                Color.Transparent
            ),
            startX = left,
            endX = right
        )
        drawLine(
            brush = beamBrush,
            start = Offset(left + 8.dp.toPx(), scanY),
            end = Offset(right - 8.dp.toPx(), scanY),
            strokeWidth = 2.dp.toPx()
        )

        // 6. Dynamic Spirit Level / Horizon Gauge (phone tilt feedback)
        val levelY = bottom + 24.dp.toPx()
        val levelBarWidth = frameWidth * 0.65f
        val levelBarLeft = centerX - levelBarWidth / 2f
        val levelBarRight = centerX + levelBarWidth / 2f

        // Baseline bar
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(levelBarLeft, levelY),
            end = Offset(levelBarRight, levelY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center sweet spot tick brackets (-6° to +6°)
        val sweetSpotHalfWidth = levelBarWidth * 0.12f
        drawLine(
            color = Color(0xFF00E676).copy(alpha = 0.8f),
            start = Offset(centerX - sweetSpotHalfWidth, levelY - 5.dp.toPx()),
            end = Offset(centerX - sweetSpotHalfWidth, levelY + 5.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color(0xFF00E676).copy(alpha = 0.8f),
            start = Offset(centerX + sweetSpotHalfWidth, levelY - 5.dp.toPx()),
            end = Offset(centerX + sweetSpotHalfWidth, levelY + 5.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        // Moving spirit bubble according to rollAngle (-45° to +45° mapped)
        val clampedRoll = rollAngle.coerceIn(-30f, 30f)
        val bubbleOffsetNormalized = (clampedRoll / 30f)
        val bubbleX = centerX - (bubbleOffsetNormalized * (levelBarWidth / 2f - 10.dp.toPx()))

        val bubbleColor = if (isPerfectAlignment) Color(0xFF00E676)
        else if (abs(rollAngle) <= 6.5f) Color(0xFF69F0AE)
        else Color(0xFFFFD54F)

        drawCircle(
            color = bubbleColor,
            radius = 6.dp.toPx(),
            center = Offset(bubbleX, levelY)
        )
        drawCircle(
            color = Color.White,
            radius = 2.5.dp.toPx(),
            center = Offset(bubbleX, levelY)
        )

        // 7. Center reticle crosshair
        val crossSize = 10.dp.toPx()
        val crossColor = Color.White.copy(alpha = 0.5f)
        drawLine(crossColor, Offset(centerX - crossSize, centerY), Offset(centerX + crossSize, centerY), 1.5.dp.toPx())
        drawLine(crossColor, Offset(centerX, centerY - crossSize), Offset(centerX, centerY + crossSize), 1.5.dp.toPx())

        // 8. Tap-to-focus animation ring
        if (tapFocusPoint != null && focusPulseAlpha > 0.05f) {
            val tapCenter = tapFocusPoint
            val ringRadius = (32.dp.toPx()) * (1.2f - (focusPulseAlpha * 0.2f))
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = focusPulseAlpha),
                radius = ringRadius,
                center = tapCenter,
                style = Stroke(width = 2.dp.toPx())
            )
            // 4 corner ticks for focus box
            val boxSize = 24.dp.toPx()
            drawRect(
                color = Color.White.copy(alpha = focusPulseAlpha),
                topLeft = Offset(tapCenter.x - boxSize / 2f, tapCenter.y - boxSize / 2f),
                size = Size(boxSize, boxSize),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}

/**
 * Visual Photo Guidance Dialog for Farmers
 */
@Composable
private fun CameraPhotoTipsDialog(
    language: Language,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFBC02D),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "સારા ફોટો માટે સૂચનો"
                                Language.HINDI -> "अच्छी फोटो के लिए सुझाव"
                                else -> "Tips for Accurate Diagnosis"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PhotoTipRow(
                    icon = "☀️",
                    title = when (language) {
                        Language.GUJARATI -> "યોગ્ય કુદરતી સૂર્યપ્રકાશ"
                        Language.HINDI -> "पर्याप्त प्राकृतिक धूप"
                        else -> "Good Natural Daylight"
                    },
                    desc = when (language) {
                        Language.GUJARATI -> "પાંદડા પર તમારો પડછાયો ન પડે તેની કાળજી રાખો. વધુ પડતો અંધકાર ટાળો."
                        Language.HINDI -> "पत्ते पर अपनी परछाई न पड़ने दें। कम रोशनी में फ्लैश का उपयोग करें।"
                        else -> "Avoid casting your own shadow on the leaf. Use flash in dim light."
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PhotoTipRow(
                    icon = "📐",
                    title = when (language) {
                        Language.GUJARATI -> "ફોન સપાટ અને સમાંતર રાખો"
                        Language.HINDI -> "फोन को सीधा और समानांतर रखें"
                        else -> "Keep Phone Level & Parallel"
                    },
                    desc = when (language) {
                        Language.GUJARATI -> "ગ્રીન ઇન્ડિકેટર આવે ત્યાં સુધી ફોનને સીધો રાખો જેથી પાન સ્પષ્ટ દેખાય."
                        Language.HINDI -> "हरा संकेतक आने तक फोन सीधा रखें ताकि पत्ते की नसें स्पष्ट दिखें।"
                        else -> "Align until the green indicator glows for undistorted leaf vein view."
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PhotoTipRow(
                    icon = "🔍",
                    title = when (language) {
                        Language.GUJARATI -> "૧૫-૨૦ સેમી અંતર રાખો"
                        Language.HINDI -> "15-20 सेमी दूरी रखें"
                        else -> "Keep 15-20 cm Distance"
                    },
                    desc = when (language) {
                        Language.GUJARATI -> "ખૂબ દૂર કે વધુ પડતી નજીક ન રાખો. રોગવાળા ટપકાં ચોખ્ખા દેખાવા જોઈએ."
                        Language.HINDI -> "न ज्यादा दूर न बहुत पास रखें। रोग के धब्बे साफ दिखने चाहिए।"
                        else -> "Neither too far nor too close. Disease spots must be sharply focused."
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PhotoTipRow(
                    icon = "🖐️",
                    title = when (language) {
                        Language.GUJARATI -> "ફોન સ્થિર પકડી રાખો"
                        Language.HINDI -> "फोन को स्थिर पकड़ें"
                        else -> "Hold Steady Before Shutter"
                    },
                    desc = when (language) {
                        Language.GUJARATI -> "પવન હોય ત્યારે પાંદડાને હળવેથી પકડો અને ફોટો ક્લિક કરતી વખતે ન હલાવો."
                        Language.HINDI -> "हवा चलने पर पत्ते को हल्के हाथ से रोकें और फोटो खींचते समय न हिलाएं।"
                        else -> "Gently steady moving leaves in the wind before clicking the shutter."
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (language) {
                            Language.GUJARATI -> "સમજાઈ ગયું"
                            Language.HINDI -> "समझ गया"
                            else -> "Got it"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoTipRow(
    icon: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 16.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Interactive scan review and symptom refinement screen.
 * Allows farmer to preview captured photo(s), take another photo (up to 3),
 * add symptoms/observations by typing or voice, and submit for diagnosis.
 */
@Composable
fun ScanReviewAndDetailsView(
    capturedPhotos: List<Uri>,
    farmerNotesText: String,
    language: Language,
    onNotesChanged: (String) -> Unit,
    onTakeAnotherPhoto: () -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    onRetakeAll: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speechLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                val updated = if (farmerNotesText.isBlank()) spoken else "$farmerNotesText, $spoken"
                onNotesChanged(updated)
            }
        }
    }

    val quickSymptoms = when (language) {
        Language.GUJARATI -> listOf("🍃 પાન પીળા પડે છે", "🍂 કાળા ડાઘ", "🐛 જીવાત દેખાય છે", "💧 ચીકાશ છે", "🥀 છોડ કરમાય છે", "🌧️ વરસાદ પછી રોગ")
        Language.HINDI -> listOf("🍃 पत्तियां पीली पड़ रही हैं", "🍂 काले धब्बे", "🐛 कीड़े दिख रहे हैं", "💧 चिपचिपापन है", "🥀 पौधा मुरझा रहा है", "🌧️ बारिश के बाद रोग")
        Language.ENGLISH -> listOf("🍃 Yellowing leaves", "🍂 Dark spots", "🐛 Insects present", "💧 Sticky residue", "🥀 Plant wilting", "🌧️ After rain")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("review_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = Translations.scanReviewTitle(language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            TextButton(onClick = onRetakeAll, modifier = Modifier.testTag("review_retake_all_btn")) {
                Text(
                    text = Translations.retakeAllPhotos(language),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photos Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.GUJARATI) "પાડેલા ફોટા (${capturedPhotos.size}/3)"
                        else if (language == Language.HINDI) "खींची गई फोटो (${capturedPhotos.size}/3)"
                        else "Captured Photos (${capturedPhotos.size}/3)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (capturedPhotos.size < 3) {
                    Text(
                        text = if (language == Language.GUJARATI) "બીજા ${3 - capturedPhotos.size} ઉમેરી શકો છો"
                        else if (language == Language.HINDI) "और ${3 - capturedPhotos.size} जोड़ सकते हैं"
                        else "Can add ${3 - capturedPhotos.size} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Photos Carousel / List
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(capturedPhotos) { uri ->
                    Box(
                        modifier = Modifier
                            .size(width = 150.dp, height = 150.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured crop photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Delete button
                        IconButton(
                            onClick = { onRemovePhoto(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Add Another Photo card if less than 3
                if (capturedPhotos.size < 3) {
                    item {
                        Surface(
                            onClick = onTakeAnotherPhoto,
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .size(width = 150.dp, height = 150.dp)
                                .testTag("review_add_photo_tile")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "+ બીજો ફોટો લો"
                                    else if (language == Language.HINDI) "+ दूसरी फोटो लें"
                                    else "+ Add Photo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Take another photo full button (if < 3)
            if (capturedPhotos.size < 3) {
                OutlinedButton(
                    onClick = onTakeAnotherPhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("review_take_another_photo_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.takeAnotherPhoto(language),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Symptom / Observation Notes Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_notes_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✏️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.addNotesPrompt(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == Language.GUJARATI) "રોગ કેટલા દિવસથી છે, પાંદડા કેવા વળે છે કે જીવાત દેખાય છે તે અહીં જણાવો:"
                        else if (language == Language.HINDI) "रोग कितने दिनों से है, पत्तियां कैसी मुड़ रही हैं या कीट दिख रहे हैं, यहां बताएं:"
                        else "Describe symptom duration, leaf texture, or insects seen for accurate detection:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick symptom suggestion chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickSymptoms) { symptom ->
                            SuggestionChip(
                                onClick = {
                                    val cleaned = symptom.substring(2).trim()
                                    val updated = if (farmerNotesText.isBlank()) cleaned else "$farmerNotesText, $cleaned"
                                    onNotesChanged(updated)
                                },
                                label = { Text(text = symptom, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = farmerNotesText,
                        onValueChange = onNotesChanged,
                        placeholder = {
                            Text(
                                text = Translations.addNotesPlaceholder(language),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val intent = SpeechRecognizerHelper.createSpeechIntent(
                                        language,
                                        if (language == Language.GUJARATI) "પાકના લક્ષણો બોલો..."
                                        else if (language == Language.HINDI) "फसल के लक्षण बोलें..."
                                        else "Speak crop symptoms..."
                                    )
                                    try {
                                        speechLauncher.launch(intent)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier.testTag("review_voice_mic_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .testTag("review_notes_input_field"),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Detect Disease Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .imePadding()
                    .testTag("review_detect_disease_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Translations.detectDiseaseAndSolution(language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
