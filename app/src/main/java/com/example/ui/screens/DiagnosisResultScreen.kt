package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.example.model.ConfidenceLevel
import com.example.model.DiagnosisResult
import com.example.model.HealthStatus
import com.example.model.Language
import com.example.model.Translations
import com.example.model.VerifiedPesticide
import com.example.ui.components.PesticideCard
import com.example.util.PdfReportExporter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DiagnosisResultScreen(
    diagnosis: DiagnosisResult,
    language: Language,
    isSpeaking: Boolean,
    activeUtterance: String? = null,
    onBackClick: () -> Unit,
    onListenClick: () -> Unit,
    onSpeakSection: (title: String, content: String) -> Unit = { _, _ -> },
    onSaveVoiceNote: (String) -> Unit = {},
    onViewPreventionLibrary: ((String) -> Unit)? = null,
    onOpenDosageCalculator: ((VerifiedPesticide?) -> Unit)? = null,
    onAskMoreClick: () -> Unit,
    onChangeCropClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == Language.GUJARATI) "નિદાન પરિણામ"
                            else if (language == Language.HINDI) "निदान परिणाम"
                            else "Diagnosis Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${diagnosis.crop.getLocalizedName(language)} • ${diagnosis.confidencePercent}% confidence",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .testTag("result_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    // Export PDF action button in top bar
                    FilledTonalIconButton(
                        onClick = {
                            val file = PdfReportExporter.exportAndShareReport(context, diagnosis, language)
                            if (file != null) {
                                Toast.makeText(context, Translations.pdfExportSuccess(language), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("result_top_export_pdf_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = Translations.exportPdfReport(language),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Mode indicator badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (diagnosis.isOnlineAnalysis) Color(0xFF198754).copy(alpha = 0.14f)
                        else Color(0xFFE65100).copy(alpha = 0.14f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (diagnosis.isOnlineAnalysis) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (diagnosis.isOnlineAnalysis) Color(0xFF198754) else Color(0xFFE65100),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (diagnosis.isOnlineAnalysis) "Online AI" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (diagnosis.isOnlineAnalysis) Color(0xFF198754) else Color(0xFFE65100),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // LISTEN ALOUD BUTTON (🔊 Listen / ⏹️ Stop) with animated audio pulse
                    Button(
                        onClick = onListenClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("result_listen_aloud_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking && activeUtterance == "full_diagnosis") MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isSpeaking && activeUtterance == "full_diagnosis") MaterialTheme.colorScheme.onError
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        if (isSpeaking && activeUtterance == "full_diagnosis") {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.stopListening(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.listenAloud(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // ASK MORE CHATBOT BUTTON (💬 Ask More)
                    Button(
                        onClick = onAskMoreClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("result_ask_more_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.askMore(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live TTS Playback Banner (Animated voice wave)
            AnimatedVisibility(visible = isSpeaking) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AudioVisualizerBars()
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.GUJARATI) "🔊 અવાજ દ્વારા સંભળાઈ રહ્યું છે..."
                                else if (language == Language.HINDI) "🔊 आवाज द्वारा सुनाया जा रहा है..."
                                else "🔊 Reading aloud...",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (activeUtterance == "full_diagnosis") (if (language == Language.GUJARATI) "સંપૂર્ણ નિદાન" else if (language == Language.HINDI) "पूरा निदान" else "Full Report")
                                else (activeUtterance ?: ""),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(
                            onClick = onListenClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StopCircle,
                                contentDescription = "Stop TTS",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // NON-CROP ALERT CARD (If user photographed hand, person, or non-plant object)
            if (diagnosis.healthStatus == HealthStatus.NOT_A_CROP) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("non_crop_warning_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = Translations.notACropLabel(language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = diagnosis.whatIsHappening,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("retake_crop_photo_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.GUJARATI) "પાકના પાનનો નવો ફોટો પાડો"
                                else if (language == Language.HINDI) "फसल के पत्ते की नई फोटो लें"
                                else "Take Photo of Crop Leaf",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // UNKNOWN / DISCOVERED CROP BANNER (Requirement 6)
            if (diagnosis.crop.id == "unknown_crop" || diagnosis.crop.id.startsWith("discovered_")) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("unknown_crop_info_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF135D2B).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color(0xFF135D2B),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (diagnosis.crop.id == "unknown_crop") Translations.unknownCropLabel(language)
                                else "${diagnosis.crop.getLocalizedName(language)} (Online Discovered)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = if (language == Language.GUJARATI) "આ પાકની વિગતો ઓનલાઇન ચકાસીને લોકલ ડેટાબેઝમાં સાચવી લેવામાં આવી છે."
                                else if (language == Language.HINDI) "इस फसल का ऑनलाइन विश्लेषण कर डेटाबेस में सुरक्षित कर लिया गया है।"
                                else "Identified online and saved to local database for future offline access.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Image Preview (up to 3 photos)
            if (diagnosis.imageUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(diagnosis.imageUris) { uri ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Diagnosed crop photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 180.dp, height = 180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }
            }

            // PRIMARY DIAGNOSIS CARD: Crop + Problem + Confidence
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Crop Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = diagnosis.crop.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Translations.cropLabel(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${diagnosis.crop.getLocalizedName(language)} (${diagnosis.crop.nameEn})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Change crop button
                        OutlinedButton(
                            onClick = onChangeCropClick,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("result_change_crop_btn")
                        ) {
                            Text(
                                text = if (language == Language.GUJARATI) "બદલો" else if (language == Language.HINDI) "बदलें" else "Change",
                                fontSize = 12.sp
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Detected Problem Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = diagnosis.healthStatus.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Translations.problemLabel(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = diagnosis.problemName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${diagnosis.healthStatus.getDisplayName(language)} • ${diagnosis.plantPart.getDisplayName(language)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confidence Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${Translations.confidenceLabel(language)}: ${diagnosis.confidenceLevel.getDisplayName(language)} (${diagnosis.confidencePercent}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // SECTION 1: What is happening?
            AdvisorySectionCard(
                icon = Icons.Default.Biotech,
                iconTint = Color(0xFF135D2B),
                title = Translations.whatIsHappening(language),
                content = diagnosis.whatIsHappening,
                isPlaying = isSpeaking && activeUtterance == Translations.whatIsHappening(language),
                onSpeak = {
                    onSpeakSection(Translations.whatIsHappening(language), diagnosis.whatIsHappening)
                }
            )

            // SECTION 2: Why does it happen?
            AdvisorySectionCard(
                icon = Icons.Default.HelpOutline,
                iconTint = Color(0xFF00695C),
                title = Translations.whyItHappens(language),
                content = diagnosis.whyItHappens,
                isPlaying = isSpeaking && activeUtterance == Translations.whyItHappens(language),
                onSpeak = {
                    onSpeakSection(Translations.whyItHappens(language), diagnosis.whyItHappens)
                }
            )

            // SECTION 3: What should I look for?
            AdvisorySectionCard(
                icon = Icons.Default.Visibility,
                iconTint = Color(0xFFE65100),
                title = Translations.whatToLookFor(language),
                content = diagnosis.whatToLookFor,
                isPlaying = isSpeaking && activeUtterance == Translations.whatToLookFor(language),
                onSpeak = {
                    onSpeakSection(Translations.whatToLookFor(language), diagnosis.whatToLookFor)
                }
            )

            // SECTION 4: How can I prevent it?
            AdvisorySectionCard(
                icon = Icons.Default.Shield,
                iconTint = Color(0xFF1565C0),
                title = Translations.howToPrevent(language),
                content = diagnosis.howToPrevent,
                isPlaying = isSpeaking && activeUtterance == Translations.howToPrevent(language),
                onSpeak = {
                    onSpeakSection(Translations.howToPrevent(language), diagnosis.howToPrevent)
                }
            )

            // SECTION 5: Biological / IPM Control
            AdvisorySectionCard(
                icon = Icons.Default.Eco,
                iconTint = Color(0xFF2E7D32),
                title = Translations.nonChemicalControl(language),
                content = diagnosis.nonChemicalControl,
                isPlaying = isSpeaking && activeUtterance == Translations.nonChemicalControl(language),
                onSpeak = {
                    onSpeakSection(Translations.nonChemicalControl(language), diagnosis.nonChemicalControl)
                }
            )

            // SECTION 6: 💊 Chemical / Verified Pesticide Recommendation Card
            PesticideCard(
                pesticide = diagnosis.verifiedPesticide,
                cropName = diagnosis.crop.getLocalizedName(language),
                language = language,
                onOpenDosageCalculator = { pest ->
                    onOpenDosageCalculator?.invoke(pest)
                }
            )

            // Link to full crop prevention library
            if (onViewPreventionLibrary != null) {
                OutlinedButton(
                    onClick = { onViewPreventionLibrary.invoke(diagnosis.crop.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("result_view_prevention_library_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.GUJARATI) "${diagnosis.crop.getLocalizedName(language)} માટે તમામ રોગ નિવારણ સલાહ જુઓ"
                        else if (language == Language.HINDI) "${diagnosis.crop.getLocalizedName(language)} की संपूर्ण रोकथाम गाइड देखें"
                        else "View all prevention tips for ${diagnosis.crop.nameEn}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SECTION 7: 📄 Export to PDF Report Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_export_pdf_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Translations.exportPdfReport(language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == Language.GUJARATI) "સંપૂર્ણ રોગ, લક્ષણો, દેશી ઉપાય અને દવા ભલામણ સાથે A4 PDF સેવ કે શેર કરો"
                                else if (language == Language.HINDI) "सम्पूर्ण रोग, लक्षण, जैविक उपाय और दवा सिफारिश के साथ A4 PDF सेव या शेयर करें"
                                else "Save or share full A4 PDF report with symptoms, causes & verified treatments",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val file = PdfReportExporter.exportAndShareReport(context, diagnosis, language)
                            if (file != null) {
                                Toast.makeText(context, Translations.pdfExportSuccess(language), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("result_download_pdf_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.exportPdfReport(language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // SECTION 8: 📝 Field Voice Notes
            FieldNotesCard(
                notes = diagnosis.notes,
                language = language,
                onAddVoiceNote = onSaveVoiceNote
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
private fun FieldNotesCard(
    notes: String?,
    language: Language,
    onAddVoiceNote: (String) -> Unit
) {
    val audioPermissionState = com.google.accompanist.permissions.rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )

    val speechLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                onAddVoiceNote(spoken)
            }
        }
    }

    fun launchVoiceDictation() {
        if (audioPermissionState.status.isGranted) {
            val intent = com.example.speech.SpeechRecognizerHelper.createSpeechIntent(
                language,
                if (language == Language.GUJARATI) "ખેતર અંગે તમારી નોંધ બોલો..."
                else if (language == Language.HINDI) "खेत के बारे में अपनी टिप्पणी बोलें..."
                else "Dictate your field note..."
            )
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                // Speech recognizer not available
            }
        } else {
            audioPermissionState.launchPermissionRequest()
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("field_notes_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (language == Language.GUJARATI) "ખેતરની નોંધ / અવલોકન"
                    else if (language == Language.HINDI) "खेत की टिप्पणी / अवलोकन"
                    else "Field Notes & Observations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Voice Note Dictation Button
                IconButton(
                    onClick = { launchVoiceDictation() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("add_voice_note_mic_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictate note",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!notes.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text(
                    text = if (language == Language.GUJARATI)
                        "હજુ સુધી કોઈ નોંધ નથી. તમારા ખેતર, પિયત કે ખાતર અંગે બોલીને નોંધ ઉમેરવા માઇક્રોફોન દબાવો."
                    else if (language == Language.HINDI)
                        "अभी कोई टिप्पणी नहीं है। अपने खेत, सिंचाई या खाद के बारे में बोलकर नोट जोड़ने के लिए माइक दबाएं।"
                    else
                        "No notes added yet. Tap the microphone to dictate observations about field location, irrigation, or weather.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Big Voice Dictation Button
            FilledTonalButton(
                onClick = { launchVoiceDictation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("dictate_voice_note_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == Language.GUJARATI) "બોલીને નોંધ ઉમેરો"
                    else if (language == Language.HINDI) "बोलकर टिप्पणी जोड़ें"
                    else "Add Voice Note",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AdvisorySectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    title: String,
    content: String,
    isPlaying: Boolean = false,
    onSpeak: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconTint.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Individual Section Speaker Button
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlaying) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read section aloud",
                        tint = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Animated sound wave indicator bars for TTS playback
 */
@Composable
private fun AudioVisualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")

    val height1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )

    val height2 by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )

    val height3 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(height1.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(height2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(height3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
