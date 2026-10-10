package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AgriculturalKnowledgeBase
import com.example.data.local.PreventionTipEntity
import com.example.model.Language
import com.example.model.Translations
import com.example.speech.SpeechRecognizerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PreventionLibraryScreen(
    tipsList: List<PreventionTipEntity>,
    language: Language,
    searchQuery: String,
    selectedCropId: String?,
    selectedCategory: String?,
    showOnlyBookmarked: Boolean,
    isSpeaking: Boolean,
    activeUtterance: String?,
    onSearchQueryChange: (String) -> Unit,
    onCropSelect: (String?) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onToggleBookmarkOnly: () -> Unit,
    onToggleBookmark: (Long, Boolean) -> Unit,
    onSpeakTip: (PreventionTipEntity) -> Unit,
    onStopSpeaking: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onStopSpeaking()
        onBackClick()
    }

    // Voice recognition permission and launcher for searching tips
    val audioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                onSearchQueryChange(spokenText)
            }
        }
    }

    fun launchVoiceSearch() {
        if (audioPermissionState.status.isGranted) {
            val prompt = when (language) {
                Language.GUJARATI -> "પાક અથવા રોગનું નામ બોલો..."
                Language.HINDI -> "फसल या रोग का नाम बोलें..."
                else -> "Speak crop or disease name..."
            }
            val intent = SpeechRecognizerHelper.createSpeechIntent(language, prompt)
            try {
                voiceSearchLauncher.launch(intent)
            } catch (_: Exception) {}
        } else {
            audioPermissionState.launchPermissionRequest()
        }
    }

    // Client-side filtering across crops, categories, bookmarks, and search query
    val filteredTips = remember(
        tipsList,
        searchQuery,
        selectedCropId,
        selectedCategory,
        showOnlyBookmarked
    ) {
        tipsList.filter { tip ->
            val matchesCrop = selectedCropId == null || tip.cropId.equals(selectedCropId, ignoreCase = true)
            val matchesCategory = selectedCategory == null || tip.category.equals(selectedCategory, ignoreCase = true)
            val matchesBookmark = !showOnlyBookmarked || tip.isBookmarked
            val matchesQuery = if (searchQuery.isBlank()) true else {
                tip.diseaseNameEn.contains(searchQuery, ignoreCase = true) ||
                        tip.diseaseNameHi.contains(searchQuery, ignoreCase = true) ||
                        tip.diseaseNameGu.contains(searchQuery, ignoreCase = true) ||
                        tip.cropNameEn.contains(searchQuery, ignoreCase = true) ||
                        tip.cropNameHi.contains(searchQuery, ignoreCase = true) ||
                        tip.cropNameGu.contains(searchQuery, ignoreCase = true) ||
                        tip.preventionEn.contains(searchQuery, ignoreCase = true) ||
                        tip.preventionHi.contains(searchQuery, ignoreCase = true) ||
                        tip.preventionGu.contains(searchQuery, ignoreCase = true) ||
                        tip.biologicalIpmEn.contains(searchQuery, ignoreCase = true) ||
                        tip.biologicalIpmHi.contains(searchQuery, ignoreCase = true) ||
                        tip.biologicalIpmGu.contains(searchQuery, ignoreCase = true)
            }
            matchesCrop && matchesCategory && matchesBookmark && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "રોગ નિવારણ પુસ્તિકા"
                                Language.HINDI -> "रोग रोकथाम गाइड"
                                else -> "Disease Prevention Library"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = when (language) {
                                        Language.GUJARATI -> "⚡ ૧૦૦% ઑફલાઇન"
                                        Language.HINDI -> "⚡ 100% ऑफलाइन"
                                        else -> "⚡ 100% Offline"
                                    },
                                    color = Color(0xFF2E7D32),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "• ${filteredTips.size} " + when (language) {
                                    Language.GUJARATI -> "સલાહ ઉપલબ્ધ"
                                    Language.HINDI -> "सलाह उपलब्ध"
                                    else -> "tips available"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onStopSpeaking()
                            onBackClick()
                        },
                        modifier = Modifier.testTag("prevention_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Bookmark toggle filter in top bar
                    IconButton(
                        onClick = onToggleBookmarkOnly,
                        modifier = Modifier.testTag("prevention_bookmark_filter_btn")
                    ) {
                        Icon(
                            imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Show Bookmarked",
                            tint = if (showOnlyBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input with Voice Mic & Clear Buttons
            PaddingValues(horizontal = 16.dp, vertical = 8.dp).let { pad ->
                Box(modifier = Modifier.padding(pad)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = when (language) {
                                    Language.GUJARATI -> "રોગ અથવા પાક શોધો (કપાસ, ગેરુ...)"
                                    Language.HINDI -> "रोग या फसल खोजें (कपास, झुलसा...)"
                                    else -> "Search prevention tips, crops..."
                                },
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onSearchQueryChange("") },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { launchVoiceSearch() },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("prevention_voice_search_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prevention_search_input")
                    )
                }
            }

            // Horizontal Crop Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All crops chip
                FilterChip(
                    selected = selectedCropId == null,
                    onClick = { onCropSelect(null) },
                    label = {
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "બધા પાક"
                                Language.HINDI -> "सभी फसलें"
                                else -> "All Crops"
                            },
                            fontWeight = if (selectedCropId == null) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("prevention_chip_all_crops")
                )

                AgriculturalKnowledgeBase.crops.forEach { crop ->
                    FilterChip(
                        selected = selectedCropId == crop.id,
                        onClick = {
                            if (selectedCropId == crop.id) onCropSelect(null)
                            else onCropSelect(crop.id)
                        },
                        label = {
                            Text(
                                text = "${crop.emoji} ${crop.getLocalizedName(language)}",
                                fontWeight = if (selectedCropId == crop.id) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("prevention_chip_${crop.id}")
                    )
                }
            }

            // Secondary Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "બધા વર્ગ"
                        Language.HINDI -> "सभी वर्ग"
                        else -> "All Types"
                    },
                    selected = selectedCategory == null && !showOnlyBookmarked,
                    onClick = {
                        onCategorySelect(null)
                        if (showOnlyBookmarked) onToggleBookmarkOnly()
                    }
                )

                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "⭐ મનપસંદ"
                        Language.HINDI -> "⭐ पसंदीदा"
                        else -> "⭐ Bookmarked"
                    },
                    selected = showOnlyBookmarked,
                    onClick = onToggleBookmarkOnly
                )

                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "🍄 ફૂગજન્ય રોગ"
                        Language.HINDI -> "🍄 फफूंद जनित"
                        else -> "🍄 Fungal Blight"
                    },
                    selected = selectedCategory == "FUNGAL",
                    onClick = {
                        if (selectedCategory == "FUNGAL") onCategorySelect(null)
                        else onCategorySelect("FUNGAL")
                    }
                )

                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "🐛 જીવાત-કીટક"
                        Language.HINDI -> "🐛 कीट प्रकोप"
                        else -> "🐛 Pest Infestation"
                    },
                    selected = selectedCategory == "PEST",
                    onClick = {
                        if (selectedCategory == "PEST") onCategorySelect(null)
                        else onCategorySelect("PEST")
                    }
                )

                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "🦠 વિષાણુ (વાયરસ)"
                        Language.HINDI -> "🦠 वायरस रोग"
                        else -> "🦠 Viral Disease"
                    },
                    selected = selectedCategory == "VIRAL",
                    onClick = {
                        if (selectedCategory == "VIRAL") onCategorySelect(null)
                        else onCategorySelect("VIRAL")
                    }
                )

                CategoryPill(
                    label = when (language) {
                        Language.GUJARATI -> "🌾 પોષકતત્વ ખામી"
                        Language.HINDI -> "🌾 पोषण कमी"
                        else -> "🌾 Deficiency"
                    },
                    selected = selectedCategory == "DEFICIENCY",
                    onClick = {
                        if (selectedCategory == "DEFICIENCY") onCategorySelect(null)
                        else onCategorySelect("DEFICIENCY")
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main List of Prevention Tip Cards
            if (filteredTips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "કોઈ નિવારણ સલાહ મળી નથી"
                                Language.HINDI -> "कोई रोकथाम सलाह नहीं मिली"
                                else -> "No prevention tips found"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "અન્ય પાક અથવા ફિલ્ટર પસંદ કરો અથવા શોધ શબ્દ બદલો."
                                Language.HINDI -> "अन्य फसल या फ़िल्टर चुनें अथवा खोज शब्द बदलें।"
                                else -> "Try selecting another crop or clearing the search query."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = {
                                onSearchQueryChange("")
                                onCropSelect(null)
                                onCategorySelect(null)
                                if (showOnlyBookmarked) onToggleBookmarkOnly()
                            }
                        ) {
                            Text(
                                text = when (language) {
                                    Language.GUJARATI -> "બધા ફિલ્ટર હટાવો"
                                    Language.HINDI -> "सभी फ़िल्टर हटाएं"
                                    else -> "Reset all filters"
                                }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("prevention_tips_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredTips, key = { it.id }) { tip ->
                        PreventionTipCard(
                            tip = tip,
                            language = language,
                            isSpeaking = isSpeaking && activeUtterance == "tip_${tip.id}",
                            onBookmarkToggle = { onToggleBookmark(tip.id, tip.isBookmarked) },
                            onSpeakClick = {
                                if (isSpeaking && activeUtterance == "tip_${tip.id}") {
                                    onStopSpeaking()
                                } else {
                                    onSpeakTip(tip)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Prevention Tip Card with expandable agronomic detail sections
 */
@Composable
private fun PreventionTipCard(
    tip: PreventionTipEntity,
    language: Language,
    isSpeaking: Boolean,
    onBookmarkToggle: () -> Unit,
    onSpeakClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val cropName = when (language) {
        Language.GUJARATI -> tip.cropNameGu
        Language.HINDI -> tip.cropNameHi
        else -> tip.cropNameEn
    }
    val diseaseName = when (language) {
        Language.GUJARATI -> tip.diseaseNameGu
        Language.HINDI -> tip.diseaseNameHi
        else -> tip.diseaseNameEn
    }
    val symptoms = when (language) {
        Language.GUJARATI -> tip.symptomsSummaryGu
        Language.HINDI -> tip.symptomsSummaryHi
        else -> tip.symptomsSummaryEn
    }
    val prevention = when (language) {
        Language.GUJARATI -> tip.preventionGu
        Language.HINDI -> tip.preventionHi
        else -> tip.preventionEn
    }
    val bioIpm = when (language) {
        Language.GUJARATI -> tip.biologicalIpmGu
        Language.HINDI -> tip.biologicalIpmHi
        else -> tip.biologicalIpmEn
    }
    val cultural = when (language) {
        Language.GUJARATI -> tip.culturalPracticesGu
        Language.HINDI -> tip.culturalPracticesHi
        else -> tip.culturalPracticesEn
    }

    val categoryColor = when (tip.category) {
        "FUNGAL" -> Color(0xFFE65100)
        "PEST" -> Color(0xFFC2185B)
        "VIRAL" -> Color(0xFF7B1FA2)
        "DEFICIENCY" -> Color(0xFF0277BD)
        else -> Color(0xFF2E7D32)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tip_card_${tip.id}")
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Crop pill + Category pill + Bookmark button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Crop Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = cropName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = when (tip.category) {
                                "FUNGAL" -> if (language == Language.GUJARATI) "ફૂગજન્ય" else if (language == Language.HINDI) "फफूंद" else "Fungal"
                                "PEST" -> if (language == Language.GUJARATI) "જીવાત" else if (language == Language.HINDI) "कीट" else "Pest"
                                "VIRAL" -> if (language == Language.GUJARATI) "વાયરસ" else if (language == Language.HINDI) "विषाणु" else "Viral"
                                "DEFICIENCY" -> if (language == Language.GUJARATI) "ખામી" else if (language == Language.HINDI) "कमी" else "Deficiency"
                                else -> tip.category
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bookmark action button
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_tip_btn_${tip.id}")
                ) {
                    Icon(
                        imageVector = if (tip.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (tip.isBookmarked) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Disease Title
            Text(
                text = diseaseName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Preventive Method Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = when (language) {
                                Language.GUJARATI -> "અગમચેતી અને નિવારણ પગલાં:"
                                Language.HINDI -> "रोकथाम व बचाव के उपाय:"
                                else -> "Primary Preventive Actions:"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = prevention,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Expandable details (Symptoms, Biological IPM, Cultural Practices)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Symptoms section
                    DetailSubSection(
                        icon = Icons.Default.Visibility,
                        title = when (language) {
                            Language.GUJARATI -> "શું લક્ષણો દેખાશે (ઓળખ):"
                            Language.HINDI -> "लक्षण व पहचान:"
                            else -> "Symptoms & Identification:"
                        },
                        content = symptoms,
                        tintColor = Color(0xFF1976D2)
                    )

                    // Biological & IPM Section
                    DetailSubSection(
                        icon = Icons.Default.Eco,
                        title = when (language) {
                            Language.GUJARATI -> "જૈવિક અને કુદરતી ઉપચાર (IPM):"
                            Language.HINDI -> "जैविक एवं प्राकृतिक उपचार (IPM):"
                            else -> "Biological & Organic IPM:"
                        },
                        content = bioIpm,
                        tintColor = Color(0xFF388E3C)
                    )

                    // Cultural practices
                    DetailSubSection(
                        icon = Icons.Default.Agriculture,
                        title = when (language) {
                            Language.GUJARATI -> "ખેતી પદ્ધતિઓ અને સાવચેતી:"
                            Language.HINDI -> "कृषि पद्धतियां व सावधानी:"
                            else -> "Cultural & Field Sanitation:"
                        },
                        content = cultural,
                        tintColor = Color(0xFF795548)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Expand/Collapse toggle & Listen voice readout button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Expand / Collapse toggle button
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("expand_tip_btn_${tip.id}")
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expanded) {
                            when (language) {
                                Language.GUJARATI -> "ઓછું દર્શાવો"
                                Language.HINDI -> "कम देखें"
                                else -> "Show Less"
                            }
                        } else {
                            when (language) {
                                Language.GUJARATI -> "સંપૂર્ણ વિગત જુઓ"
                                Language.HINDI -> "पूरी जानकारी देखें"
                                else -> "View Details"
                            }
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Listen Audio TTS Button
                FilledTonalButton(
                    onClick = onSpeakClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isSpeaking) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("speak_tip_btn_${tip.id}")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen Aloud",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpeaking) {
                            when (language) {
                                Language.GUJARATI -> "બંધ કરો"
                                Language.HINDI -> "रोकें"
                                else -> "Stop"
                            }
                        } else {
                            when (language) {
                                Language.GUJARATI -> "સાંભળો"
                                Language.HINDI -> "सुनें"
                                else -> "Listen"
                            }
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSubSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    tintColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = tintColor
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
