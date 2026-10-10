package com.example.model

object Translations {
    fun appTitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "કૃષિ સહાયક"
        Language.HINDI -> "कृषि सहायक"
        Language.ENGLISH -> "Krushi Sahayak"
    }

    fun appSubtitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "ઓફલાઇન પાક રોગ અને જીવાત નિદાન"
        Language.HINDI -> "ऑफ़लाइन फसल रोग एवं कीट निदान"
        Language.ENGLISH -> "Offline AI Crop Disease & Pest Advisory"
    }

    fun checkYourCrop(lang: Language) = when (lang) {
        Language.GUJARATI -> "તમારા પાકની તપાસ કરો"
        Language.HINDI -> "अपनी फसल की जांच करें"
        Language.ENGLISH -> "Check your crop"
    }

    fun checkYourCropDesc(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાંદડા કે રોગગ્રસ્ત ભાગનો સ્પષ્ટ ફોટો પાડો. ઈન્ટરનેટ વગર પણ કામ કરશે."
        Language.HINDI -> "पत्ते या प्रभावित भाग की स्पष्ट फोटो लें। बिना इंटरनेट भी काम करेगा।"
        Language.ENGLISH -> "Take a clear photo of the affected leaf or plant. Works offline."
    }

    fun takePhoto(lang: Language) = when (lang) {
        Language.GUJARATI -> "ફોટો પાડો"
        Language.HINDI -> "फोटो खींचें"
        Language.ENGLISH -> "Take Photo"
    }

    fun chooseGallery(lang: Language) = when (lang) {
        Language.GUJARATI -> "ગેલેરીમાંથી પસંદ કરો"
        Language.HINDI -> "गैलरी से चुनें"
        Language.ENGLISH -> "Choose from Gallery"
    }

    fun askByVoice(lang: Language) = when (lang) {
        Language.GUJARATI -> "બોલીને પૂછો"
        Language.HINDI -> "बोलकर पूछें"
        Language.ENGLISH -> "Ask by Voice"
    }

    fun askAboutCropPlaceholder(lang: Language) = when (lang) {
        Language.GUJARATI -> "તમારા પાક વિશે પૂછો..."
        Language.HINDI -> "अपनी फसल के बारे में पूछें..."
        Language.ENGLISH -> "Ask about your crop..."
    }

    fun newDiagnosis(lang: Language) = when (lang) {
        Language.GUJARATI -> "નવું નિદાન"
        Language.HINDI -> "नया निदान"
        Language.ENGLISH -> "New Diagnosis"
    }

    fun history(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાછલા નિદાન (ઇતિહાસ)"
        Language.HINDI -> "पिछले निदान (इतिहास)"
        Language.ENGLISH -> "History"
    }

    fun today(lang: Language) = when (lang) {
        Language.GUJARATI -> "આજે"
        Language.HINDI -> "आज"
        Language.ENGLISH -> "Today"
    }

    fun yesterday(lang: Language) = when (lang) {
        Language.GUJARATI -> "ગઈકાલે"
        Language.HINDI -> "कल"
        Language.ENGLISH -> "Yesterday"
    }

    fun older(lang: Language) = when (lang) {
        Language.GUJARATI -> "પહેલાના દિવસો"
        Language.HINDI -> "पहले के दिन"
        Language.ENGLISH -> "Older"
    }

    fun searchByCropOrDate(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાકનું નામ કે તારીખ શોધો..."
        Language.HINDI -> "फसल का नाम या तारीख खोजें..."
        Language.ENGLISH -> "Search by crop or date..."
    }

    fun all(lang: Language) = when (lang) {
        Language.GUJARATI -> "બધા"
        Language.HINDI -> "सभी"
        Language.ENGLISH -> "All"
    }

    fun pastWeek(lang: Language) = when (lang) {
        Language.GUJARATI -> "છેલ્લા ૭ દિવસ"
        Language.HINDI -> "पिछले ७ दिन"
        Language.ENGLISH -> "Past 7 Days"
    }

    fun viewAllHistory(lang: Language) = when (lang) {
        Language.GUJARATI -> "બધો ઇતિહાસ જુઓ ➔"
        Language.HINDI -> "पूरा इतिहास देखें ➔"
        Language.ENGLISH -> "View Full History ➔"
    }

    fun onlineStatus(lang: Language) = when (lang) {
        Language.GUJARATI -> "ઓનલાઇન AI સક્રિય"
        Language.HINDI -> "ऑनलाइन AI सक्रिय"
        Language.ENGLISH -> "Online AI"
    }

    fun offlineStatus(lang: Language) = when (lang) {
        Language.GUJARATI -> "ઓફલાઇન મોડ (સુરક્ષિત)"
        Language.HINDI -> "ऑफ़लाइन मोड (सुरक्षित)"
        Language.ENGLISH -> "Offline Mode"
    }

    fun cropLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "તમારો પાક"
        Language.HINDI -> "आपकी फसल"
        Language.ENGLISH -> "Crop"
    }

    fun problemLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "સમસ્યા / રોગ"
        Language.HINDI -> "समस्या / रोग"
        Language.ENGLISH -> "Problem / Issue"
    }

    fun confidenceLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "સચોટતા"
        Language.HINDI -> "सटीकता"
        Language.ENGLISH -> "Confidence"
    }

    fun whatIsHappening(lang: Language) = when (lang) {
        Language.GUJARATI -> "શું થઈ રહ્યું છે?"
        Language.HINDI -> "क्या हो रहा है?"
        Language.ENGLISH -> "What is happening?"
    }

    fun whyItHappens(lang: Language) = when (lang) {
        Language.GUJARATI -> "આ શા માટે થાય છે?"
        Language.HINDI -> "यह क्यों होता है?"
        Language.ENGLISH -> "Why does it happen?"
    }

    fun whatToLookFor(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાકમાં શું લક્ષણો તપાસવા?"
        Language.HINDI -> "फसल में क्या लक्षण देखें?"
        Language.ENGLISH -> "What should I look for?"
    }

    fun howToPrevent(lang: Language) = when (lang) {
        Language.GUJARATI -> "અટકાવવાના ઉપાયો"
        Language.HINDI -> "रोकथाम के उपाय"
        Language.ENGLISH -> "How can I prevent it?"
    }

    fun nonChemicalControl(lang: Language) = when (lang) {
        Language.GUJARATI -> "દવા વગરના / દેશી ઉપાયો (IPM)"
        Language.HINDI -> "बिना दवा / प्राकृतिक उपाय (IPM)"
        Language.ENGLISH -> "Biological & Cultural Control"
    }

    fun chemicalControl(lang: Language) = when (lang) {
        Language.GUJARATI -> "ભલામણ કરેલ દવા (કેમિકલ કંટ્રોલ)"
        Language.HINDI -> "अनुशंसित दवा (रासायनिक नियंत्रण)"
        Language.ENGLISH -> "Verified Chemical Control"
    }

    fun safetyInstructions(lang: Language) = when (lang) {
        Language.GUJARATI -> "દવા છાંટતી વખતે સાવચેતી"
        Language.HINDI -> "दवा छिड़कते समय सावधानियां"
        Language.ENGLISH -> "Spraying Safety Instructions"
    }

    fun listenAloud(lang: Language) = when (lang) {
        Language.GUJARATI -> "અવાજ સાંભળો"
        Language.HINDI -> "बोलकर सुनें"
        Language.ENGLISH -> "Listen"
    }

    fun stopListening(lang: Language) = when (lang) {
        Language.GUJARATI -> "અવાજ બંધ કરો"
        Language.HINDI -> "आवाज रोकें"
        Language.ENGLISH -> "Stop"
    }

    fun askMore(lang: Language) = when (lang) {
        Language.GUJARATI -> "વધારે પ્રશ્નો પૂછો"
        Language.HINDI -> "और सवाल पूछें"
        Language.ENGLISH -> "Ask More"
    }

    fun selectCropManually(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાક જાતે પસંદ કરો"
        Language.HINDI -> "फसल स्वयं चुनें"
        Language.ENGLISH -> "Select Crop Manually"
    }

    fun analyzingCrop(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાકની તપાસ ચાલુ છે..."
        Language.HINDI -> "फसल की जांच हो रही है..."
        Language.ENGLISH -> "Analyzing your crop..."
    }

    fun imageNotClearTitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "ફોટો બરાબર નથી આવ્યો"
        Language.HINDI -> "फोटो साफ नहीं आई"
        Language.ENGLISH -> "Photo is not clear"
    }

    fun retakePhoto(lang: Language) = when (lang) {
        Language.GUJARATI -> "ફરીથી ફોટો પાડો"
        Language.HINDI -> "फिर से फोटो लें"
        Language.ENGLISH -> "Take Another Photo"
    }

    fun continueAnyway(lang: Language) = when (lang) {
        Language.GUJARATI -> "તો પણ ચકાસો"
        Language.HINDI -> "फिर भी जांचें"
        Language.ENGLISH -> "Continue Anyway"
    }

    fun activeIngredient(lang: Language) = when (lang) {
        Language.GUJARATI -> "મુખ્ય દવા (એક્ટિવ કન્ટેન્ટ):"
        Language.HINDI -> "सक्रिय तत्व (Active Ingredient):"
        Language.ENGLISH -> "Active Ingredient:"
    }

    fun applicationDosage(lang: Language) = when (lang) {
        Language.GUJARATI -> "છંટકાવનું પ્રમાણ:"
        Language.HINDI -> "छिड़काव की मात्रा:"
        Language.ENGLISH -> "Application & Dilution:"
    }

    fun waitingPeriod(lang: Language) = when (lang) {
        Language.GUJARATI -> "કાપણી પહેલા રાહ જોવાનો સમય (PHI):"
        Language.HINDI -> "कटाई से पहले प्रतीक्षा समय (PHI):"
        Language.ENGLISH -> "Pre-Harvest Interval (PHI):"
    }

    fun verifiedBy(lang: Language) = when (lang) {
        Language.GUJARATI -> "પ્રમાણિત સ્ત્રોત:"
        Language.HINDI -> "प्रमाणित स्रोत:"
        Language.ENGLISH -> "Verified by:"
    }

    fun noHistoryYet(lang: Language) = when (lang) {
        Language.GUJARATI -> "હજુ સુધી કોઈ નિદાન સંગ્રહાયેલ નથી."
        Language.HINDI -> "अभी तक कोई निदान सहेजा नहीं गया है।"
        Language.ENGLISH -> "No diagnosis history yet."
    }

    fun switchLanguage(lang: Language) = when (lang) {
        Language.GUJARATI -> "ભાષા બદલો"
        Language.HINDI -> "भाषा बदलें"
        Language.ENGLISH -> "Change Language"
    }

    // PESTICIDE DOSAGE CALCULATOR TRANSLATIONS
    fun pesticideCalculatorTitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "દવા છંટકાવ ગણતરી કેલ્ક્યુલેટર"
        Language.HINDI -> "दवा खुराक कैलकुलेटर"
        Language.ENGLISH -> "Pesticide Dosage Calculator"
    }

    fun pesticideCalculatorSubtitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "વીઘા/એકર અને પંપ મુજબ ચોક્કસ દવા, પાણી અને ખર્ચની ગણતરી"
        Language.HINDI -> "खेत के आकार व पंप अनुसार सटीक दवा, पानी एवं लागत की गणना"
        Language.ENGLISH -> "Accurate chemical, water & tank calculation per field size"
    }

    fun selectPresetOrCustom(lang: Language) = when (lang) {
        Language.GUJARATI -> "પ્રમાણિત દવા પસંદ કરો અથવા નવી ઉમેરો"
        Language.HINDI -> "प्रमाणित दवा चुनें या नई दर्ज करें"
        Language.ENGLISH -> "Choose Verified Chemical or Custom"
    }

    fun customChemical(lang: Language) = when (lang) {
        Language.GUJARATI -> "જાતે દવા દાખલ કરો"
        Language.HINDI -> "खुद दवा दर्ज करें"
        Language.ENGLISH -> "Custom Chemical"
    }

    fun fieldSizeSection(lang: Language) = when (lang) {
        Language.GUJARATI -> "૧. જમીન / ખેતરનું ક્ષેત્રફળ"
        Language.HINDI -> "1. खेत का क्षेत्रफल"
        Language.ENGLISH -> "1. Field Size & Area"
    }

    fun sprayerPumpSection(lang: Language) = when (lang) {
        Language.GUJARATI -> "૨. સ્પ્રેયર પંપ / ટાંકીની ક્ષમતા"
        Language.HINDI -> "2. स्प्रे पंप / टंकी की क्षमता"
        Language.ENGLISH -> "2. Sprayer Pump Capacity"
    }

    fun dosageSettingsSection(lang: Language) = when (lang) {
        Language.GUJARATI -> "૩. દવાનું પ્રમાણ (ડોઝ)"
        Language.HINDI -> "3. दवा की मात्रा (खुराक)"
        Language.ENGLISH -> "3. Dosage & Dilution Rate"
    }

    fun calculationSummary(lang: Language) = when (lang) {
        Language.GUJARATI -> "છંટકાવ ગણતરી પરિણામ"
        Language.HINDI -> "छिड़काव गणना परिणाम"
        Language.ENGLISH -> "Calculation Summary"
    }

    fun totalChemicalNeeded(lang: Language) = when (lang) {
        Language.GUJARATI -> "કુલ જરૂરી દવા:"
        Language.HINDI -> "कुल आवश्यक दवा:"
        Language.ENGLISH -> "Total Chemical Required:"
    }

    fun dosePerPumpLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "પંપ દીઠ દવાનું માપ:"
        Language.HINDI -> "प्रति पंप दवा की मात्रा:"
        Language.ENGLISH -> "Dose per Spray Pump:"
    }

    fun totalPumpsLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "કુલ છાંટવાના પંપ:"
        Language.HINDI -> "कुल छिड़कने वाले पंप:"
        Language.ENGLISH -> "Total Sprayer Pumps:"
    }

    fun totalWaterLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "કુલ પાણીની જરૂરિયાત:"
        Language.HINDI -> "कुल पानी की आवश्यकता:"
        Language.ENGLISH -> "Total Clean Water:"
    }

    fun safeMixingSequenceTitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "દવા મિશ્રણ કરવાની સાચી રીત"
        Language.HINDI -> "दवा घोलने का सही तरीका"
        Language.ENGLISH -> "Safe Tank-Mixing Procedure"
    }

    fun toxicityBandNotice(lang: Language) = when (lang) {
        Language.GUJARATI -> "CIBRC ઝેરીપણું વર્ગીકરણ લેબલ"
        Language.HINDI -> "CIBRC विषाक्तता वर्गीकरण लेबल"
        Language.ENGLISH -> "CIBRC Toxicity Classification"
    }

    fun calculateForMyField(lang: Language) = when (lang) {
        Language.GUJARATI -> "મારા ખેતર માટે દવા ગણો"
        Language.HINDI -> "मेरे खेत के लिए खुराक निकालें"
        Language.ENGLISH -> "Calculate Dosage for Field"
    }

    fun priceEstimatorOptional(lang: Language) = when (lang) {
        Language.GUJARATI -> "દવાનો ભાવ અને ખર્ચ અંદાજ (વૈકલ્પિક)"
        Language.HINDI -> "दवा की कीमत और कुल खर्च (वैकल्पिक)"
        Language.ENGLISH -> "Bottle Price & Cost Estimator (Optional)"
    }

    // PDF EXPORT TRANSLATIONS
    fun exportPdfReport(lang: Language) = when (lang) {
        Language.GUJARATI -> "પીડીએફ રિપોર્ટ ડાઉનલોડ / શેર કરો"
        Language.HINDI -> "पीडीएफ रिपोर्ट डाउनलोड / साझा करें"
        Language.ENGLISH -> "Export & Share PDF Report"
    }

    fun pdfExportSuccess(lang: Language) = when (lang) {
        Language.GUJARATI -> "પીડીએફ રિપોર્ટ તૈયાર થઈ ગયો છે!"
        Language.HINDI -> "पीडीएफ रिपोर्ट सफलतापूर्वक तैयार हो गई है!"
        Language.ENGLISH -> "PDF Report generated successfully!"
    }

    // MULTI-PHOTO & SYMPTOM TEXT WORKFLOW TRANSLATIONS
    fun scanReviewTitle(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાકની તપાસ અને વિગતો"
        Language.HINDI -> "फसल की जांच एवं विवरण"
        Language.ENGLISH -> "Crop Scan Review & Details"
    }

    fun takeAnotherPhoto(lang: Language) = when (lang) {
        Language.GUJARATI -> "બીજો ફોટો લો (પાંદડાની પાછળ / ડાળી)"
        Language.HINDI -> "दूसरी फोटो लें (पत्ते के नीचे / तना)"
        Language.ENGLISH -> "Take Another Photo (Leaf underside / Stem)"
    }

    fun addNotesPrompt(lang: Language) = when (lang) {
        Language.GUJARATI -> "વધુ ચોકસાઈ માટે લક્ષણો લખો અથવા બોલો (વૈકલ્પિક)"
        Language.HINDI -> "सटीक जांच के लिए लक्षण लिखें या बोलें (वैकल्पिक)"
        Language.ENGLISH -> "Add Symptoms or Notes for Precision (Optional)"
    }

    fun addNotesPlaceholder(lang: Language) = when (lang) {
        Language.GUJARATI -> "દા.ત. પાન 3 દિવસથી પીળા પડે છે, ચીકાશ દેખાય છે..."
        Language.HINDI -> "उदा. 3 दिन से पत्तियां पीली पड़ रही हैं, चिपचिपापन है..."
        Language.ENGLISH -> "e.g., Yellowing started 3 days ago, sticky coating on leaves..."
    }

    fun detectDiseaseAndSolution(lang: Language) = when (lang) {
        Language.GUJARATI -> "રોગ અને ઉપાય શોધો"
        Language.HINDI -> "रोग और उपाय खोजें"
        Language.ENGLISH -> "Detect Disease & Solutions"
    }

    fun checkYourCropGuide(lang: Language) = when (lang) {
        Language.GUJARATI -> "ફોટો પાડીને, ગેલેરીમાંથી પસંદ કરીને, બોલીને અથવા આ બધા ભેગા વાપરીને તમારા પાકની તપાસ કરો"
        Language.HINDI -> "फोटो खींचकर, गैलरी से चुनकर, बोलकर या इन सभी का साथ में उपयोग करके अपनी फसल की जांच करें"
        Language.ENGLISH -> "Check your crop by taking photo, choosing from gallery, by voice, or by using any combination or all of them"
    }

    fun retakeAllPhotos(lang: Language) = when (lang) {
        Language.GUJARATI -> "બધા ફોટા ફરીથી પાડો"
        Language.HINDI -> "सभी फोटो दोबारा लें"
        Language.ENGLISH -> "Retake Photos"
    }

    fun unknownCropLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "અજ્ઞાત પાક"
        Language.HINDI -> "अज्ञात फसल"
        Language.ENGLISH -> "Unknown Crop"
    }

    fun notACropLabel(lang: Language) = when (lang) {
        Language.GUJARATI -> "પાક કે છોડ નથી"
        Language.HINDI -> "फसल या पौधा नहीं है"
        Language.ENGLISH -> "Not a Crop / Plant"
    }
}

