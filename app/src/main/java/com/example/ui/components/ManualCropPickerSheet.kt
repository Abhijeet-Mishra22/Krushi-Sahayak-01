package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Crop
import com.example.model.Language
import com.example.model.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualCropPickerSheet(
    crops: List<Crop> = com.example.data.local.AgriculturalKnowledgeBase.crops,
    selectedCrop: Crop?,
    language: Language,
    onCropSelected: (Crop) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = Translations.selectCropManually(language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == Language.GUJARATI) "સચોટ નિદાન માટે તમારો વાસ્તવિક પાક પસંદ કરો"
                        else if (language == Language.HINDI) "सटीक निदान के लिए अपनी वास्तविक फसल चुनें"
                        else "Choose your actual crop for high-accuracy diagnosis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(crops) { crop ->
                    val isSelected = selectedCrop?.id == crop.id
                    Surface(
                        onClick = { onCropSelected(crop) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF135D2B).copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF135D2B)
                            else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shadowElevation = if (isSelected) 2.dp else 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crop_select_${crop.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color(0xFF135D2B).copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = crop.emoji, fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = crop.getLocalizedName(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF135D2B) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = crop.nameEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
