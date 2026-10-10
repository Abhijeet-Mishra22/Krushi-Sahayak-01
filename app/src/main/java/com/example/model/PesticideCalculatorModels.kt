package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.Locale

/**
 * Area units standard in Indian Agriculture (especially Gujarat, Central & North India).
 */
enum class AreaUnit(
    val conversionToAcre: Double,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val symbol: String
) {
    VIGHA_GUJARAT(0.40, "Vigha (Gujarat - 16 Guntha)", "वीघा (गुजरात - 16 गुंठा)", "વીઘા (ગુજરાત - ૧૬ ગૂંઠા)", "vigha"),
    ACRE(1.0, "Acre (2.5 Vigha)", "एकड़ (2.5 वीघा)", "એકર (૨.૫ વીઘા)", "acre"),
    BIGHA_NORTH(0.625, "Bigha (North India - 25 Guntha)", "बीघा (उत्तर भारत)", "બીઘા (ઉત્તર ભારત)", "bigha"),
    GUNTHA(0.025, "Guntha (1/40 Acre)", "गुंठा (1/40 एकड़)", "ગૂંઠા (૧/૪૦ એકર)", "guntha"),
    HECTARE(2.471, "Hectare (10,000 m²)", "हेक्टेयर (10,000 वर्ग मी.)", "હેક્ટર (૧૦,૦૦૦ ચો.મી.)", "ha");

    fun getDisplayName(language: Language): String = when (language) {
        Language.GUJARATI -> nameGu
        Language.HINDI -> nameHi
        Language.ENGLISH -> nameEn
    }
}

/**
 * Common agricultural sprayer pumps in India.
 */
enum class SprayerPumpType(
    val defaultCapacityLiters: Double,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val iconEmoji: String
) {
    KNAPSACK_15L(15.0, "15L Manual Knapsack Pump", "15 लीटर हाथ का पंप", "૧૫ લિટર મેન્યુઅલ પંપ", "🎒"),
    KNAPSACK_16L(16.0, "16L Standard Knapsack", "16 लीटर नैपसैक पंप", "૧૬ લિટર પ્રમાણિત પંપ", "🎒"),
    BATTERY_20L(20.0, "20L Battery Sprayer", "20 लीटर बैटरी पंप", "૨૦ લિટર બેટરી પંપ", "🔋"),
    POWER_25L(25.0, "25L Power Sprayer", "25 लीटर पावर स्प्रेयर", "૨૫ લિટર પાવર સ્પ્રેયર", "⚡"),
    TRACTOR_200L(200.0, "200L Drum / Tractor Tank", "200 लीटर ड्रम / ट्रैक्टर टैंक", "૨૦૦ લિટર ડ્રમ / ટ્રેક્ટર", "🚜"),
    CUSTOM(15.0, "Custom Capacity", "अन्य क्षमता", "અન્ય ક્ષમતા", "⚙️");

    fun getDisplayName(language: Language): String = when (language) {
        Language.GUJARATI -> "$iconEmoji $nameGu"
        Language.HINDI -> "$iconEmoji $nameHi"
        Language.ENGLISH -> "$iconEmoji $nameEn"
    }
}

/**
 * Official Indian CIBRC toxicity classification bands on pesticide containers.
 */
