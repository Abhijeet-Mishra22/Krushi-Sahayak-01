package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AgriculturalKnowledgeBase
import com.example.data.local.PesticidePresetsData
import com.example.model.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesticideCalculatorScreen(
    language: Language,
    initialCropId: String? = null,
    initialPresetId: String? = null,
    initialVerifiedPesticide: VerifiedPesticide? = null,
    isSpeaking: Boolean = false,
    onBackClick: () -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Selected Crop Filter
    var selectedCropFilter by remember {
        mutableStateOf(initialCropId ?: "all")
    }

    // Selected Preset (null if custom)
    val availablePresets = remember { PesticidePresetsData.presets }
    val initialPreset = remember(initialPresetId, initialCropId) {
        if (initialPresetId != null) {
            availablePresets.find { it.id == initialPresetId }
        } else if (initialCropId != null) {
            availablePresets.find { it.cropId == initialCropId }
        } else {
            availablePresets.firstOrNull()
        }
    }

    var selectedPreset by remember { mutableStateOf(initialPreset) }
    var isCustomMode by remember { mutableStateOf(false) }

    // Calculation Inputs
    var areaValueStr by remember { mutableStateOf("1.0") }
    var selectedAreaUnit by remember {
        mutableStateOf(if (language == Language.GUJARATI) AreaUnit.VIGHA_GUJARAT else AreaUnit.ACRE)
    }

    var selectedPumpType by remember { mutableStateOf(SprayerPumpType.KNAPSACK_15L) }
    var customPumpCapacityStr by remember { mutableStateOf("15") }

    // Chemical / Formulation
    var chemicalName by remember {
        mutableStateOf(initialPreset?.tradeOrCommonName ?: "Imidacloprid 17.8% SL")
    }
    var activeIngredient by remember {
        mutableStateOf(initialPreset?.activeIngredient ?: "Imidacloprid 17.8% SL")
    }
    var formulationType by remember {
        mutableStateOf(initialPreset?.formulationType ?: ChemicalFormulationType.LIQUID)
    }
    var dosageInputMode by remember {
        mutableStateOf(DosageInputMode.PER_15L_PUMP)
    }
    var doseValueStr by remember {
        mutableStateOf(initialPreset?.dosePer15LPump?.toString() ?: "4.0")
    }
    var waterPerAcreStr by remember { mutableStateOf("180") }

    // Optional Cost Estimator
    var showCostEstimator by remember { mutableStateOf(false) }
    var bottlePriceStr by remember { mutableStateOf("") }
    var bottleSizeStr by remember { mutableStateOf("100") }

    var toxicityBand by remember {
        mutableStateOf(initialPreset?.toxicityBand ?: ToxicityBand.GREEN)
    }
    var phiDays by remember {
        mutableStateOf(initialPreset?.phiDays ?: "15 days")
    }

    // When initialVerifiedPesticide is provided (e.g. opened from DiagnosisResultScreen)
    LaunchedEffect(initialVerifiedPesticide) {
        if (initialVerifiedPesticide != null) {
            val matchingPreset = availablePresets.find {
                it.activeIngredient.contains(initialVerifiedPesticide.activeIngredient, ignoreCase = true) ||
                        it.cropId.equals(initialVerifiedPesticide.cropId, ignoreCase = true)
            }
            if (matchingPreset != null) {
                selectedPreset = matchingPreset
                selectedCropFilter = matchingPreset.cropId
                chemicalName = matchingPreset.tradeOrCommonName
                activeIngredient = matchingPreset.activeIngredient
                formulationType = matchingPreset.formulationType
                doseValueStr = matchingPreset.dosePer15LPump.toString()
                toxicityBand = matchingPreset.toxicityBand
                phiDays = matchingPreset.phiDays
                isCustomMode = false
            } else {
                chemicalName = initialVerifiedPesticide.activeIngredient
                activeIngredient = initialVerifiedPesticide.activeIngredient
                val isPowder = initialVerifiedPesticide.formulation.contains("WP", ignoreCase = true) ||
                        initialVerifiedPesticide.formulation.contains("Powder", ignoreCase = true)
                formulationType = if (isPowder) ChemicalFormulationType.POWDER else ChemicalFormulationType.LIQUID
                phiDays = initialVerifiedPesticide.preHarvestIntervalDays
                isCustomMode = true
            }
        }
    }

    // React to preset selection
    fun applyPreset(preset: PesticidePreset) {
        selectedPreset = preset
        isCustomMode = false
        chemicalName = preset.tradeOrCommonName
        activeIngredient = preset.activeIngredient
        formulationType = preset.formulationType
        toxicityBand = preset.toxicityBand
        phiDays = preset.phiDays
        when (dosageInputMode) {
            DosageInputMode.PER_15L_PUMP -> doseValueStr = preset.dosePer15LPump.toString()
            DosageInputMode.PER_LITER_WATER -> doseValueStr = preset.dosePerLiter.toString()
            DosageInputMode.PER_ACRE -> doseValueStr = preset.dosePerAcre.toString()
        }
        waterPerAcreStr = preset.standardWaterPerAcre.toInt().toString()
        if (preset.typicalPricePerPack != null) {
            bottlePriceStr = preset.typicalPricePerPack.toInt().toString()
            bottleSizeStr = preset.typicalBottleSizes.firstOrNull()?.toInt()?.toString() ?: "100"
        }
    }

    // Parse input numbers safely
    val parsedArea = areaValueStr.toDoubleOrNull() ?: 1.0
    val parsedPumpCapacity = if (selectedPumpType == SprayerPumpType.CUSTOM) {
        customPumpCapacityStr.toDoubleOrNull() ?: 15.0
    } else {
        selectedPumpType.defaultCapacityLiters
    }
    val parsedDose = doseValueStr.toDoubleOrNull() ?: 4.0
    val parsedWaterPerAcre = waterPerAcreStr.toDoubleOrNull() ?: 180.0
    val parsedBottlePrice = bottlePriceStr.toDoubleOrNull()
    val parsedBottleSize = bottleSizeStr.toDoubleOrNull()

    // Calculate result dynamically
    val calculationResult = remember(
        parsedArea,
        selectedAreaUnit,
        parsedPumpCapacity,
        formulationType,
        dosageInputMode,
        parsedDose,
        parsedWaterPerAcre,
        parsedBottlePrice,
        parsedBottleSize,
        toxicityBand,
        phiDays,
        language
    ) {
        PesticideDosageCalculatorEngine.calculate(
            areaValue = parsedArea,
            areaUnit = selectedAreaUnit,
            tankCapacityLiters = parsedPumpCapacity,
            formulationType = formulationType,
            inputMode = dosageInputMode,
            enteredDoseValue = parsedDose,
            waterPerAcreLiters = parsedWaterPerAcre,
            bottlePrice = parsedBottlePrice,
            bottleSize = parsedBottleSize,
            toxicityBand = toxicityBand,
            phiDays = phiDays,
            language = language
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Translations.pesticideCalculatorTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ICAR & CIBRC Calibrated • 100% Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("calculator_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Audio voice speech button
                    IconButton(
                        onClick = {
                            if (isSpeaking) {
                                onStopSpeaking()
                            } else {
                                val speechText = buildSpokenSummary(calculationResult, chemicalName, language)
                                onSpeakText(speechText)
                            }
                        },
                        modifier = Modifier.testTag("calculator_speak_btn")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak calculation",
                            tint = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareText = buildShareText(calculationResult, chemicalName, language)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Spray Calculation"))
                        },
                        modifier = Modifier.testTag("calculator_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Screen Subtitle & Quick Info Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🧪", fontSize = 24.sp, modifier = Modifier.padding(end = 10.dp))
                    Text(
                        text = Translations.pesticideCalculatorSubtitle(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ==========================================
            // STEP 1: CHEMICAL SELECTION (PRESET OR CUSTOM)
            // ==========================================
            Text(
                text = Translations.selectPresetOrCustom(language),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Crop Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCropFilter == "all",
                        onClick = { selectedCropFilter = "all" },
                        label = { Text(if (language == Language.GUJARATI) "બધા પાક" else if (language == Language.HINDI) "सभी फसलें" else "All Crops") },
                        modifier = Modifier.testTag("calc_crop_filter_all")
                    )
                }
                items(AgriculturalKnowledgeBase.crops) { crop ->
                    FilterChip(
                        selected = selectedCropFilter == crop.id,
                        onClick = { selectedCropFilter = crop.id },
                        label = { Text("${crop.emoji} ${crop.getLocalizedName(language)}") },
                        modifier = Modifier.testTag("calc_crop_filter_${crop.id}")
                    )
                }
            }

            // Preset Chemicals Carousel
            val filteredPresets = remember(selectedCropFilter) {
                if (selectedCropFilter == "all") availablePresets
                else availablePresets.filter { it.cropId.equals(selectedCropFilter, ignoreCase = true) || it.cropId == "all" }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                // Preset Items
                items(filteredPresets) { preset ->
                    val isSelected = !isCustomMode && selectedPreset?.id == preset.id
                    Card(
                        onClick = { applyPreset(preset) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                        ) else CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .width(220.dp)
                            .testTag("preset_card_${preset.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${preset.cropEmoji} ${preset.getLocalizedCropName(language)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = preset.toxicityBand.color.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = preset.formulationType.unit,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = preset.toxicityBand.color,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.tradeOrCommonName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = preset.targetPestOrDisease,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Standard: ${preset.dosePer15LPump} ${preset.formulationType.unit} / 15L",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Custom chemical option chip card
                item {
                    Card(
                        onClick = {
                            isCustomMode = true
                            selectedPreset = null
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCustomMode) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isCustomMode) CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.secondary)
                        ) else CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .width(180.dp)
                            .testTag("preset_card_custom")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✏️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Translations.customChemical(language),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (language == Language.GUJARATI) "દવાની વિગત જાતે લખો"
                                else if (language == Language.HINDI) "दवा की जानकारी स्वयं भरें"
                                else "Enter your own chemical",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Custom Chemical Inputs (if custom mode active)
            AnimatedVisibility(visible = isCustomMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = chemicalName,
                            onValueChange = { chemicalName = it },
                            label = { Text("Chemical / Product Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calc_custom_chem_name"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Formulation:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FilterChip(
                                selected = formulationType == ChemicalFormulationType.LIQUID,
                                onClick = { formulationType = ChemicalFormulationType.LIQUID },
                                label = { Text("💧 Liquid (ml)") }
                            )
                            FilterChip(
                                selected = formulationType == ChemicalFormulationType.POWDER,
                                onClick = { formulationType = ChemicalFormulationType.POWDER },
                                label = { Text("🧂 Powder (gm)") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // STEP 2: FIELD SIZE & AREA (જમીનનું ક્ષેત્રફળ)
            // ==========================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("calc_field_area_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Translations.fieldSizeSection(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.2f", calculationResult.areaInAcres)} Acres",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Area Input with Stepper Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                val current = areaValueStr.toDoubleOrNull() ?: 1.0
                                if (current > 0.5) {
                                    areaValueStr = String.format(Locale.US, "%.1f", current - 0.5)
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("calc_area_minus_btn")
                        ) {
                            Text(text = "-0.5", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedTextField(
                            value = areaValueStr,
                            onValueChange = { areaValueStr = it },
                            label = { Text("Area (${selectedAreaUnit.symbol})") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_area_input"),
                            singleLine = true
                        )

                        FilledTonalIconButton(
                            onClick = {
                                val current = areaValueStr.toDoubleOrNull() ?: 1.0
                                areaValueStr = String.format(Locale.US, "%.1f", current + 0.5)
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("calc_area_plus_btn")
                        ) {
                            Text(text = "+0.5", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Area Unit Selector Chips
                    Text(
                        text = "Unit (માપ એકમ):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(AreaUnit.values()) { unit ->
                            FilterChip(
                                selected = selectedAreaUnit == unit,
                                onClick = { selectedAreaUnit = unit },
                                label = { Text(unit.getDisplayName(language), fontSize = 12.sp) },
                                modifier = Modifier.testTag("calc_unit_${unit.name}")
                            )
                        }
                    }
                }
            }

            // ==========================================
            // STEP 3: SPRAYER PUMP CAPACITY (પંપ ક્ષમતા)
            // ==========================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("calc_pump_capacity_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.sprayerPumpSection(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SprayerPumpType.values()) { pump ->
                            FilterChip(
                                selected = selectedPumpType == pump,
                                onClick = { selectedPumpType = pump },
                                label = { Text(pump.getDisplayName(language), fontSize = 12.sp) },
                                modifier = Modifier.testTag("calc_pump_${pump.name}")
                            )
                        }
                    }

                    if (selectedPumpType == SprayerPumpType.CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customPumpCapacityStr,
                            onValueChange = { customPumpCapacityStr = it },
                            label = { Text("Custom Tank Volume (Liters)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calc_custom_pump_capacity"),
                            singleLine = true
                        )
                    }
                }
            }

            // ==========================================
            // STEP 4: DOSAGE & DILUTION SETTINGS
            // ==========================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("calc_dosage_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.dosageSettingsSection(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Mode Tabs (Per 15L Pump / Per Liter / Per Acre)
                    TabRow(
                        selectedTabIndex = dosageInputMode.ordinal,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = dosageInputMode == DosageInputMode.PER_15L_PUMP,
                            onClick = { dosageInputMode = DosageInputMode.PER_15L_PUMP },
                            text = { Text("Per 15L Pump", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = dosageInputMode == DosageInputMode.PER_LITER_WATER,
                            onClick = { dosageInputMode = DosageInputMode.PER_LITER_WATER },
                            text = { Text("Per Liter", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = dosageInputMode == DosageInputMode.PER_ACRE,
                            onClick = { dosageInputMode = DosageInputMode.PER_ACRE },
                            text = { Text("Per Acre", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dose Input with Quick Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                val current = doseValueStr.toDoubleOrNull() ?: 1.0
                                val step = if (dosageInputMode == DosageInputMode.PER_LITER_WATER) 0.25 else 1.0
                                if (current > step) {
                                    doseValueStr = String.format(Locale.US, "%.2f", current - step)
                                }
                            },
                            modifier = Modifier.size(46.dp)
                        ) {
                            Text(text = "-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        OutlinedTextField(
                            value = doseValueStr,
                            onValueChange = { doseValueStr = it },
                            label = {
                                val unit = formulationType.unit
                                val labelText = when (dosageInputMode) {
                                    DosageInputMode.PER_15L_PUMP -> "Dose ($unit / 15L pump)"
                                    DosageInputMode.PER_LITER_WATER -> "Dose ($unit / Liter water)"
                                    DosageInputMode.PER_ACRE -> "Dose ($unit / Acre)"
                                }
                                Text(labelText)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_dose_input"),
                            singleLine = true
                        )

                        FilledTonalIconButton(
                            onClick = {
                                val current = doseValueStr.toDoubleOrNull() ?: 1.0
                                val step = if (dosageInputMode == DosageInputMode.PER_LITER_WATER) 0.25 else 1.0
                                doseValueStr = String.format(Locale.US, "%.2f", current + step)
                            },
                            modifier = Modifier.size(46.dp)
                        ) {
                            Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Water volume per acre
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Water Volume (પાણી પ્રતિ એકર):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = waterPerAcreStr,
                            onValueChange = { waterPerAcreStr = it },
                            modifier = Modifier
                                .width(90.dp)
                                .height(50.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Text(text = "L/Acre", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // ==========================================
            // OPTIONAL STEP 5: COST ESTIMATOR
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCostEstimator = !showCostEstimator }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💰", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.priceEstimatorOptional(language),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (showCostEstimator) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(visible = showCostEstimator) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = bottlePriceStr,
                                    onValueChange = { bottlePriceStr = it },
                                    label = { Text("Pack Price (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("calc_bottle_price"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = bottleSizeStr,
                                    onValueChange = { bottleSizeStr = it },
                                    label = { Text("Pack Size (${formulationType.unit})") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("calc_bottle_size"),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // HERO CALCULATION SUMMARY CARD (THE ACCURACY SHOWCASE!)
            // =========================================================
            CalculationResultHeroCard(
                result = calculationResult,
                chemicalName = chemicalName,
                activeIngredient = activeIngredient,
                language = language,
                onCopySummary = {
                    val summary = buildShareText(calculationResult, chemicalName, language)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Dosage Calculation", summary))
                    Toast.makeText(context, "Calculation copied to clipboard! / નકલ થઈ ગઈ", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================
            // CIBRC TOXICITY BAND CARD & SAFE HANDLING
            // =========================================================
            ToxicityBandCard(band = toxicityBand, language = language)

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================
            // SAFE TANK MIXING SEQUENCE (5 STEPS)
            // =========================================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧪", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.safeMixingSequenceTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    calculationResult.mixingSequence.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }

            // Field Safety Checklist
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.safetyInstructions(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    calculationResult.safetyTips.forEach { tip ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(text = "• ", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Beautiful high-visibility calculation result hero card.
 */
@Composable
private fun CalculationResultHeroCard(
    result: DosageCalculationResult,
    chemicalName: String,
    activeIngredient: String,
    language: Language,
    onCopySummary: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calc_hero_result_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Translations.calculationSummary(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = result.userAreaDisplay,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Copy button
                IconButton(
                    onClick = onCopySummary,
                    modifier = Modifier.testTag("calc_copy_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Summary",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Main Highlights Grid
            // 1. Total Chemical Required (BIG HERO NUMBER)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = Translations.totalChemicalNeeded(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = chemicalName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val formattedChem = if (result.totalChemicalRequired >= 1000.0) {
                            val inKgOrL = result.totalChemicalRequired / 1000.0
                            val unitStr = if (result.formulationType == ChemicalFormulationType.LIQUID) "L" else "kg"
                            "${String.format(Locale.US, "%.2f", inKgOrL)} $unitStr"
                        } else {
                            "${String.format(Locale.US, "%.1f", result.totalChemicalRequired)} ${result.chemicalUnit}"
                        }

                        Text(
                            text = formattedChem,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 2. Chemical Per Pump & Measuring Cap
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Translations.dosePerPumpLabel(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", result.chemicalDosePerPump)} ${result.chemicalUnit} / ${result.pumpCapacityLiters.toInt()}L Pump",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "🧢 ${result.measuringCapGuidance}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 3. Total Pumps & Water
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = Translations.totalPumpsLabel(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🎒 ${result.totalPumpsRounded} Pumps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(${String.format(Locale.US, "%.1f", result.totalPumpsCountExact)} exact)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = Translations.totalWaterLabel(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💧 ${String.format(Locale.US, "%.0f", result.totalWaterLiters)} Liters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${result.pumpCapacityLiters.toInt()}L per tank",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cost summary if calculated
            if (result.totalCost != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Estimated Application Cost:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            if (result.costPerAcre != null) {
                                Text(
                                    text = "₹${String.format(Locale.US, "%.0f", result.costPerAcre)} / Acre",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Text(
                            text = "₹${String.format(Locale.US, "%.0f", result.totalCost)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Pre-Harvest Interval (PHI)
            if (result.phiDays != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🥬", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                text = Translations.waitingPeriod(language),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${result.phiDays} before harvest",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * CIBRC official toxicity classification badge.
 */
@Composable
private fun ToxicityBandCard(band: ToxicityBand, language: Language) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(band.color)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = Translations.toxicityBandNotice(language),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = band.color.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = band.getLocalizedLabel(language),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = band.color
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = band.getLocalizedCaution(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun buildSpokenSummary(
    result: DosageCalculationResult,
    chemicalName: String,
    language: Language
): String {
    val doseRounded = String.format(Locale.US, "%.1f", result.chemicalDosePerPump)
    val chemTotal = String.format(Locale.US, "%.1f", result.totalChemicalRequired)
    val waterTotal = String.format(Locale.US, "%.0f", result.totalWaterLiters)

    return when (language) {
        Language.GUJARATI -> {
            "${result.userAreaDisplay} માટે, કુલ $chemTotal ${result.chemicalUnit} $chemicalName ની જરૂર છે. " +
                    "દરેક ${result.pumpCapacityLiters.toInt()} લિટર પંપમાં $doseRounded ${result.chemicalUnit} દવા નાખો. " +
                    "કુલ ${result.totalPumpsRounded} પંપ છાંટવાના રહેશે અને કુલ $waterTotal લિટર ચોખ્ખું પાણી જોઈશે. " +
                    "પવનની દિશામાં જ છંટકાવ કરવો અને માસ્ક પહેરવો."
        }
        Language.HINDI -> {
            "${result.userAreaDisplay} के लिए, कुल $chemTotal ${result.chemicalUnit} $chemicalName की आवश्यकता है। " +
                    "प्रति ${result.pumpCapacityLiters.toInt()} लीटर स्प्रे पंप में $doseRounded ${result.chemicalUnit} दवा डालें। " +
                    "कुल ${result.totalPumpsRounded} पंप का छिड़काव होगा और कुल $waterTotal लीटर पानी लगेगा। " +
                    "मास्क पहनकर हवा की दिशा में ही छिड़काव करें।"
        }
        Language.ENGLISH -> {
            "For ${result.userAreaDisplay}, you need total $chemTotal ${result.chemicalUnit} of $chemicalName. " +
                    "Add $doseRounded ${result.chemicalUnit} per ${result.pumpCapacityLiters.toInt()} Litre spray pump. " +
                    "Total ${result.totalPumpsRounded} sprayer pumps required with $waterTotal Litres clean water. " +
                    "Wear protective mask and spray along the wind direction."
        }
    }
}

private fun buildShareText(
    result: DosageCalculationResult,
    chemicalName: String,
    language: Language
): String {
    val doseRounded = String.format(Locale.US, "%.1f", result.chemicalDosePerPump)
    val chemTotal = String.format(Locale.US, "%.1f", result.totalChemicalRequired)
    val waterTotal = String.format(Locale.US, "%.0f", result.totalWaterLiters)

    return """
        🌾 *Krushi Sahayak - Spray Dosage Calculation*
        🧪 Chemical: $chemicalName
        📍 Field Area: ${result.userAreaDisplay}
        ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        ✅ Dose per Pump: $doseRounded ${result.chemicalUnit} / ${result.pumpCapacityLiters.toInt()}L
        🎒 Total Pumps: ${result.totalPumpsRounded} Pumps
        💧 Total Clean Water: $waterTotal Liters
        📦 Total Chemical: $chemTotal ${result.chemicalUnit}
        🧢 Measuring Cap: ${result.measuringCapGuidance}
        🥬 Waiting Period (PHI): ${result.phiDays ?: "N/A"}
        ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        🛡️ Safety: Always spray in morning/evening downwind with protective gear.
        Generated with Krushi Sahayak (Offline Agriculture App)
    """.trimIndent()
}
