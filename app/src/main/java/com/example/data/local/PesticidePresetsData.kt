package com.example.data.local

import com.example.model.ChemicalFormulationType
import com.example.model.PesticidePreset
import com.example.model.ToxicityBand

object PesticidePresetsData {

    val presets = listOf(
        // 1. COTTON - IMIDACLOPRID
        PesticidePreset(
            id = "preset_cotton_imidacloprid",
            cropId = "cotton",
            cropNameEn = "Cotton",
            cropNameHi = "कपास",
            cropNameGu = "કપાસ",
            cropEmoji = "🌱",
            tradeOrCommonName = "Confidor / Imidacloprid 17.8% SL",
            activeIngredient = "Imidacloprid 17.8% SL",
            targetPestOrDisease = "Aphids, Jassids, Whiteflies (રસ ચૂસતી જીવાત)",
            formulationType = ChemicalFormulationType.LIQUID,
            dosePer15LPump = 4.0, // ml
            dosePerLiter = 0.25, // ml/L
            dosePerAcre = 50.0, // ml/acre
            standardWaterPerAcre = 180.0,
            toxicityBand = ToxicityBand.GREEN,
            phiDays = "40 days",
            typicalBottleSizes = listOf(50.0, 100.0, 250.0, 500.0, 1000.0),
            typicalPricePerPack = 320.0,
            specialAdvisoryGu = "ફૂલ બેસવાના સમયે મધમાખીઓ હોવાથી સાંજના સમયે જ છંટકાવ કરવો.",
            specialAdvisoryHi = "फूल आने पर शाम के समय छिड़कें ताकि मधुमक्खियों को नुकसान न हो।",
            specialAdvisoryEn = "Spray late afternoon during flowering to safeguard pollinating honeybees."
        ),

        // 2. COTTON - DIAFENTHIURON
        PesticidePreset(
            id = "preset_cotton_diafenthiuron",
            cropId = "cotton",
            cropNameEn = "Cotton",
            cropNameHi = "कपास",
            cropNameGu = "કપાસ",
            cropEmoji = "🌱",
            tradeOrCommonName = "Pegasus / Diafenthiuron 50% WP",
            activeIngredient = "Diafenthiuron 50% WP",
            targetPestOrDisease = "Whitefly Vector & Mites (સફેદ માખી અને કથીરી)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 20.0, // gm
            dosePerLiter = 1.25,
            dosePerAcre = 240.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.BLUE,
            phiDays = "30 days",
            typicalBottleSizes = listOf(100.0, 250.0, 500.0),
            typicalPricePerPack = 650.0,
            specialAdvisoryGu = "પાઉડરને પહેલા નાની ડોલમાં ઓગાળીને જ પંપમાં રેડવો.",
            specialAdvisoryHi = "पाउडर को पहले बाल्टी में घोलें फिर पंप में डालें।",
            specialAdvisoryEn = "Premix powder slurry in bucket with clean water before pouring into pump."
        ),

        // 3. TOMATO - MANCOZEB
        PesticidePreset(
            id = "preset_tomato_mancozeb",
            cropId = "tomato",
            cropNameEn = "Tomato",
            cropNameHi = "टमाटर",
            cropNameGu = "ટામેટા",
            cropEmoji = "🍅",
            tradeOrCommonName = "Dithane M-45 / Mancozeb 75% WP",
            activeIngredient = "Mancozeb 75% WP",
            targetPestOrDisease = "Early Blight & Leaf Spot (આગોતરો સુકારો)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 35.0, // gm
            dosePerLiter = 2.0,
            dosePerAcre = 450.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.GREEN,
            phiDays = "7 days",
            typicalBottleSizes = listOf(250.0, 500.0, 1000.0),
            typicalPricePerPack = 280.0,
            specialAdvisoryGu = "ટામેટા ઉતારવાના ૭ દિવસ પહેલા છેલ્લો છંટકાવ કરવો.",
            specialAdvisoryHi = "टमाटर तोड़ने से 7 दिन पहले छिड़काव बंद करें।",
            specialAdvisoryEn = "Strictly observe 7-day pre-harvest interval before picking tomatoes."
        ),

        // 4. TOMATO - CHLORANTRANILIPROLE
        PesticidePreset(
            id = "preset_tomato_coragen",
            cropId = "tomato",
            cropNameEn = "Tomato",
            cropNameHi = "टमाटर",
            cropNameGu = "ટામેટા",
            cropEmoji = "🍅",
            tradeOrCommonName = "Coragen / Chlorantraniliprole 18.5% SC",
            activeIngredient = "Chlorantraniliprole 18.5% SC",
            targetPestOrDisease = "Fruit Borer / Helicoverpa (ફળ કોરી ખાનાર ઈયળ)",
            formulationType = ChemicalFormulationType.LIQUID,
            dosePer15LPump = 6.0, // ml
            dosePerLiter = 0.35,
            dosePerAcre = 60.0,
            standardWaterPerAcre = 180.0,
            toxicityBand = ToxicityBand.GREEN,
            phiDays = "3 days",
            typicalBottleSizes = listOf(30.0, 60.0, 150.0),
            typicalPricePerPack = 490.0,
            specialAdvisoryGu = "માત્ર ૬ મિલી પ્રતિ પંપ વાપરવું, વધુ માત્રા ન લેવી.",
            specialAdvisoryHi = "मात्रा 6 मिली प्रति पंप से अधिक न बढ़ाएं।",
            specialAdvisoryEn = "Low-dose systemic active: do not exceed 6 ml per pump."
        ),

        // 5. WHEAT - PROPICONAZOLE
        PesticidePreset(
            id = "preset_wheat_tilt",
            cropId = "wheat",
            cropNameEn = "Wheat",
            cropNameHi = "गेहूं",
            cropNameGu = "ઘઉં",
            cropEmoji = "🌾",
            tradeOrCommonName = "Tilt / Propiconazole 25% EC",
            activeIngredient = "Propiconazole 25% EC",
            targetPestOrDisease = "Yellow & Brown Rust (પીળો ગેરુ / રતવો)",
            formulationType = ChemicalFormulationType.LIQUID,
            dosePer15LPump = 15.0, // ml
            dosePerLiter = 1.0,
            dosePerAcre = 200.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.YELLOW,
            phiDays = "30 days",
            typicalBottleSizes = listOf(100.0, 250.0, 500.0, 1000.0),
            typicalPricePerPack = 460.0,
            specialAdvisoryGu = "ખેતરમાં પીળો ડાઘ દેખાતા જ તાત્કાલિક છંટકાવ કરવો.",
            specialAdvisoryHi = "पीले धब्बे दिखते ही तुरंत छिड़काव करें।",
            specialAdvisoryEn = "Apply immediately when first stripe rust pustules appear."
        ),

        // 6. GROUNDNUT - SAFF / CARBENDAZIM + MANCOZEB
        PesticidePreset(
            id = "preset_groundnut_saaf",
            cropId = "groundnut",
            cropNameEn = "Groundnut",
            cropNameHi = "मूंगफली",
            cropNameGu = "મગફળી",
            cropEmoji = "🥜",
            tradeOrCommonName = "Saaf / Carbendazim 12% + Mancozeb 63% WP",
            activeIngredient = "Carbendazim 12% + Mancozeb 63% WP",
            targetPestOrDisease = "Tikka Leaf Spot & Rust (ટિક્કા રોગ અને ગેરુ)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 30.0, // gm
            dosePerLiter = 1.75,
            dosePerAcre = 350.0,
            standardWaterPerAcre = 180.0,
            toxicityBand = ToxicityBand.BLUE,
            phiDays = "30 days",
            typicalBottleSizes = listOf(250.0, 500.0, 1000.0),
            typicalPricePerPack = 340.0,
            specialAdvisoryGu = "સવારનું ઝાકળ ઉડી ગયા પછી જ છંટકાવ કરવો.",
            specialAdvisoryHi = "सुबह की ओस सूखने के बाद ही छिड़काव करें।",
            specialAdvisoryEn = "Spray after morning dew has evaporated completely."
        ),

        // 7. RICE - TRICYCLAZOLE
        PesticidePreset(
            id = "preset_rice_baan",
            cropId = "rice",
            cropNameEn = "Rice / Paddy",
            cropNameHi = "धान",
            cropNameGu = "ડાંગર",
            cropEmoji = "🌾",
            tradeOrCommonName = "Baan / Tricyclazole 75% WP",
            activeIngredient = "Tricyclazole 75% WP",
            targetPestOrDisease = "Blast Disease (કરમોડી / બ્લાસ્ટ)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 10.0, // gm
            dosePerLiter = 0.6,
            dosePerAcre = 120.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.BLUE,
            phiDays = "30 days",
            typicalBottleSizes = listOf(120.0, 250.0, 500.0),
            typicalPricePerPack = 390.0,
            specialAdvisoryGu = "કંકી બહાર આવે તે પહેલાં છંટકાવ કરવો સૌથી વધુ અસરકારક છે.",
            specialAdvisoryHi = "बाली निकलने से ठीक पहले छिड़काव करें।",
            specialAdvisoryEn = "Best applied just before panicle emergence."
        ),

        // 8. CHILLI - SPINOSAD
        PesticidePreset(
            id = "preset_chilli_tracer",
            cropId = "chilli",
            cropNameEn = "Chilli",
            cropNameHi = "मिर्च",
            cropNameGu = "મરચાં",
            cropEmoji = "🌶️",
            tradeOrCommonName = "Tracer / Spinosad 45% SC",
            activeIngredient = "Spinosad 45% SC",
            targetPestOrDisease = "Thrips & Fruit Borer (થ્રીપ્સ અને ફળની ઈયળ)",
            formulationType = ChemicalFormulationType.LIQUID,
            dosePer15LPump = 4.0, // ml
            dosePerLiter = 0.25,
            dosePerAcre = 60.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.GREEN,
            phiDays = "3 days",
            typicalBottleSizes = listOf(25.0, 75.0, 100.0),
            typicalPricePerPack = 520.0,
            specialAdvisoryGu = "લીલા મરચાં ઉતારવાના ૩ દિવસ પહેલાં છંટકાવ બંધ કરવો.",
            specialAdvisoryHi = "मिर्च तोड़ने से 3 दिन पहले सुरक्षित।",
            specialAdvisoryEn = "Safe 3-day harvest interval for continuous chilli harvests."
        ),

        // 9. ORGANIC - NEEM OIL 10,000 PPM
        PesticidePreset(
            id = "preset_organic_neem",
            cropId = "all",
            cropNameEn = "All Crops (Organic)",
            cropNameHi = "सभी फसलें (जैविक)",
            cropNameGu = "બધા પાક (જૈવિક)",
            cropEmoji = "🌿",
            tradeOrCommonName = "Neem Oil 10,000 PPM (Azadirachtin)",
            activeIngredient = "Azadirachtin 1% (10,000 ppm)",
            targetPestOrDisease = "Sucking Pests, Leafminer & Early Caterpillars",
            formulationType = ChemicalFormulationType.LIQUID,
            dosePer15LPump = 30.0, // ml
            dosePerLiter = 2.0,
            dosePerAcre = 350.0,
            standardWaterPerAcre = 180.0,
            toxicityBand = ToxicityBand.GREEN,
            phiDays = "0 days (No chemical residue)",
            typicalBottleSizes = listOf(100.0, 250.0, 500.0, 1000.0),
            typicalPricePerPack = 350.0,
            specialAdvisoryGu = "સંપૂર્ણ પ્રાકૃતિક/ઓર્ગેનિક દવા. પાણી સાથે હલાવતી વખતે ૨ મિલી સાબુનું ફીણ ઉમેરવું.",
            specialAdvisoryHi = "पूरी तरह से जैविक। पानी में अच्छी तरह मिलाने के लिए थोड़ा साबुन का घोल मिलाएं।",
            specialAdvisoryEn = "100% Organic certified botanical. Add mild soap solution as emulsifier."
        ),

        // 10. SOYBEAN / VEGETABLES - EMAMECTIN BENZOATE
        PesticidePreset(
            id = "preset_emamectin_proclaim",
            cropId = "soybean",
            cropNameEn = "Soybean & Veg",
            cropNameHi = "सोयाबीन एवं सब्जियां",
            cropNameGu = "સોયાબીન અને શાકભાજી",
            cropEmoji = "🌱",
            tradeOrCommonName = "Proclaim / Emamectin Benzoate 5% SG",
            activeIngredient = "Emamectin Benzoate 5% SG",
            targetPestOrDisease = "Spodoptera & Heliothis Caterpillars (લશ્કરી ઈયળ)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 8.0, // gm
            dosePerLiter = 0.5,
            dosePerAcre = 80.0,
            standardWaterPerAcre = 180.0,
            toxicityBand = ToxicityBand.BLUE,
            phiDays = "5 days",
            typicalBottleSizes = listOf(50.0, 100.0, 250.0),
            typicalPricePerPack = 310.0,
            specialAdvisoryGu = "ઈયળો નાની હોય ત્યારે છાંટવાથી ઉત્તમ પરિણામ મળે છે.",
            specialAdvisoryHi = "कीट की छोटी अवस्था में ही छिड़काव सबसे प्रभावी है।",
            specialAdvisoryEn = "Most effective when larvae are in early instars."
        ),

        // 11. ONION / GARLIC - COPPER OXYCHLORIDE
        PesticidePreset(
            id = "preset_onion_copper",
            cropId = "onion",
            cropNameEn = "Onion & Garlic",
            cropNameHi = "प्याज एवं लहसुन",
            cropNameGu = "ડુંગળી અને લસણ",
            cropEmoji = "🧅",
            tradeOrCommonName = "Blitox / Copper Oxychloride 50% WP",
            activeIngredient = "Copper Oxychloride 50% WP",
            targetPestOrDisease = "Purple Blotch & Downy Mildew (જાંબલી ધાબા રોગ)",
            formulationType = ChemicalFormulationType.POWDER,
            dosePer15LPump = 40.0, // gm
            dosePerLiter = 2.5,
            dosePerAcre = 500.0,
            standardWaterPerAcre = 200.0,
            toxicityBand = ToxicityBand.BLUE,
            phiDays = "10 days",
            typicalBottleSizes = listOf(250.0, 500.0, 1000.0),
            typicalPricePerPack = 290.0,
            specialAdvisoryGu = "ડુંગળીના ચીકણા પાન પર દવા ચોંટી રહે તે માટે ૫ મિલી ગુંદર/સ્ટીકર ખાસ ઉમેરવું.",
            specialAdvisoryHi = "प्याज की चिकनी पत्तियों पर दवा टिकने के लिए स्टीकर अवश्य मिलाएं।",
            specialAdvisoryEn = "Always mix surfactant/spreader sticker on waxy allium foliage."
        )
    )

    fun findPresetById(id: String): PesticidePreset? {
        return presets.find { it.id == id }
    }

    fun findPresetByCrop(cropId: String): List<PesticidePreset> {
        return presets.filter { it.cropId.equals(cropId, ignoreCase = true) || it.cropId == "all" }
    }
}