enum class ToxicityBand(
    val color: Color,
    val labelEn: String,
    val labelHi: String,
    val labelGu: String,
    val cautionTextEn: String,
    val cautionTextHi: String,
    val cautionTextGu: String
) {
    GREEN(
        color = Color(0xFF2E7D32),
        labelEn = "Green Triangle - Slightly Toxic",
        labelHi = "हरा त्रिकोण - कम विषाक्त (सावधानी)",
        labelGu = "લીલો ત્રિકોણ - ઓછું ઝેરી (સાવચેતી)",
        cautionTextEn = "CAUTION: Relatively safe, mild toxicity. Standard PPE recommended.",
        cautionTextHi = "सावधानी: अपेक्षाकृत सुरक्षित, जैविक या कम विषाक्त। सामान्य दस्ताने पहनें।",
        cautionTextGu = "સાવચેતી: પ્રમાણમાં સુરક્ષિત, ઓછું ઝેરી. સામાન્ય મોજા અને માસ્ક વાપરો."
    ),
    BLUE(
        color = Color(0xFF1565C0),
        labelEn = "Blue Triangle - Moderately Toxic",
        labelHi = "नीला त्रिकोण - मध्यम विषाक्त (खतरा)",
        labelGu = "વાદળી ત્રિકોણ - મધ્યમ ઝેરી (જોખમ)",
        cautionTextEn = "DANGER: Moderately toxic. Wear full protective mask and gloves.",
        cautionTextHi = "खतरा: मध्यम विषैला। मुंह पर मास्क और रबर के दस्ताने अनिवार्य।",
        cautionTextGu = "જોખમ: મધ્યમ ઝેરી. મોં પર માસ્ક અને રબરના મોજા પહેરવા ફરજિયાત."
    ),
    YELLOW(
        color = Color(0xFFF57F17),
        labelEn = "Yellow Triangle - Highly Toxic",
        labelHi = "पीला त्रिकोण - अत्यधिक विषाक्त (विष)",
        labelGu = "પીળો ત્રિકોણ - ભારે ઝેરી (ઝેર)",
        cautionTextEn = "POISON: Highly toxic. Handle with extreme caution. Keep antidote ready.",
        cautionTextHi = "विष: बहुत तेज दवा! हवा के विपरीत छिड़काव न करें। बच्चों से दूर रखें।",
        cautionTextGu = "ઝેર: અતિશય ઝેરી દવા! પવનની સામે છંટકાવ ન કરવો. બાળકોથી ખાસ દૂર રાખવી."
    ),
    RED(
        color = Color(0xFFC62828),
        labelEn = "Red Triangle - Extremely Toxic",
        labelHi = "लाल त्रिकोण - अत्यंत विषैला (खतरनाक जहर)",
        labelGu = "લાલ ત્રિકોણ - અત્યંત ઝેરી (ખતરનાક ઝેર)",
        cautionTextEn = "SKULL & POISON: Extremely dangerous. Professional application only.",
        cautionTextHi = "अत्यंत विषैला: बहुत खतरनाक! केवल प्रशिक्षित व्यक्ति ही छिड़कें।",
        cautionTextGu = "અત્યંત ઝેરી: ખૂબ જ જોખમી! કાળજીપૂર્વક છંટકાવ કરવો."
    );

    fun getLocalizedLabel(language: Language): String = when (language) {
        Language.GUJARATI -> labelGu
        Language.HINDI -> labelHi
        Language.ENGLISH -> labelEn
    }

    fun getLocalizedCaution(language: Language): String = when (language) {
        Language.GUJARATI -> cautionTextGu
        Language.HINDI -> cautionTextHi
        Language.ENGLISH -> cautionTextEn
    }
}

enum class ChemicalFormulationType(val unit: String, val packagingUnit: String) {
    LIQUID("ml", "Litre / ml"),
    POWDER("gm", "Kg / gm")
}

enum class DosageInputMode {
    PER_15L_PUMP,
    PER_LITER_WATER,
    PER_ACRE
}

/**
 * Pre-configured verified chemical preset for easy one-tap calculations.
 */
data class PesticidePreset(
    val id: String,
    val cropId: String,
    val cropNameEn: String,
    val cropNameHi: String,
    val cropNameGu: String,
    val cropEmoji: String,
    val tradeOrCommonName: String,
    val activeIngredient: String,
    val targetPestOrDisease: String,
    val formulationType: ChemicalFormulationType,
    val dosePer15LPump: Double, // in ml or gm
    val dosePerLiter: Double,   // in ml or gm
    val dosePerAcre: Double,    // in ml or gm
    val standardWaterPerAcre: Double = 180.0, // Liters
    val toxicityBand: ToxicityBand,
    val phiDays: String,
    val typicalBottleSizes: List<Double> = listOf(100.0, 250.0, 500.0, 1000.0), // ml or gm
    val typicalPricePerPack: Double? = null,
    val specialAdvisoryGu: String? = null,
    val specialAdvisoryHi: String? = null,
    val specialAdvisoryEn: String? = null
) {
    fun getLocalizedCropName(language: Language): String = when (language) {
        Language.GUJARATI -> cropNameGu
        Language.HINDI -> cropNameHi
        Language.ENGLISH -> cropNameEn
    }

    fun getLocalizedSpecialAdvisory(language: Language): String? = when (language) {
        Language.GUJARATI -> specialAdvisoryGu
        Language.HINDI -> specialAdvisoryHi
        Language.ENGLISH -> specialAdvisoryEn
    }
}

