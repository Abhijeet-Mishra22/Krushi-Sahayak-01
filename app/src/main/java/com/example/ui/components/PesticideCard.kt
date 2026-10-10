package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Translations
import com.example.model.VerifiedPesticide
import com.example.ui.theme.HarvestAmberContainer
import com.example.ui.theme.HarvestAmberOnContainer

@Composable
fun PesticideCard(
    pesticide: VerifiedPesticide?,
    cropName: String,
    language: Language,
    onOpenDosageCalculator: ((VerifiedPesticide) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verified_pesticide_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = Translations.chemicalControl(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Active Ingredient First • ICAR/CIBRC Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

            if (pesticide != null) {
                // ACTIVE INGREDIENT FIRST
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Translations.activeIngredient(language),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            // Toxicity band pill badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1565C0).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "🏷️ CIBRC Verified",
                                    color = Color(0xFF1565C0),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pesticide.activeIngredient,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Formulation: ${pesticide.formulation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dosage & Application Dilution
                DetailRow(
                    label = Translations.applicationDosage(language),
                    value = pesticide.getLocalizedApplicationRate(language)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // QUICK FIELD DOSAGE CALCULATOR CARD
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🧪", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == Language.GUJARATI) "દવા છંટકાવ ગણતરી"
                                    else if (language == Language.HINDI) "छिड़काव खुराक कैलकुलेटर"
                                    else "Dosage Calculator",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "⚡ Instant",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "તમારા ખેતરના કદ મુજબ કેટલા પંપ અને કેટલી દવા જોઈશે તેની ગણતરી કરો."
                            else if (language == Language.HINDI) "अपने खेत के आकार अनुसार पंप और दवा की सटीक मात्रा जानें।"
                            else "Calculate exact chemical & pumps needed for your field area.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button to open full calculator
                        Button(
                            onClick = { onOpenDosageCalculator?.invoke(pesticide) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("pesticide_open_calculator_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = Translations.calculateForMyField(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pre-Harvest Interval (PHI)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HarvestAmberContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🥬", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                text = Translations.waitingPeriod(language),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = HarvestAmberOnContainer
                            )
                            Text(
                                text = pesticide.preHarvestIntervalDays,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = HarvestAmberOnContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Authority
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${Translations.verifiedBy(language)} ${pesticide.verifiedAuthority}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Safety warnings
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Translations.safetyInstructions(language),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        pesticide.getLocalizedSafetyWarnings(language).forEach { warning ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = warning,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Pictogram icons
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SafetyIconBadge("🧤", if (language == Language.GUJARATI) "મોજા" else if (language == Language.HINDI) "दस्ताने" else "Gloves")
                            SafetyIconBadge("😷", if (language == Language.GUJARATI) "માસ્ક" else if (language == Language.HINDI) "मास्क" else "Mask")
                            SafetyIconBadge("👁️", if (language == Language.GUJARATI) "ચશ્મા" else if (language == Language.HINDI) "चश्मा" else "Goggles")
                            SafetyIconBadge("🚫", if (language == Language.GUJARATI) "બાળકો દૂર" else if (language == Language.HINDI) "बच्चे दूर" else "Keep Away")
                            SafetyIconBadge("🌬️", if (language == Language.GUJARATI) "પવનમાં નહિ" else if (language == Language.HINDI) "हवा में नहीं" else "No Wind")
                        }
                    }
                }

            } else {
                // If pesticide is not required or unverified
                Text(
                    text = if (language == Language.GUJARATI)
                        "આ સમસ્યા માટે રાસાયણિક દવાની જરૂર નથી. પ્રાકૃતિક/જૈવિક પદ્ધતિઓ અપનાવો અથવા સ્થાનિક કૃષિ વિજ્ઞાન કેન્દ્ર (KVK) નો સંપર્ક કરો."
                    else if (language == Language.HINDI)
                        "इस समस्या के लिए रासायनिक दवा की आवश्यकता नहीं है। जैविक व प्राकृतिक तरीके अपनाएं या स्थानीय कृषि विज्ञान केंद्र (KVK) से संपर्क करें।"
                    else
                        "Chemical pesticide is not required for this issue. Prefer cultural and biological controls, or consult your local Krishi Vigyan Kendra (KVK).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SafetyIconBadge(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 22.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
