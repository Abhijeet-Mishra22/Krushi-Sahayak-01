package com.example.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    GUJARATI("gu", "Gujarati", "ગુજરાતી"),
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English")
}

enum class HealthStatus(val emoji: String) {
    HEALTHY("🟢"),
    DISEASE("🔴"),
    PEST_INFESTATION("🐛"),
    NUTRIENT_DEFICIENCY("🟡"),
    ABIOTIC_STRESS("🌦️"),
    PHYSICAL_DAMAGE("🩹"),
    NOT_A_CROP("🚫"),
    UNKNOWN("❓");

    fun getDisplayName(language: Language): String = when (this) {
        HEALTHY -> when (language) {
            Language.GUJARATI -> "સ્વસ્થ પાક"
            Language.HINDI -> "स्वस्थ फसल"
            Language.ENGLISH -> "Healthy Crop"
        }
        DISEASE -> when (language) {
            Language.GUJARATI -> "રોગનો ઉપદ્રવ"
            Language.HINDI -> "रोग का प्रकोप"
            Language.ENGLISH -> "Disease Detected"
        }
        PEST_INFESTATION -> when (language) {
            Language.GUJARATI -> "જીવાતનો ઉપદ્રવ"
            Language.HINDI -> "कीट प्रकोप"
            Language.ENGLISH -> "Pest Infestation"
        }
        NUTRIENT_DEFICIENCY -> when (language) {
            Language.GUJARATI -> "પોષક તત્વોની ઉણપ"
            Language.HINDI -> "पोषक तत्वों की कमी"
            Language.ENGLISH -> "Nutrient Deficiency"
        }
        ABIOTIC_STRESS -> when (language) {
            Language.GUJARATI -> "હવામાન / પર્યાવરણીય તાણ"
            Language.HINDI -> "पर्यावरणीय / मौसमी तनाव"
            Language.ENGLISH -> "Abiotic / Weather Stress"
        }
        PHYSICAL_DAMAGE -> when (language) {
            Language.GUJARATI -> "શારીરિક / યાંત્રિક નુકસાન"
            Language.HINDI -> "भौतिक / यांत्रिक क्षति"
            Language.ENGLISH -> "Physical Damage"
        }
        NOT_A_CROP -> when (language) {
            Language.GUJARATI -> "પાક કે છોડ નથી"
            Language.HINDI -> "फसल या पौधा नहीं है"
            Language.ENGLISH -> "Not a Crop / Plant"
        }
        UNKNOWN -> when (language) {
            Language.GUJARATI -> "અચોક્કસ સ્થિતિ"
            Language.HINDI -> "अनिश्चित स्थिति"
            Language.ENGLISH -> "Uncertain / Insufficient"
        }
    }
}

enum class PlantPart {
    LEAF, FRUIT, STEM, WHOLE_PLANT, PEST_DAMAGE;

    fun getDisplayName(language: Language): String = when (this) {
        LEAF -> when (language) {
            Language.GUJARATI -> "પાન (Leaf)"
            Language.HINDI -> "पत्ता (Leaf)"
            Language.ENGLISH -> "Leaf"
        }
        FRUIT -> when (language) {
            Language.GUJARATI -> "ફળ / જીંડવું (Fruit/Pod)"
            Language.HINDI -> "फल / फली (Fruit/Pod)"
            Language.ENGLISH -> "Fruit / Pod"
        }
        STEM -> when (language) {
            Language.GUJARATI -> "થડ / ડાળી (Stem)"
            Language.HINDI -> "तना / शाखा (Stem)"
            Language.ENGLISH -> "Stem"
        }
        WHOLE_PLANT -> when (language) {
            Language.GUJARATI -> "આખો છોડ (Whole Plant)"
            Language.HINDI -> "पूरा पौधा (Whole Plant)"
            Language.ENGLISH -> "Whole Plant"
        }
        PEST_DAMAGE -> when (language) {
            Language.GUJARATI -> "જીવાતનું નુકસાન (Pest Damage)"
            Language.HINDI -> "कीट क्षति (Pest Damage)"
            Language.ENGLISH -> "Pest Damage"
        }
    }
}

enum class ConfidenceLevel {
    HIGH, MEDIUM, LOW;

    fun getDisplayName(language: Language): String = when (this) {
        HIGH -> when (language) {
            Language.GUJARATI -> "ઉચ્ચ સચોટતા (High)"
            Language.HINDI -> "उच्च सटीकता (High)"
            Language.ENGLISH -> "High Confidence"
        }
        MEDIUM -> when (language) {
            Language.GUJARATI -> "મધ્યમ સચોટતા (Medium)"
            Language.HINDI -> "मध्यम सटीकता (Medium)"
            Language.ENGLISH -> "Medium Confidence"
        }
        LOW -> when (language) {
            Language.GUJARATI -> "ઓછી સચોટતા (Low)"
            Language.HINDI -> "कम सटीकता (Low)"
            Language.ENGLISH -> "Low Confidence"
        }
    }
}