/**
 * Complete calculated dosage breakdown for farmers.
 */
data class DosageCalculationResult(
    val areaInAcres: Double,
    val userAreaDisplay: String,
    val pumpCapacityLiters: Double,
    val totalWaterLiters: Double,
    val totalPumpsCountExact: Double,
    val totalPumpsRounded: Int,
    val chemicalDosePerPump: Double,
    val chemicalDosePerLiter: Double,
    val totalChemicalRequired: Double,
    val chemicalUnit: String,
    val formulationType: ChemicalFormulationType,
    val measuringCapGuidance: String,
    val waterGuidance: String,
    val totalCost: Double?,
    val costPerAcre: Double?,
    val costPerPump: Double?,
    val toxicityBand: ToxicityBand,
    val phiDays: String?,
    val mixingSequence: List<String>,
    val safetyTips: List<String>
)

object PesticideDosageCalculatorEngine {

    /**
     * Calculates the exact dosage, water volume, pump count, measuring cups, and cost.
     */
    fun calculate(
        areaValue: Double,
        areaUnit: AreaUnit,
        tankCapacityLiters: Double,
        formulationType: ChemicalFormulationType,
        inputMode: DosageInputMode,
        enteredDoseValue: Double, // value corresponding to inputMode
        waterPerAcreLiters: Double = 180.0,
        bottlePrice: Double? = null,
        bottleSize: Double? = null,
        toxicityBand: ToxicityBand = ToxicityBand.BLUE,
        phiDays: String? = "15 days",
        language: Language = Language.GUJARATI
    ): DosageCalculationResult {
        val safeArea = if (areaValue <= 0.0) 1.0 else areaValue
        val safeTankCapacity = if (tankCapacityLiters <= 0.0) 15.0 else tankCapacityLiters
        val safeWaterPerAcre = if (waterPerAcreLiters <= 0.0) 180.0 else waterPerAcreLiters
        val safeDose = if (enteredDoseValue <= 0.0) 1.0 else enteredDoseValue

        // 1. Convert area to acres
        val areaInAcres = safeArea * areaUnit.conversionToAcre

        // 2. Total water requirement
        val totalWaterLiters = areaInAcres * safeWaterPerAcre

        // 3. Total sprayer pumps
        val totalPumpsExact = totalWaterLiters / safeTankCapacity
        val totalPumpsRounded = kotlin.math.max(1, kotlin.math.ceil(totalPumpsExact).toInt())

        // 4. Determine dose per liter, dose per pump, and total chemical
        val dosePerLiter: Double
        val dosePerPump: Double
        val totalChemical: Double

        when (inputMode) {
            DosageInputMode.PER_15L_PUMP -> {
                // Entered value is for a 15L reference pump
                val dosePerRefLiter = safeDose / 15.0
                dosePerLiter = dosePerRefLiter
                dosePerPump = dosePerRefLiter * safeTankCapacity
                totalChemical = dosePerLiter * totalWaterLiters
            }
            DosageInputMode.PER_LITER_WATER -> {
                dosePerLiter = safeDose
                dosePerPump = safeDose * safeTankCapacity
                totalChemical = safeDose * totalWaterLiters
            }
            DosageInputMode.PER_ACRE -> {
                totalChemical = safeDose * areaInAcres
                dosePerLiter = totalChemical / totalWaterLiters
                dosePerPump = dosePerLiter * safeTankCapacity
            }
        }

        val unit = formulationType.unit

        // 5. Measuring cap / spoon guidance (standard pesticide measuring caps are 25ml or 10ml)
        val capGuidance = formatMeasuringCapGuidance(dosePerPump, formulationType, language)

        // 6. Water volume guidance
        val waterGuidance = when (language) {
            Language.GUJARATI -> "કુલ ${String.format(Locale.US, "%.0f", totalWaterLiters)} લિટર ચોખ્ખું પાણી ($totalPumpsRounded પંપ)"
            Language.HINDI -> "कुल ${String.format(Locale.US, "%.0f", totalWaterLiters)} लीटर साफ पानी ($totalPumpsRounded पंप)"
            Language.ENGLISH -> "Total ${String.format(Locale.US, "%.0f", totalWaterLiters)} Liters clean water ($totalPumpsRounded pumps)"
        }

        // 7. Cost estimation
        var calculatedTotalCost: Double? = null
        var calculatedCostPerAcre: Double? = null
        var calculatedCostPerPump: Double? = null

        if (bottlePrice != null && bottlePrice > 0 && bottleSize != null && bottleSize > 0) {
            val costPerUnit = bottlePrice / bottleSize
            calculatedTotalCost = totalChemical * costPerUnit
            calculatedCostPerAcre = if (areaInAcres > 0) calculatedTotalCost / areaInAcres else null
            calculatedCostPerPump = if (totalPumpsRounded > 0) calculatedTotalCost / totalPumpsRounded else null
        }

        // 8. Mixing sequence
        val mixingSequence = when (language) {
            Language.GUJARATI -> listOf(
                "૧. ડોલમાં પહેલા અડધું પાણી લો (સીધું પંપમાં પાઉડર કે દવા ન નાખવી).",
                "૨. જો પાઉડર (WP/SP) હોય તો પહેલા ડોલમાં ઓગાળીને ઘોળ બનાવો.",
                "૩. પ્રવાહી દવા (EC/SC) ઉમેરી લાકડીથી બરાબર હલાવો.",
                "૪. જો ચોમાસું હોય તો છેલ્લે ૫ મિલી સ્ટીકર/ચીપકું (Agri-80) ઉમેરો.",
                "૫. ગરણી વડે પંપમાં રેડી બાકીનું પાણી ઉમેરી ટાંકી પૂરી ભરો."
            )
            Language.HINDI -> listOf(
                "1. बाल्टी में पहले आधा पानी लें (सीधे स्प्रे पंप में दवा न डालें)।",
                "2. यदि पाउडर (WP/SP) दवा है तो पहले बाल्टी में घोल बनाएं।",
                "3. अब तरल दवा (EC/SC) डालकर डंडे से अच्छी तरह हिलाएं।",
                "4. बारिश के मौसम में अंत में 5 मिली स्टीकर/चिपकू (Agri-80) मिलाएं।",
                "5. छलनी से छानकर पंप में डालें और बाकी पानी भरकर टंकी भरें।"
            )
            Language.ENGLISH -> listOf(
                "1. Fill a bucket halfway with clean water (never pour chemical directly into pump).",
                "2. If using powder (WP/SP/WDG), premix slurry in the bucket first.",
                "3. Add liquid formulation (EC/SC/SL) and stir thoroughly with a stick.",
                "4. Add spreader/sticker adjuvant (5ml) if spraying during monsoon.",
                "5. Pour into sprayer through the mesh strainer and top up with water."
            )
        }

        // 9. Field safety tips
        val safetyTips = when (language) {
            Language.GUJARATI -> listOf(
                "સવારના કે સાંજના ઠંડા સમયે જ છંટકાવ કરવો (બપોરના આકરા તડકામાં નહિ).",
                "પવનની દિશામાં જ આગળ વધી છંટકાવ કરવો (પવનની સામે ક્યારેય નહિ).",
                "છંટકાવ કરતી વખતે બીડી, તમાકુ કે ખોરાક ન લેવો. પૂર્ણ કપડાં અને માસ્ક પહેરવા.",
                "ખાલી બોટલો નહેર કે તળાવમાં ન ફેંકવી, ઊંડો ખાડો કરી જમીનમાં દાટી દેવી."
            )
            Language.HINDI -> listOf(
                "सुबह या शाम के ठंडे समय ही छिड़काव करें (दोपहर की धूप में नहीं)।",
                "हवा की दिशा में आगे बढ़ते हुए छिड़कें (हवा के विपरीत कभी नहीं)।",
                "छिड़काव के दौरान तंबाकू, बीड़ी या भोजन न करें। मास्क व दस्ताने पहनें।",
                "खाली बोतलों को पानी के स्रोतों से दूर जमीन में गहरा गाड़ दें।"
            )
            Language.ENGLISH -> listOf(
                "Spray during early morning or late afternoon (avoid hot midday sun).",
                "Always walk downwind with the spray direction (never against wind).",
                "Wear protective mask and gloves; avoid smoking or eating while spraying.",
                "Rinse empty containers and bury them safely away from water sources."
            )
        }

        val userAreaDisplay = when (language) {
            Language.GUJARATI -> "${String.format(Locale.US, "%.1f", safeArea)} ${areaUnit.nameGu}"
            Language.HINDI -> "${String.format(Locale.US, "%.1f", safeArea)} ${areaUnit.nameHi}"
            Language.ENGLISH -> "${String.format(Locale.US, "%.1f", safeArea)} ${areaUnit.nameEn}"
        }

        return DosageCalculationResult(
            areaInAcres = areaInAcres,
            userAreaDisplay = userAreaDisplay,
            pumpCapacityLiters = safeTankCapacity,
            totalWaterLiters = totalWaterLiters,
            totalPumpsCountExact = totalPumpsExact,
            totalPumpsRounded = totalPumpsRounded,
            chemicalDosePerPump = dosePerPump,
            chemicalDosePerLiter = dosePerLiter,
            totalChemicalRequired = totalChemical,
            chemicalUnit = unit,
            formulationType = formulationType,
            measuringCapGuidance = capGuidance,
            waterGuidance = waterGuidance,
            totalCost = calculatedTotalCost,
            costPerAcre = calculatedCostPerAcre,
            costPerPump = calculatedCostPerPump,
            toxicityBand = toxicityBand,
            phiDays = phiDays,
            mixingSequence = mixingSequence,
            safetyTips = safetyTips
        )
    }

