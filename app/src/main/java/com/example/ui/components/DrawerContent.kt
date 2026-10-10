package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.DiagnosisEntity
import com.example.model.Language
import com.example.model.Translations
import com.example.speech.SpeechRecognizerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DrawerContent(
    historyList: List<DiagnosisEntity>,
    language: Language,
    onNewDiagnosis: () -> Unit,
    onItemClick: (DiagnosisEntity) -> Unit,
    onViewAllHistory: (() -> Unit)? = null,
    onOpenPreventionLibrary: (() -> Unit)? = null,
    onOpenPesticideCalculator: (() -> Unit)? = null,
    onDeleteItem: ((Long) -> Unit)? = null,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val audioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    val drawerVoiceLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                searchQuery = spoken
            }
        }
    }

    fun launchDrawerVoiceSearch() {
        if (audioPermissionState.status.isGranted) {
            val intent = SpeechRecognizerHelper.createSpeechIntent(
                language,
                if (language == Language.GUJARATI) "પાકનું નામ બોલો..."
                else if (language == Language.HINDI) "फसल का नाम बोलें..."
                else "Speak crop name..."
            )
            try {
                drawerVoiceLauncher.launch(intent)
            } catch (e: Exception) {
                // Not available
            }
        } else {
            audioPermissionState.launchPermissionRequest()
        }
    }

    val filteredList = remember(historyList, searchQuery) {
        if (searchQuery.isBlank()) historyList
        else historyList.filter {
            it.cropName.contains(searchQuery, ignoreCase = true) ||
                    it.problemName.contains(searchQuery, ignoreCase = true) ||
                    it.cropId.contains(searchQuery, ignoreCase = true)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(330.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(18.dp)
        ) {
            // Enhanced Brand Header with subtle gradient tint
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFE8F5E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.krushi_sahayak_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF135D2B).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Translations.appTitle(language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF135D2B),
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = Translations.appSubtitle(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary New Diagnosis Button (Emerald CTA)
            Button(
                onClick = onNewDiagnosis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("drawer_new_diagnosis_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF135D2B),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = Translations.newDiagnosis(language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool Shortcuts in Drawer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Prevention Library
                Surface(
                    onClick = { onOpenPreventionLibrary?.invoke() },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("drawer_prevention_library_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = "📚", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "નિવારણ" else if (language == Language.HINDI) "रोकथाम" else "Library",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dosage Calculator
                Surface(
                    onClick = { onOpenPesticideCalculator?.invoke() },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("drawer_pesticide_calculator_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = "🧪", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.GUJARATI) "દવા ગણક" else if (language == Language.HINDI) "दवा कैलकुलेटर" else "Calculator",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History Header & Search Box
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${Translations.history(language)} (${historyList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                if (onViewAllHistory != null && historyList.isNotEmpty()) {
                    TextButton(
                        onClick = onViewAllHistory,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = Translations.viewAllHistory(language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (historyList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (language == Language.GUJARATI) "પાક શોધો (કપાસ, ટામેટા...)"
                            else if (language == Language.HINDI) "फसल खोजें (कपास, टमाटर...)"
                            else "Search history...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { launchDrawerVoiceSearch() },
                                modifier = Modifier.testTag("drawer_voice_search_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice search",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("history_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌿", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching records found" else Translations.noHistoryYet(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val (todayItems, yesterdayItems, olderItems) = groupHistoryByDate(filteredList)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (todayItems.isNotEmpty()) {
                        item {
                            HistoryGroupHeader(text = Translations.today(language))
                        }
                        items(todayItems, key = { it.id }) { item ->
                            HistoryItemCard(
                                item = item,
                                onClick = { onItemClick(item) },
                                onDelete = onDeleteItem
                            )
                        }
                    }

                    if (yesterdayItems.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            HistoryGroupHeader(text = Translations.yesterday(language))
                        }
                        items(yesterdayItems, key = { it.id }) { item ->
                            HistoryItemCard(
                                item = item,
                                onClick = { onItemClick(item) },
                                onDelete = onDeleteItem
                            )
                        }
                    }

                    if (olderItems.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            HistoryGroupHeader(text = Translations.older(language))
                        }
                        items(olderItems, key = { it.id }) { item ->
                            HistoryItemCard(
                                item = item,
                                onClick = { onItemClick(item) },
                                onDelete = onDeleteItem
                            )
                        }
                    }
                }
            }

            if (historyList.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.GUJARATI) "ઇતિહાસ સાફ કરો" else if (language == Language.HINDI) "इतिहास हटाएं" else "Clear All History",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryGroupHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    )
}

@Composable
private fun HistoryItemCard(
    item: DiagnosisEntity,
    onClick: () -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val cropEmoji = when (item.cropId.lowercase()) {
        "cotton" -> "🌱"
        "tomato" -> "🍅"
        "wheat" -> "🌾"
        "groundnut" -> "🥜"
        "rice" -> "🌾"
        "chilli" -> "🌶️"
        "potato" -> "🥔"
        "maize" -> "🌽"
        "soybean" -> "🌿"
        "onion" -> "🧅"
        else -> "🌱"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("history_item_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = cropEmoji, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.cropName} • ${item.problemName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (item.isOnline) Color(0xFF198754).copy(alpha = 0.12f)
                else Color(0xFFE65100).copy(alpha = 0.12f)
            ) {
                Text(
                    text = if (item.isOnline) "🟢 Online" else "🟠 Offline",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isOnline) Color(0xFF198754) else Color(0xFFE65100),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            if (onDelete != null) {
                IconButton(
                    onClick = { onDelete(item.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun groupHistoryByDate(list: List<DiagnosisEntity>): Triple<List<DiagnosisEntity>, List<DiagnosisEntity>, List<DiagnosisEntity>> {
    val now = Calendar.getInstance()
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = todayStart - (24 * 60 * 60 * 1000)

    val today = mutableListOf<DiagnosisEntity>()
    val yesterday = mutableListOf<DiagnosisEntity>()
    val older = mutableListOf<DiagnosisEntity>()

    for (item in list) {
        when {
            item.timestamp >= todayStart -> today.add(item)
            item.timestamp >= yesterdayStart -> yesterday.add(item)
            else -> older.add(item)
        }
    }

    return Triple(today, yesterday, older)
}