data class Crop(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val scientificName: String,
    val emoji: String
) {
    fun getLocalizedName(language: Language): String = when (language) {
        Language.GUJARATI -> nameGu
        Language.HINDI -> nameHi
        Language.ENGLISH -> nameEn
    }

    companion object {
        val NON_CROP = Crop(
            id = "non_crop",
            nameEn = "Not a Crop / Plant",
            nameHi = "फसल या पौधा नहीं है",
            nameGu = "પાક કે છોડ નથી",
            scientificName = "Non-botanical subject",
            emoji = "⚠️"
        )

        val UNKNOWN_CROP = Crop(
            id = "unknown_crop",
            nameEn = "Unknown Crop",
            nameHi = "अज्ञात फसल",
            nameGu = "અજ્ઞાત પાક",
            scientificName = "Plantae (Species Pending)",
            emoji = "🌱"
        )
    }
}

data class DiseasePest(
    val id: String,
    val cropId: String,
    val healthStatus: HealthStatus,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val symptomsEn: String,
    val symptomsHi: String,
    val symptomsGu: String,
    val causesEn: String,
    val causesHi: String,
    val causesGu: String,
    val lookForEn: String,
    val lookForHi: String,
    val lookForGu: String,
    val preventionEn: String,
    val preventionHi: String,
    val preventionGu: String,
    val biologicalIpmEn: String,
    val biologicalIpmHi: String,
    val biologicalIpmGu: String
) {
    fun getLocalizedName(language: Language): String = when (language) {
        Language.GUJARATI -> nameGu
        Language.HINDI -> nameHi
        Language.ENGLISH -> nameEn
    }

    fun getLocalizedSymptoms(language: Language): String = when (language) {
        Language.GUJARATI -> symptomsGu
        Language.HINDI -> symptomsHi
        Language.ENGLISH -> symptomsEn
    }

    fun getLocalizedCauses(language: Language): String = when (language) {
        Language.GUJARATI -> causesGu
        Language.HINDI -> causesHi
        Language.ENGLISH -> causesEn
    }

    fun getLocalizedLookFor(language: Language): String = when (language) {
        Language.GUJARATI -> lookForGu
        Language.HINDI -> lookForHi
        Language.ENGLISH -> lookForEn
    }

    fun getLocalizedPrevention(language: Language): String = when (language) {
        Language.GUJARATI -> preventionGu
        Language.HINDI -> preventionHi
        Language.ENGLISH -> preventionEn
    }

    fun getLocalizedBiological(language: Language): String = when (language) {
        Language.GUJARATI -> biologicalIpmGu
        Language.HINDI -> biologicalIpmHi
        Language.ENGLISH -> biologicalIpmEn
    }
}

data class VerifiedPesticide(
    val id: String,
    val cropId: String,
    val diseasePestId: String,
    val targetNameEn: String,
    val activeIngredient: String,
    val formulation: String,
    val applicationRateEn: String,
    val applicationRateHi: String,
    val applicationRateGu: String,
    val preHarvestIntervalDays: String,
    val verifiedAuthority: String,
    val safetyWarningsEn: List<String>,
    val safetyWarningsHi: List<String>,
    val safetyWarningsGu: List<String>
) {
    fun getLocalizedApplicationRate(language: Language): String = when (language) {
        Language.GUJARATI -> applicationRateGu
        Language.HINDI -> applicationRateHi
        Language.ENGLISH -> applicationRateEn
    }

    fun getLocalizedSafetyWarnings(language: Language): List<String> = when (language) {
        Language.GUJARATI -> safetyWarningsGu
        Language.HINDI -> safetyWarningsHi
        Language.ENGLISH -> safetyWarningsEn
    }
}

data class ImageQualityResult(
    val isClear: Boolean,
    val blurScore: Double,
    val brightnessScore: Double,
    val hasPlantCharacteristics: Boolean,
    val errorMessageEn: String? = null,
    val errorMessageHi: String? = null,
    val errorMessageGu: String? = null
) {
    fun getLocalizedErrorMessage(language: Language): String? = when (language) {
        Language.GUJARATI -> errorMessageGu
        Language.HINDI -> errorMessageHi
        Language.ENGLISH -> errorMessageEn
    }
}

data class DiagnosisResult(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val imageUris: List<String>,
    val crop: Crop,
    val plantPart: PlantPart,
    val healthStatus: HealthStatus,
    val problemName: String,
    val confidenceLevel: ConfidenceLevel,
    val confidencePercent: Int,
    val whatIsHappening: String,
    val whyItHappens: String,
    val whatToLookFor: String,
    val howToPrevent: String,
    val nonChemicalControl: String,
    val verifiedPesticide: VerifiedPesticide?,
    val isOnlineAnalysis: Boolean,
    val audioExplanation: String,
    val notes: String? = null
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val diagnosisId: Long,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedImageUri: String? = null
)