    private fun formatMeasuringCapGuidance(
        dosePerPump: Double,
        formulationType: ChemicalFormulationType,
        language: Language
    ): String {
        if (formulationType == ChemicalFormulationType.LIQUID) {
            val standardCap = 25.0 // standard 25 ml pesticide cap
            val caps = dosePerPump / standardCap
            val roundedDose = String.format(Locale.US, "%.1f", dosePerPump)

            return if (caps in 0.8..1.2) {
                when (language) {
                    Language.GUJARATI -> "લગભગ ૧ આખો માપવાનો ઢાંકણ ($roundedDose ml)"
                    Language.HINDI -> "लगभग 1 पूरा मापने वाला ढक्कन ($roundedDose मिली)"
                    Language.ENGLISH -> "Approx. 1 full measuring cap ($roundedDose ml)"
                }
            } else if (caps in 1.8..2.2) {
                when (language) {
                    Language.GUJARATI -> "૨ આખા માપવાના ઢાંકણ ($roundedDose ml)"
                    Language.HINDI -> "2 पूरे मापने वाले ढक्कन ($roundedDose मिली)"
                    Language.ENGLISH -> "Approx. 2 full measuring caps ($roundedDose ml)"
                }
            } else if (dosePerPump < 10.0) {
                when (language) {
                    Language.GUJARATI -> "ઝીણી સિરીંજ/ડ્રોપરથી $roundedDose ml માપો"
                    Language.HINDI -> "सिरिंज या ड्रॉपर से $roundedDose मिली मापें"
                    Language.ENGLISH -> "Use measuring syringe ($roundedDose ml)"
                }
            } else {
                when (language) {
                    Language.GUJARATI -> "પંપ દીઠ $roundedDose ml (ઢાંકણ પરના કાપા મુજબ)"
                    Language.HINDI -> "पंप प्रति $roundedDose मिली (ढक्कन के निशान अनुसार)"
                    Language.ENGLISH -> "$roundedDose ml per pump using calibrated cap"
                }
            }
        } else {
            val roundedDose = String.format(Locale.US, "%.1f", dosePerPump)
            val approxSpoons = (dosePerPump / 10.0) // approx 1 tablespoon ~ 10g powder
            val spoonsRounded = String.format(Locale.US, "%.1f", approxSpoons)
            return when (language) {
                Language.GUJARATI -> "પંપ દીઠ $roundedDose ગ્રામ (આશરે $spoonsRounded ચમચી અથવા વજન કાંટો)"
                Language.HINDI -> "पंप प्रति $roundedDose ग्राम (लगभग $spoonsRounded चम्मच या तौलकर)"
                Language.ENGLISH -> "$roundedDose grams per pump (approx. $spoonsRounded tablespoons)"
            }
        }
    }
}
