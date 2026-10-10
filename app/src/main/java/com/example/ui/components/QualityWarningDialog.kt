package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ImageQualityResult
import com.example.model.Language
import com.example.model.Translations

@Composable
fun QualityWarningDialog(
    qualityResult: ImageQualityResult,
    language: Language,
    onRetake: () -> Unit,
    onContinue: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* force decision */ },
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(
                text = Translations.imageNotClearTitle(language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = qualityResult.getLocalizedErrorMessage(language)
                        ?: "Photo is not clear. Please take a closer photo of the leaf.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (language == Language.GUJARATI) "💡 સારો ફોટો પાડવા માટેની ટીપ્સ:"
                            else if (language == Language.HINDI) "💡 अच्छी फोटो खींचने के सुझाव:"
                            else "💡 Tips for a clear photo:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == Language.GUJARATI)
                                "• કેમેરા રોગવાળા પાંદડાની નજીક રાખો\n• સૂર્યપ્રકાશમાં ફોટો પાડો\n• હાથ સ્થિર રાખો જેથી ફોટો ધૂંધળો ન થાય"
                            else if (language == Language.HINDI)
                                "• कैमरा प्रभावित पत्ते के पास रखें\n• दिन की धूप में फोटो लें\n• हाथ स्थिर रखें ताकि फोटो साफ आए"
                            else
                                "• Hold camera close to the affected leaf\n• Take photo in clear daylight\n• Keep hand steady to avoid blur",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("retake_photo_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.retakePhoto(language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("continue_anyway_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.continueAnyway(language),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
