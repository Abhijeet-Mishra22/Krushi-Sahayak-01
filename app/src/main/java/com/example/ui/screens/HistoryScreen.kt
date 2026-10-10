package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AgriculturalKnowledgeBase
import com.example.data.local.DiagnosisEntity
import com.example.model.Language
import com.example.model.Translations
import com.example.speech.SpeechRecognizerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.text.SimpleDateFormat
import java.util.*

enum class DateFilter {
    ALL,
    TODAY,
    YESTERDAY,
    PAST_WEEK
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HistoryScreen(
    historyList: List<DiagnosisEntity>,
    language: Language,
    onBackClick: () -> Unit,
    onItemClick: (DiagnosisEntity) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onClearAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf(DateFilter.ALL) }
    var selectedCropId by remember { mutableStateOf<String?>(null) } // null means all crops
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val audioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    val voiceSearchLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
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

    fun launchVoiceSearch() {
        if (audioPermissionState.status.isGranted) {
            val intent = SpeechRecognizerHelper.createSpeechIntent(
                language,
                if (language == Language.GUJARATI) "પાકનું નામ કે તારીખ બોલો..."
                else if (language == Language.HINDI) "फसल का नाम या तारीख बोलें..."
                else "Speak crop name or date..."
            )
            try {
                voiceSearchLauncher.launch(intent)
            } catch (e: Exception) {
                // Not available
            }
        } else {
            audioPermissionState.launchPermissionRequest()
        }
    }

    // Date threshold calculations
    val now = Calendar.getInstance()
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val yesterdayStart = todayStart - (24 * 60 * 60 * 1000)
    val pastWeekStart = todayStart - (7 * 24 * 60 * 60 * 1000)

    // Filtered list
    val filteredDiagnoses = remember(historyList, searchQuery, selectedDateFilter, selectedCropId) {
        historyList.filter { item ->
            // Date filter condition
            val matchesDate = when (selectedDateFilter) {
                DateFilter.ALL -> true
                DateFilter.TODAY -> item.timestamp >= todayStart
                DateFilter.YESTERDAY -> item.timestamp in yesterdayStart until todayStart
                DateFilter.PAST_WEEK -> item.timestamp >= pastWeekStart
            }

            // Crop filter condition
            val matchesCrop = selectedCropId == null || item.cropId.equals(selectedCropId, ignoreCase = true)

            // Search query condition (crop name, problem name, date text)
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.timestamp))
                item.cropName.contains(searchQuery, ignoreCase = true) ||
                        item.problemName.contains(searchQuery, ignoreCase = true) ||
                        item.cropId.contains(searchQuery, ignoreCase = true) ||
                        formattedDate.contains(searchQuery, ignoreCase = true)
            }

            matchesDate && matchesCrop && matchesSearch
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = if (language == Language.GUJARATI) "ઇતિહાસ સાફ કરવો?"
                    else if (language == Language.HINDI) "इतिहास हटाएं?"
                    else "Clear All History?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == Language.GUJARATI)
                        "શું તમે ખરેખર બધા પાછલા પાક નિદાનના રેકોર્ડ કાઢી નાખવા માંગો છો?"
                    else if (language == Language.HINDI)
                        "क्या आप वाकई सभी पिछले फसल निदान रिकॉर्ड हटाना चाहते हैं?"
                    else
                        "Are you sure you want to delete all previous crop diagnosis reports? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearAllHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = if (language == Language.GUJARATI) "હા, સાફ કરો"
                        else if (language == Language.HINDI) "हाँ, हटाएं"
                        else "Clear All",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = Translations.history(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Count badge
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${historyList.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("history_clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear All",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar & Filter Section
            Surface(
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // 1. Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = Translations.searchByCropOrDate(language),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { launchVoiceSearch() },
                                    modifier = Modifier.testTag("history_voice_search_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_search_bar")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Date Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedDateFilter == DateFilter.ALL,
                                onClick = { selectedDateFilter = DateFilter.ALL },
                                label = { Text(Translations.all(language)) },
                                modifier = Modifier.testTag("filter_date_all")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedDateFilter == DateFilter.TODAY,
                                onClick = { selectedDateFilter = DateFilter.TODAY },
                                label = { Text(Translations.today(language)) },
                                modifier = Modifier.testTag("filter_date_today")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedDateFilter == DateFilter.YESTERDAY,
                                onClick = { selectedDateFilter = DateFilter.YESTERDAY },
                                label = { Text(Translations.yesterday(language)) },
                                modifier = Modifier.testTag("filter_date_yesterday")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedDateFilter == DateFilter.PAST_WEEK,
                                onClick = { selectedDateFilter = DateFilter.PAST_WEEK },
                                label = { Text(Translations.pastWeek(language)) },
                                modifier = Modifier.testTag("filter_date_past_week")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Crop Filter Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCropId == null,
                                onClick = { selectedCropId = null },
                                label = {
                                    Text(
                                        if (language == Language.GUJARATI) "બધા પાક"
                                        else if (language == Language.HINDI) "सभी फसलें"
                                        else "All Crops"
                                    )
                                },
                                modifier = Modifier.testTag("filter_crop_all")
                            )
                        }

                        items(AgriculturalKnowledgeBase.crops, key = { it.id }) { crop ->
                            val isSelected = selectedCropId == crop.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCropId = if (isSelected) null else crop.id
                                },
                                label = {
                                    Text("${crop.emoji} ${crop.getLocalizedName(language)}")
                                },
                                modifier = Modifier.testTag("filter_crop_${crop.id}")
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Report Results List
            if (filteredDiagnoses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HistoryEdu,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedCropId != null || selectedDateFilter != DateFilter.ALL)
                                (if (language == Language.GUJARATI) "આ ફિલ્ટર મુજબ કોઈ નિદાન મળ્યું નથી."
                                else if (language == Language.HINDI) "इस फ़िल्टर अनुसार कोई निदान नहीं मिला।"
                                else "No diagnosis reports found matching filters.")
                            else Translations.noHistoryYet(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (searchQuery.isNotBlank() || selectedCropId != null || selectedDateFilter != DateFilter.ALL) {
                            TextButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedDateFilter = DateFilter.ALL
                                    selectedCropId = null
                                }
                            ) {
                                Text(
                                    text = if (language == Language.GUJARATI) "ફિલ્ટર્સ રીસેટ કરો"
                                    else if (language == Language.HINDI) "फ़िल्टर रीसेट करें"
                                    else "Reset Filters"
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredDiagnoses, key = { it.id }) { item ->
                        HistoryReportCard(
                            item = item,
                            language = language,
                            onClick = { onItemClick(item) },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryReportCard(
    item: DiagnosisEntity,
    language: Language,
    onClick: () -> Unit,
    onDelete: () -> Unit
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

    val formattedDate = remember(item.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("history_report_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo thumbnail or crop avatar
            if (item.imageUri.isNotBlank()) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = "Crop photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.cropName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = item.problemName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!item.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Badges row: Confidence and Online/Offline
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${item.confidencePercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.isOnline) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (item.isOnline) "🟢 Online" else "🟠 Offline",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete individual report button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_report_btn_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete report",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
