package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.local.ChatMessageEntity
import com.example.model.DiagnosisResult
import com.example.model.Language
import com.example.model.Translations
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    diagnosis: DiagnosisResult?,
    chatMessages: List<ChatMessageEntity>,
    language: Language,
    onBackClick: () -> Unit,
    onSendMessage: (String, Uri?) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()
    var showChatRationaleDialog by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Camera launcher inside chat
    var chatCamUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && chatCamUri != null) {
            attachedImageUri = chatCamUri
        }
    }

    fun captureChatPhoto() {
        if (cameraPermissionState.status.isGranted) {
            val file = File(context.cacheDir, "chat_cam_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            chatCamUri = uri
            cameraLauncher.launch(uri)
        } else if (cameraPermissionState.status.shouldShowRationale) {
            showChatRationaleDialog = true
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val quickQuestions = when (language) {
        Language.GUJARATI -> listOf(
            "કઈ દવા છાંટવી?",
            "છંટકાવનો યોગ્ય સમય કયો?",
            "શું લીંબોળીનું તેલ વાપરી શકાય?",
            "આને અટકાવવા શું કરવું?",
            "શું આનાથી વધુ નુકસાન થશે?"
        )
        Language.HINDI -> listOf(
            "कौन सी दवा छिड़कें?",
            "छिड़काव का सही समय क्या है?",
            "क्या नीम तेल इस्तेमाल करें?",
            "रोकथाम कैसे करें?",
            "क्या फसल को गंभीर नुकसान है?"
        )
        Language.ENGLISH -> listOf(
            "Which pesticide should I use?",
            "When is the best time to spray?",
            "Can I use neem oil?",
            "How to prevent this next time?",
            "Is this dangerous to yield?"
        )
    }

    if (showChatRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showChatRationaleDialog = false },
            title = {
                Text(
                    text = if (language == Language.GUJARATI) "કેમેરા પરવાનગી જરૂરી છે"
                    else if (language == Language.HINDI) "कैमरा अनुमति आवश्यक है"
                    else "Camera Permission Required",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == Language.GUJARATI)
                        "ચેટમાં પાક કે પાંદડાનો ફોટો મોકલવા માટે કેમેરાની પરવાનગી જરૂરી છે."
                    else if (language == Language.HINDI)
                        "चैट में फसल या पत्ते की फोटो भेजने के लिए कैमरा अनुमति आवश्यक है।"
                    else
                        "Camera permission is required to capture and attach crop photos in chat."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showChatRationaleDialog = false
                        cameraPermissionState.launchPermissionRequest()
                    }
                ) {
                    Text(
                        text = if (language == Language.GUJARATI) "પરવાનગી આપો"
                        else if (language == Language.HINDI) "अनुमति दें"
                        else "Allow"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatRationaleDialog = false }) {
                    Text(
                        text = if (language == Language.GUJARATI) "રદ કરો"
                        else if (language == Language.HINDI) "रद्द करें"
                        else "Cancel"
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == Language.GUJARATI) "કૃષિ સહાયક ચેટ"
                            else if (language == Language.HINDI) "कृषि सहायक चैट"
                            else "Krushi Sahayak Chat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (diagnosis != null) {
                            Text(
                                text = "${diagnosis.crop.getLocalizedName(language)} • ${diagnosis.problemName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("chat_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(8.dp)
            ) {
                // Attached image thumbnail preview if any
                if (attachedImageUri != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        AsyncImage(
                            model = attachedImageUri,
                            contentDescription = "Attached photo",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "ફોટો જોડ્યો છે" else if (language == Language.HINDI) "फोटो जुड़ी है" else "Photo attached",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { attachedImageUri = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attached photo",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Quick query chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    items(quickQuestions) { q ->
                        AssistChip(
                            onClick = {
                                onSendMessage(q, attachedImageUri)
                                attachedImageUri = null
                            },
                            label = { Text(text = q, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Input bar: [📷] [🎤] [Type...] [Send]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Camera button
                    IconButton(
                        onClick = { captureChatPhoto() },
                        modifier = Modifier.testTag("chat_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Attach photo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Voice button
                    IconButton(
                        onClick = onVoiceInput,
                        modifier = Modifier.testTag("chat_mic_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Text Input Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = Translations.askAboutCropPlaceholder(language),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text"),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() || attachedImageUri != null) {
                                onSendMessage(inputText, attachedImageUri)
                                inputText = ""
                                attachedImageUri = null
                            }
                        },
                        enabled = inputText.isNotBlank() || attachedImageUri != null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() || attachedImageUri != null) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() || attachedImageUri != null) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF135D2B).copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Color(0xFF135D2B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == Language.GUJARATI)
                                "હું તમારો કૃષિ સહાયક છું. નિદાન કરેલા પાક અંગે દવા, છંટકાવ કે દેશી ઉપાયો વિશે પૂછો."
                            else if (language == Language.HINDI)
                                "मैं आपका कृषि सहायक हूँ। जांची गई फसल के बारे में दवा, छिड़काव या देसी उपायों के बारे में पूछें."
                            else
                                "I am your Krushi Sahayak (agricultural assistant). Ask questions about the diagnosed crop, pesticides, spraying timings, or organic controls.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            items(chatMessages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeak = { onSpeakMessage(msg.text) }
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageEntity,
    onSpeak: () -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF135D2B).copy(alpha = 0.12f),
                modifier = Modifier
                    .padding(end = 8.dp, top = 2.dp)
                    .size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color(0xFF135D2B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) Color(0xFF135D2B)
            else MaterialTheme.colorScheme.surface,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
            shadowElevation = if (isUser) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.attachedImageUri != null) {
                    AsyncImage(
                        model = message.attachedImageUri,
                        contentDescription = "User uploaded crop picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = if (isUser) Color.White
                    else MaterialTheme.colorScheme.onSurface
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSpeak() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = Color(0xFF135D2B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Listen",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF135D2B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (isUser) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(start = 8.dp, top = 2.dp)
                    .size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
