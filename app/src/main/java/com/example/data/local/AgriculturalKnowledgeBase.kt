package com.example.data.local

import com.example.model.*

object AgriculturalKnowledgeBase {

    val crops = listOf(
        Crop("cotton", "Cotton", "कपास", "કપાસ", "Gossypium hirsutum", "🌱"),
        Crop("tomato", "Tomato", "टमाटर", "ટામેટા", "Solanum lycopersicum", "🍅"),
        Crop("wheat", "Wheat", "गेहूं", "ઘઉં", "Triticum aestivum", "🌾"),
        Crop("groundnut", "Groundnut", "मूंगफली", "મગફળી", "Arachis hypogaea", "🥜"),
        Crop("rice", "Rice / Paddy", "धान / चावल", "ડાંગર", "Oryza sativa", "🌾"),
        Crop("chilli", "Chilli", "मिर्च", "મરચાં", "Capsicum annuum", "🌶️"),
        Crop("potato", "Potato", "आलू", "બટાકા", "Solanum tuberosum", "🥔"),
        Crop("maize", "Maize / Corn", "मक्का", "મકાઈ", "Zea mays", "🌽"),
        Crop("soybean", "Soybean", "सोयाबीन", "સોયાબીન", "Glycine max", "🌿"),
        Crop("onion", "Onion", "प्याज", "ડુંગળી", "Allium cepa", "🧅")
    )

    val diseasesAndPests = listOf(
        // COTTON
        DiseasePest(
            id = "cotton_curl",
            cropId = "cotton",
            healthStatus = HealthStatus.DISEASE,
            nameEn = "Cotton Leaf Curl Disease (CLCuD)",
            nameHi = "कपास पर्ण कुंचन रोग (लीफ कर्ल)",
            nameGu = "કપાસના પાન વળવાનો રોગ (લીફ કર્લ)",
            symptomsEn = "Leaves curl upward or downward with thickening of veins. Enations (leaf-like outgrowths) on underside of leaves and stunted growth.",
            symptomsHi = "पत्ते ऊपर या नीचे की ओर मुड़ जाते हैं, नसें मोटी हो जाती हैं और पत्तों के नीचे छोटी पत्ती जैसी गांठें बन जाती हैं।",
            symptomsGu = "પાન કિનારીથી ઉપર કે નીચે વળી જાય છે, પાનની નસો જાડી અને લીલી દેખાય છે તથા પાનની નીચે નાની ગાંઠ જેવો ઉપસેલો ભાગ બને છે.",
            causesEn = "Gemini virus transmitted by Cotton Whitefly (Bemisia tabaci). Higher risk during warm, humid weather.",
            causesHi = "सफेद मक्खी द्वारा फैलने वाला विषाणु (वायरस)। गर्म और आर्द्र मौसम में प्रकोप तेजी से बढ़ता है।",
            causesGu = "સફેદ માખી દ્વારા ફેલાતો વાયરસ રોગ. ગરમ અને ભેજવાળા વાતાવરણમાં રોગ ઝડપથી ફેલાય છે.",
            lookForEn = "Upward cup-shaped leaf curling, leathery thickened leaves, small cup growths under leaves.",
            lookForHi = "कप के आकार में मुड़े हुए पत्ते, पत्तों की नसों का असामान्य उभार और पौधे की धीमी वृद्धि।",
            lookForGu = "પાન રકાબી જેવા વળી જવા, નસો જાડી થવી અને છોડનો વિકાસ અટકી જવો.",
            preventionEn = "Grow CLCuD-resistant varieties. Remove and destroy weeds like abutilon. Maintain field sanitation.",
            preventionHi = "रोगरोधी किस्मों की बुवाई करें। खेत और मेड़ों से खरपतवार नष्ट करें। प्रभावित पौधों को तुरंत उखाड़ दें।",
            preventionGu = "રોગપ્રતિકારક જાતો વાવવી. શેઢા-પાળા પરથી ખપટી અને ગાજરઘાસ જેવા નીંદણ દૂર કરવા.",
            biologicalIpmEn = "Install yellow sticky traps (10-15 per acre) for whiteflies. Spray 5% Neem seed kernel extract (NSKE) or Neem oil 10,000 ppm at 30 ml/15 L pump.",
            biologicalIpmHi = "पीले चिपचिपे कार्ड (10-15 प्रति एकड़) लगाएं। नीम तेल (10,000 ppm) 30 मिली प्रति 15 लीटर पंप छिड़कें।",
            biologicalIpmGu = "એકર દીઠ 10-15 પીળા ચીકણા ટ્રેપ (યલો સ્ટીકી ટ્રેપ) ગોઠવો. લીમડાનું તેલ (10,000 ppm) 30 મિલી પ્રતિ 15 લિટર પંપમાં છાંટવું."
        ),
        DiseasePest(
            id = "cotton_aphid",
            cropId = "cotton",
            healthStatus = HealthStatus.PEST_INFESTATION,
            nameEn = "Cotton Aphid / Sucking Pest Infestation",
            nameHi = "कपास में माहू / चेपा (एफिड) का प्रकोप",
            nameGu = "કપાસમાં મોલો-મશી (એફિડ્સ) નો ઉપદ્રવ",
            symptomsEn = "Leaves turn downward, crinkled and glossy from honeydew sticky excretion. Sooty mold develops on leaves.",
            symptomsHi = "पत्तियों की निचली सतह पर छोटे काले/पीले कीड़े, पत्तियां मुड़कर काली पड़ जाती हैं और चिपचिपा स्राव दिखता है।",
            symptomsGu = "પાનની નીચે નાની પીળી કે કાળી જીવાત ચોંટેલી દેખાય છે. પાન નીચે તરફ વળે છે અને ચીકણો ગુંદર જેવો રસ ઝરે છે જેનાથી કાળી ફૂગ થાય છે.",
            causesEn = "Small sap-sucking nymphs and adults that multiply rapidly in dry spells following rain.",
            causesHi = "रस चूसने वाले कीट जो कम वर्षा और सूखे अंतराल में तेजी से बढ़ते हैं।",
            causesGu = "રસ ચૂસતી જીવાત જે વરસાદ બંધ રહ્યા પછી સૂકા વાતાવરણમાં ખૂબ ઝડપથી વધે છે.",
            lookForEn = "Underside of tender leaves clustered with yellow/black aphids, sticky sheen, presence of ants.",
            lookForHi = "कोमल पत्तों के नीचे कीड़ों का झुंड, चिपचिपापन और काली फफूंद।",
            lookForGu = "કૂણા પાન નીચે ઝીણી જીવાતનો જથ્થો, પાન પર ચીકાશ અને કાળી મેશ.",
            preventionEn = "Avoid excessive nitrogen (urea) fertilizers. Conserve natural predators like ladybird beetles.",
            preventionHi = "यूरिया का अत्यधिक उपयोग न करें। मित्र कीट जैसे लेडीबर्ड बीटल का संरक्षण करें।",
            preventionGu = "વધારાનું યુરિયા ખાતર ન આપવું. દાળિયા (લેડીબર્ડ બીટલ) જેવા મિત્ર કીટકોનું રક્ષણ કરવું.",
            biologicalIpmEn = "Spray 5% NSKE or Verticillium lecanii entomopathogenic fungus (50g / 15L water). Use yellow sticky traps.",
            biologicalIpmHi = "नीम का काढ़ा (5% NSKE) या वर्टिसिलियम लेकानी (50 ग्राम/15 ली.) का छिड़काव करें।",
            biologicalIpmGu = "લીંબોળીનું મીંજ અર્ક 5% અથવા વર્ટીસીલીયમ લેકાની ફૂગ 50 ગ્રામ/15 લિટર પંપમાં છાંટવું."
        ),
        DiseasePest(
            id = "cotton_healthy",
            cropId = "cotton",
            healthStatus = HealthStatus.HEALTHY,
            nameEn = "Healthy Cotton Plant",
            nameHi = "स्वस्थ कपास का पौधा",
            nameGu = "સ્વસ્થ કપાસનો છોડ",
            symptomsEn = "Vibrant deep green foliage, active terminal growth, well-formed squares and bolls with no leaf distortion.",
            symptomsHi = "गहरे हरे रंग की पत्तियां, मजबूत तना, स्वस्थ फूल और कलियां। कोई रोग लक्षण नहीं।",
            symptomsGu = "ઘાટા લીલા પાન, તંદુરસ્ત ડાળીઓ અને સુંદર જીંડવા. કોઈ રોગ કે જીવાતના લક્ષણ નથી.",
            causesEn = "Balanced soil fertility, adequate moisture and good crop management.",
            causesHi = "उचित पोषण, संतुलित सिंचाई और अच्छी देखरेख।",
            causesGu = "સંતુલિત ખાતર, પૂરતી ભીનાશ અને સારી દેખભાળ.",
            lookForEn = "Clean leaf surfaces, intact margins, active flower and boll development.",
            lookForHi = "साफ पत्ते, सामान्य वृद्धि और कीट-मुक्त पौधे।",
            lookForGu = "સ્વચ્છ પાન, સરખી વૃદ્ધિ અને રોગમુક્ત છોડ.",
            preventionEn = "Maintain regular scouting every 5-7 days. Avoid over-irrigation. Apply balanced N-P-K nutrients.",
            preventionHi = "हर 5-7 दिन में खेत का निरीक्षण करते रहें। जलभराव न होने दें।",
            preventionGu = "દર 5-7 દિવસે પાકની જાતતપાસ કરતા રહો. પાણી ભરાવા ન દેવું.",
            biologicalIpmEn = "Install pheromone traps to monitor bollworm moth activity early.",
            biologicalIpmHi = "कीट निगरानी के लिए फेरोमोन ट्रैप लगाएं।",
            biologicalIpmGu = "ગુલાબી ઇયળની આગોતરી જાણકારી માટે ફેરોમોન ટ્રેપ લગાવો."
        ),

        // TOMATO
        DiseasePest(
            id = "tomato_early_blight",
            cropId = "tomato",
            healthStatus = HealthStatus.DISEASE,
            nameEn = "Tomato Early Blight",
            nameHi = "टमाटर का अगेती झुलसा रोग (अर्ली ब्लाइट)",
            nameGu = "ટામેટાનો આગોતરો સુકારો (અર્લી બ્લાઈટ)",
            symptomsEn = "Concentric brown-black circular rings (target board pattern) on older lower leaves. Yellow halo surrounds spots.",
            symptomsHi = "निचली पत्तियों पर गोल भूरे-काले छल्लेदार धब्बे (लक्ष्य पट्टिका जैसे), धब्बों के चारों ओर पीलापन और पत्तियां सूखना।",
            symptomsGu = "નીચલા જૂના પાન પર ગોળ ભૂખરા-કાળા કુંડાળા જેવા ડાઘ (ટાર્ગેટ બોર્ડ આકાર). ડાઘની આસપાસ પાન પીળું પડી સુકાઈ જાય છે.",
            causesEn = "Fungus Alternaria solani. Thrives in warm temperatures (24-29°C) with frequent showers or heavy morning dew.",
            causesHi = "अल्टरनेरिया सोलानी फफूंद। गर्म और नम मौसम में यह तेजी से फैलता है।",
            causesGu = "અલ્ટરનેરિયા સોલાની નામની ફૂગ. ગરમ અને ભેજવાળા વાતાવરણ તથા સવારના ઝાકળમાં રોગ વધે છે.",
            lookForEn = "Lower leaves showing brown target rings, progressing upward as plant matures.",
            lookForHi = "निचले पत्तों पर छल्लेनुमा धब्बे जो धीरे-धीरे ऊपर की पत्तियों पर फैलते हैं।",
            lookForGu = "નીચલા પાન પર કુંડાળા જેવા કાળા ડાઘ જે ધીમે-ધીમે ઉપરના પાન તરફ ફેલાય છે.",
            preventionEn = "Prune lower infected leaves touching soil. Avoid overhead sprinkler irrigation. Use mulch around plants.",
            preventionHi = "जमीन को छूने वाले रोगग्रस्त निचले पत्तों को काटकर नष्ट करें। पौधों के आधार पर प्लास्टिक मल्चिंग लगाएं।",
            preventionGu = "જમીનને અડતા નીચલા રોગિષ્ઠ પાન તોડી નાશ કરવો. ડ્રિપ પદ્ધતિ વાપરવી. છોડ પર સીધું પાણી ન છાંટવું.",
            biologicalIpmEn = "Foliar spray of Trichoderma harzianum (5g/L water) or Pseudomonas fluorescens (10g/L) during early vegetative stage.",
            biologicalIpmHi = "ट्राइकोडर्मा हरजिएनम (5 ग्राम/लीटर) या स्यूडोमोनास फ्लोरोसेंस का पत्तियों पर छिड़काव करें।",
            biologicalIpmGu = "ટ્રાઈકોડર્મા હાર્ઝિયાનમ (5 ગ્રામ/લિટર) અથવા સ્યુડોમોનાસ ફ્લોરોસન્સ પંપમાં ઓગાળી છાંટવું."
        ),
        DiseasePest(
            id = "tomato_fruit_borer",
            cropId = "tomato",
            healthStatus = HealthStatus.PEST_INFESTATION,
            nameEn = "Tomato Fruit Borer (Helicoverpa armigera)",
            nameHi = "टमाटर का फल छेदक कीट (हेलिकोवर्पा)",
            nameGu = "ટામેટાની ફળ કોરી ખાનાર ઈયળ (ફળ વિંધક)",
            symptomsEn = "Circular bore holes on fruits with larval entry points, decaying internal flesh, premature fruit drop.",
            symptomsHi = "टमाटर के फलों पर गोल छेद, फलों के अंदर कीड़ा घुसकर गूदा खाता है और फल सड़कर गिर जाते हैं।",
            symptomsGu = "ટામેટા પર ગોળાકાર કાણાં, ઈયળ ફળમાં અંદર ઘૂસી ગર્ભ ખાય છે અને ટામેટા સડીને ખરી પડે છે.",
            causesEn = "Caterpillar of Helicoverpa moth active during flowering and fruiting period.",
            causesHi = "हेलिकोवर्पा पतंगे की सुंडी जो फूल व फल बनने के समय अंडे देती है।",
            causesGu = "હેલીકોવર્પા ફુદાની ઈયળ જે ફૂલ અને નાના ફળ સમયે નુકસાન કરે છે.",
            lookForEn = "Damaged fruit with frass (caterpillar droppings) around bore hole.",
            lookForHi = "फलों पर छेद और छेद के बाहर कीड़े का मल।",
            lookForGu = "ફળ પર કાણું અને કાણા પાસે ઈયળની હગાર.",
            preventionEn = "Plant African marigold (galgota) as trap crop every 16 rows of tomato. Handpick visible caterpillars in small plots.",
            preventionHi = "प्रत्येक 16 कतारों के बाद गेंदे (मैरीगोल्ड) की एक कतार लगाएं (ट्रैप क्रॉप)। प्रभावित फलों को चुनकर नष्ट करें।",
            preventionGu = "દર 16 હાર પછી ગલગોટાની 1 હાર વાવવી (ટ્રેપ ક્રોપ). ઈયળવાળા ટામેટા વીણીને જમીનમાં દાટી દેવા.",
            biologicalIpmEn = "Install Helilure pheromone traps (5/acre). Spray Bacillus thuringiensis (Bt) @ 2g/L or HaNPV 250 LE/acre in evening.",
            biologicalIpmHi = "हेलिल्योर फेरोमोन ट्रैप (5 प्रति एकड़) लगाएं। बी.टी. (Bt) 2 ग्राम/लीटर या नीम तेल का छिड़काव करें।",
            biologicalIpmGu = "હેલીલ્યોર ફેરોમોન ટ્રેપ એકરે 5 ગોઠવો. સાંજના સમયે બીટી (Bt) પાવડર 2 ગ્રામ/લિટર પાણીમાં છાંટવો."
        ),

        // WHEAT
        DiseasePest(
            id = "wheat_yellow_rust",
            cropId = "wheat",
            healthStatus = HealthStatus.DISEASE,
            nameEn = "Wheat Stripe / Yellow Rust (Puccinia striiformis)",
            nameHi = "गेहूं का पीला रतुआ / पीला गेरुआ रोग",
            nameGu = "ઘઉંનો પીળો ગેરુ રોગ (યલો રસ્ટ)",
            symptomsEn = "Bright yellow elongated pustules arranged in parallel stripes along leaf veins. Yellow dust rubs off on fingers.",
            symptomsHi = "पत्तियों पर नसों के समानांतर चमकीली पीली धारियों में पाउडर जैसी फफोले। हाथ लगाने पर पीला पाउडर उंगलियों पर लगता है।",
            symptomsGu = "પાનની નસોને સમાંતર પીળા પટ્ટામાં ફૂગના કણો. આંગળી અડાડતા પીળો પાવડર આંગળી પર ચોંટે છે.",
            causesEn = "Airborne fungal spores carried by wind under cool humid winter conditions (10-18°C).",
            causesHi = "हवा द्वारा फैलने वाली फफूंद। ठंडे, बादलों वाले और नम मौसम में तेजी से फैलती है।",
            causesGu = "હવા દ્વારા ફેલાતી ફૂગ. શિયાળાના ઠંડા, ભેજવાળા અને વાદળછાયા વાતાવરણમાં ઝડપથી ફેલાય છે.",
            lookForEn = "Parallel yellow powdery stripes on leaf blades, rapid spread across sunny patches.",
            lookForHi = "पत्ती पर सीधी पीली लकीरें, खेत में पीले गोल पैच।",
            lookForGu = "પાન પર લાંબી પીળી રેખાઓ અને ખેતરમાં કુંડાળા સ્વરૂપે પીળાશ.",
            preventionEn = "Sow certified rust-resistant wheat varieties (e.g. HD-2967, PBW-550, GW-451). Avoid late sowing.",
            preventionHi = "रतुआ रोधी प्रमाणित किस्मों की समय पर बुवाई करें। अत्यधिक सिंचाई से बचें।",
            preventionGu = "ગેરુ પ્રતિકારક સુધારેલી જાતોનું સમયસર વાવેતર કરવું. વધુ પડતું પિયત ન આપવું.",
            biologicalIpmEn = "Early morning inspection. Spray bio-formulation Trichoderma viride 5g/L as preventive before canopy closure.",
            biologicalIpmHi = "सुबह के समय खेत की निगरानी। प्राथमिक स्तर पर ट्राइकोडर्मा विरिडी का उपयोग करें।",
            biologicalIpmGu = "સવારે ખેતરમાં ફરી તપાસ કરવી. શરૂઆતની અવસ્થામાં ટ્રાઈકોડર્મા વીરીડી 5 ગ્રામ/લિટર છાંટવું."
        ),

        // GROUNDNUT
        DiseasePest(
            id = "groundnut_tikka",
            cropId = "groundnut",
            healthStatus = HealthStatus.DISEASE,
            nameEn = "Groundnut Tikka Leaf Spot",
            nameHi = "मूंगफली का टिक्का रोग (पर्ण धब्बा)",
            nameGu = "મગફળીનો ટીક્કો રોગ (પાનના ટપકાં)",
            symptomsEn = "Small reddish-brown circular spots on upper leaf surface, surrounded by bright yellow halo. Premature defoliation.",
            symptomsHi = "पत्तियों पर गोल गहरे भूरे से काले धब्बे, धब्बे के चारों ओर चमकीला पीला घेरा और पत्तियों का झड़ना।",
            symptomsGu = "પાન પર ગોળ લાલ-ભૂખરા રંગના ટપકાં અને ટપકાંની ફરતે પીળી કિનારી. પાન ખરી પડે છે અને છોડ સુકાઈ જાય છે.",
            causesEn = "Cercospora arachidicola fungus active in warm, humid weather with intermittent sunshine.",
            causesHi = "सर्कोस्पोरा फफूंद। अधिक आर्द्रता और 25-30 डिग्री तापमान में रोग फैलता है।",
            causesGu = "સર્કોસ્પોરા નામની ફૂગ. ભેજવાળા અને ગરમ વાતાવરણમાં રોગનું પ્રમાણ વધે છે.",
            lookForEn = "Dark spots with bright yellow borders on mature groundnut leaves.",
            lookForHi = "पुरानी पत्तियों पर पीले छल्ले वाले भूरे धब्बे।",
            lookForGu = "પાકા પાન પર પીળા કુંડાળાવાળા કાળા ટપકાં.",
            preventionEn = "Treat seeds with Trichoderma harzianum @ 10g/kg seed before sowing. Crop rotation with cereals.",
            preventionHi = "बुवाई से पहले बीज को ट्राइकोडर्मा (10 ग्राम/किग्रा) से उपचारित करें। अनाज वाली फसलों के साथ फसल चक्र अपनाएं।",
            preventionGu = "વાવેતર પહેલા બિયારણને ટ્રાઈકોડર્મા પાવડર (10 ગ્રામ/કિલો) નો પટ આપવો. પાકની ફેરબદલી કરવી.",
            biologicalIpmEn = "Spray 5% Neem leaf extract or Cow urine (10%) + Asafetida solution at initial appearance.",
            biologicalIpmHi = "नीम का अर्क 5% या गोमूत्र + हींग का घोल शुरुआती लक्षणों पर छिड़कें।",
            biologicalIpmGu = "5% લીમડાના પાનનો અર્ક અથવા ગૌમૂત્ર (10%) દ્રાવણ શરૂઆતમાં છાંટવું."
        ),

        // RICE
        DiseasePest(
            id = "rice_blast",
            cropId = "rice",
            healthStatus = HealthStatus.DISEASE,
            nameEn = "Rice Blast (Pyricularia oryzae)",
            nameHi = "धान का झोंका रोग (ब्लास्ट)",
            nameGu = "ડાંગરનો કરમોડી / બ્લાસ્ટ રોગ",
            symptomsEn = "Spindle-shaped or eye-shaped lesions on leaves with gray/white centers and brownish margins. Neck blast causes grain unfilled.",
            symptomsHi = "पत्तियों पर आंख या नाव के आकार के धब्बे जिनका केंद्र धूसर और किनारे भूरे होते हैं। बाली की गर्दन काली होकर टूट जाती है।",
            symptomsGu = "પાન પર આંખ જેવા કે ત્રાક આકારના ડાઘ, જેની વચ્ચેનો ભાગ રાખોડી અને કિનારી કથ્થાઈ હોય છે. કંકીની ડોક કાળી પડી ભાંગી જાય છે.",
            causesEn = "Magnaporthe oryzae fungus favored by cool nights, high humidity (>90%) and excess nitrogen.",
            causesHi = "हवा द्वारा फैलने वाली फफूंद। ठंडी रातें, अत्यधिक नमी और यूरिया की अधिकता से रोग भड़कता है।",
            causesGu = "મેગ્નાપોર્થે ઓરાઈઝી ફૂગ. ઠંડી રાત, 90% થી વધુ ભેજ અને યુરિયાનો વધુ પડતો ઉપયોગ જવાબદાર છે.",
            lookForEn = "Eye-shaped diamond spots with pointy ends on leaf blade.",
            lookForHi = "पत्ती पर नाव के आकार के नुकीले धब्बे।",
            lookForGu = "પાન પર બંને છેડે અણીવાળા ત્રાક આકારના ડાઘ.",
            preventionEn = "Avoid excessive split doses of urea. Drain standing field water temporarily during severe attack.",
            preventionHi = "यूरिया का संतुलित उपयोग करें। खेत में लगातार पानी न भरे रहने दें।",
            preventionGu = "વધુ પડતો યુરિયા ન નાખવો. રોગ દેખાય ત્યારે ખેતરમાંથી વધારાનું પાણી નિતારી લેવું.",
            biologicalIpmEn = "Foliar spray with Pseudomonas fluorescens @ 10g/L or Trichoderma viride 5g/L.",
            biologicalIpmHi = "स्यूडोमोनास फ्लोरोसेंस (10 ग्राम/ली.) का पत्तियों पर छिड़काव करें।",
            biologicalIpmGu = "સ્યુડોમોનાસ ફ્લોરોસન્સ 10 ગ્રામ/લિટર પાણીમાં ઓગાળી છાંટવું."
        ),

        // CHILLI
        DiseasePest(
            id = "chilli_thrips",
            cropId = "chilli",
            healthStatus = HealthStatus.PEST_INFESTATION,
            nameEn = "Chilli Thrips & Mites (Murda Complex)",
            nameHi = "मिर्च का मरोड़िया रोग / थ्रिप्स कीट",
            nameGu = "મરચીનો કોકડવા / થ્રિપ્સ અને કથીરી (મુરડા)",
            symptomsEn = "Leaves curl upward (boat-shaped) from thrips or downward (inverted cup) from yellow mites. Leaves become brittle and small.",
            symptomsHi = "पत्तियां ऊपर की ओर नाव की तरह मुड़ जाती हैं (थ्रिप्स) या नीचे की ओर मुड़ती हैं (माइट्स)। पत्तियां छोटी और खुरदरी हो जाती हैं।",
            symptomsGu = "પાન ઉપર તરફ હોડી આકારમાં વળી જાય છે (થ્રિપ્સ) અથવા નીચે તરફ વળે છે (કથીરી). પાન કડક અને નાના થઈ જાય છે.",
            causesEn = "Tiny rasping-sucking thrips and microscopic mites sucking sap from tender terminal buds.",
            causesHi = "बारीक रस चूसने वाले थ्रिप्स और माइट्स जो कोमल पत्तियों से रस चूसते हैं।",
            causesGu = "અતિ સૂક્ષ્મ થ્રિપ્સ અને પીળી કથીરી જે કૂણા પાનમાંથી રસ ચૂસે છે.",
            lookForEn = "Upward curled leaves with brownish scratches underneath.",
            lookForHi = "ऊपर की ओर मुड़ी हुई नाव जैसी पत्तियां।",
            lookForGu = "હોડી આકારના વળી ગયેલા પાન અને પાનની પાછળ કથ્થાઈ લિસોટા.",
            preventionEn = "Grow border crops like maize or sorghum around chilli plot. Spray neem oil early.",
            preventionHi = "मिर्च के खेत के चारों तरफ मक्का या ज्वार की 2 कतारें लगाएं।",
            preventionGu = "મરચીના ખેતર ફરતે મકાઈ કે જુવારની 2-3 હાર વાવવી (બોર્ડર ક્રોપ).",
            biologicalIpmEn = "Install blue sticky traps (10/acre) for thrips and yellow traps for whiteflies. Spray 5% NSKE.",
            biologicalIpmHi = "नीले चिपचिपे कार्ड (10 प्रति एकड़) लगाएं। नीम का अर्क (5% NSKE) छिड़कें।",
            biologicalIpmGu = "એકર દીઠ 10 વાદળી (બ્લૂ) સ્ટીકી ટ્રેપ લગાવો. લીંબોળીનું તેલ 30 મિલી/પંપ છાંટવું."
        )
    )

    val verifiedPesticides = listOf(
        // COTTON APHID
        VerifiedPesticide(
            id = "pest_cotton_aphid",
            cropId = "cotton",
            diseasePestId = "cotton_aphid",
            targetNameEn = "Aphids, Jassids & Whiteflies",
            activeIngredient = "Imidacloprid 17.8% SL",
            formulation = "Soluble Liquid (SL)",
            applicationRateEn = "3 to 4 ml per 15 Litre knapsack spray pump (40-50 ml in 150-200 L water per acre)",
            applicationRateHi = "3 से 4 मिली प्रति 15 लीटर पंप (40-50 मिली प्रति एकड़ 150-200 लीटर पानी में)",
            applicationRateGu = "3 થી 4 મિલી પ્રતિ 15 લિટર પંપ (40-50 મિલી પ્રતિ એકર 150-200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "40 days",
            verifiedAuthority = "Central Insecticides Board & Registration Committee (CIBRC) / Anand Agricultural University (AAU)",
            safetyWarningsEn = listOf(
                "Wear rubber gloves and long-sleeve protective clothing.",
                "Do not spray during full bloom to protect honeybees.",
                "Avoid spraying during strong wind or direct midday sunlight.",
                "Keep containers safely away from livestock and children."
            ),
            safetyWarningsHi = listOf(
                "रबर के दस्ताने और सुरक्षात्मक कपड़े पहनें।",
                "मधुमक्खियों की सुरक्षा के लिए पूर्ण फूल आने पर न छिड़कें।",
                "तेज हवा या दोपहर की कड़ी धूप में छिड़काव न करें।",
                "दवा की बोतल बच्चों और पालतू जानवरों से दूर रखें।"
            ),
            safetyWarningsGu = listOf(
                "રબરના મોજા અને રક્ષણાત્મક કપડાં પહેરો.",
                "મધમાખીઓના રક્ષણ માટે ફૂલ ખીલેલા હોય ત્યારે છંટકાવ ન કરવો.",
                "સખત પવન કે બપોરના આકરા તડકામાં છંટકાવ ટાળવો.",
                "દવાની બોટલ પશુઓ અને બાળકોથી દૂર રાખવી."
            )
        ),

        // COTTON WHITEFLY (VECTOR OF LEAF CURL)
        VerifiedPesticide(
            id = "pest_cotton_curl",
            cropId = "cotton",
            diseasePestId = "cotton_curl",
            targetNameEn = "Whitefly Vector Management (CLCuD)",
            activeIngredient = "Diafenthiuron 50% WP",
            formulation = "Wettable Powder (WP)",
            applicationRateEn = "18 to 20 grams per 15 Litre knapsack spray pump (200-240 g per acre in 200 L water)",
            applicationRateHi = "18 से 20 ग्राम प्रति 15 लीटर पंप (200-240 ग्राम प्रति एकड़ 200 लीटर पानी में)",
            applicationRateGu = "18 થી 20 ગ્રામ પ્રતિ 15 લિટર પંપ (200-240 ગ્રામ પ્રતિ એકર 200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "30 days",
            verifiedAuthority = "ICAR - Central Institute for Cotton Research (CICR) / CIBRC Label",
            safetyWarningsEn = listOf(
                "CLCuD is viral: chemical controls whitefly vector only, does not cure already curled leaves.",
                "Wear face mask to prevent inhaling powder or spray mist.",
                "Do not mix with alkaline solutions or Bordeaux mixture.",
                "Wash hands thoroughly with soap after application."
            ),
            safetyWarningsHi = listOf(
                "यह दवा केवल सफेद मक्खी रोकती है, मुड़े हुए पत्तों को ठीक नहीं करती।",
                "दवा का पाउडर सांस में न जाए इसके लिए मास्क पहनें।",
                "छिड़काव के बाद हाथ-मुंह साबुन से अच्छी तरह धोएं।"
            ),
            safetyWarningsGu = listOf(
                "આ દવા માત્ર સફેદ માખીને નિયંત્રિત કરે છે, વળી ગયેલા પાનને સીધા કરતી નથી.",
                "નાક પર માસ્ક પહેરવો જેથી દવા શ્વાસમાં ન જાય.",
                "છંટકાવ પછી હાથ-મોં સાબુથી બરાબર ધોવા."
            )
        ),

        // TOMATO EARLY BLIGHT
        VerifiedPesticide(
            id = "pest_tomato_early_blight",
            cropId = "tomato",
            diseasePestId = "tomato_early_blight",
            targetNameEn = "Alternaria Early Blight & Leaf Spot",
            activeIngredient = "Mancozeb 75% WP",
            formulation = "Wettable Powder (WP)",
            applicationRateEn = "30 to 35 grams per 15 Litre knapsack spray pump (400-500 g per acre in 200 L water)",
            applicationRateHi = "30 से 35 ग्राम प्रति 15 लीटर पंप (400-500 ग्राम प्रति एकड़ 200 लीटर पानी में)",
            applicationRateGu = "30 થી 35 ગ્રામ પ્રતિ 15 લિટર પંપ (400-500 ગ્રામ પ્રતિ એકર 200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "7 days",
            verifiedAuthority = "Indian Agricultural Research Institute (ICAR-IARI) / CIBRC Verified",
            safetyWarningsEn = listOf(
                "Ensure uniform coverage on both upper and lower leaf surfaces.",
                "Strictly observe 7-day waiting period before harvesting ripe tomatoes.",
                "Do not graze cattle in treated tomato plots.",
                "Wear protective eye goggles to avoid ocular irritation."
            ),
            safetyWarningsHi = listOf(
                "पत्तियों के ऊपर और नीचे दोनों तरफ एकसमान छिड़काव करें।",
                "टमाटर तोड़ने से 7 दिन पहले छिड़काव बंद कर दें (PHI)।",
                "छिड़काव के दौरान आंखों में चश्मा पहनें।"
            ),
            safetyWarningsGu = listOf(
                "પાનના ઉપર અને નીચે બંને ભાગ પર સરખી રીતે દવા પહોંચે તેમ છંટકાવ કરવો.",
                "ટામેટા ઉતારવાના 7 દિવસ પહેલાં છેલ્લો છંટકાવ બંધ કરવો (PHI).",
                "છંટકાવ કરતી વખતે આંખમાં ગોગલ્સ/ચશ્મા પહેરવા."
            )
        ),

        // TOMATO FRUIT BORER
        VerifiedPesticide(
            id = "pest_tomato_fruit_borer",
            cropId = "tomato",
            diseasePestId = "tomato_fruit_borer",
            targetNameEn = "Tomato Fruit Borer (Helicoverpa)",
            activeIngredient = "Chlorantraniliprole 18.5% SC",
            formulation = "Suspension Concentrate (SC)",
            applicationRateEn = "6 ml per 15 Litre knapsack spray pump (60 ml per acre in 150-200 L water)",
            applicationRateHi = "6 मिली प्रति 15 लीटर पंप (60 मिली प्रति एकड़ 150-200 लीटर पानी में)",
            applicationRateGu = "6 મિલી પ્રતિ 15 લિટર પંપ (60 મિલી પ્રતિ એકર 150-200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "3 days",
            verifiedAuthority = "ICAR - Indian Institute of Vegetable Research (IIVR) / CIBRC",
            safetyWarningsEn = listOf(
                "Apply during evening hours when borer caterpillars are active.",
                "Do not exceed recommended dilution of 6 ml per pump.",
                "Safe waiting period of 3 days before picking market tomatoes.",
                "Dispose of empty bottles responsibly by burying deeply away from water channels."
            ),
            safetyWarningsHi = listOf(
                "शाम के समय छिड़काव करें जब सुंडी सक्रिय होती है।",
                "मात्रा 6 मिली प्रति पंप से अधिक न बढ़ाएं।",
                "टमाटर तोड़ने से 3 दिन पहले छिड़काव करें।",
                "खाली बोतलों को पानी के स्रोतों से दूर जमीन में गाड़ें।"
            ),
            safetyWarningsGu = listOf(
                "સાંજના સમયે છંટકાવ કરવો જ્યારે ઈયળો બહાર નીકળે છે.",
                "પંપ દીઠ 6 મિલીથી વધુ માત્રા ન લેવી.",
                "ટામેટા વીણવાના 3 દિવસ પહેલા છંટકાવ બંધ રાખવો.",
                "ખાલી બોટલો પાણીના કુવા કે તળાવથી દૂર ઊંડો ખાડો કરી દાટી દેવી."
            )
        ),

        // WHEAT YELLOW RUST
        VerifiedPesticide(
            id = "pest_wheat_rust",
            cropId = "wheat",
            diseasePestId = "wheat_yellow_rust",
            targetNameEn = "Stripe & Yellow Rust of Wheat",
            activeIngredient = "Propiconazole 25% EC",
            formulation = "Emulsifiable Concentrate (EC)",
            applicationRateEn = "15 ml per 15 Litre knapsack spray pump (200 ml per acre in 200 L water)",
            applicationRateHi = "15 मिली प्रति 15 लीटर पंप (200 मिली प्रति एकड़ 200 लीटर पानी में)",
            applicationRateGu = "15 મિલી પ્રતિ 15 લિટર પંપ (200 મિલી પ્રતિ એકર 200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "30 days",
            verifiedAuthority = "ICAR - Indian Institute of Wheat and Barley Research (IIWBR) / PAU",
            safetyWarningsEn = listOf(
                "Spray immediately on detecting first yellow pustule patch in field.",
                "Spray with hollow cone nozzle using at least 200 litres water per acre.",
                "Do not spray if rain is anticipated within 4 hours.",
                "Keep domestic animals away from the field for 48 hours."
            ),
            safetyWarningsHi = listOf(
                "खेत में पहला पीला धब्बा दिखते ही तुरंत छिड़काव करें।",
                "प्रति एकड़ कम से कम 200 लीटर पानी का उपयोग करें।",
                "यदि 4 घंटे में बारिश की संभावना हो तो छिड़काव न करें।"
            ),
            safetyWarningsGu = listOf(
                "ખેતરમાં પીળો ડાઘ દેખાતા જ તરત જ છંટકાવ કરવો.",
                "એકરે ઓછામાં ઓછું 200 લિટર પાણી વાપરવું.",
                "જો 4 કલાકમાં વરસાદની શક્યતા હોય તો છંટકાવ મોકૂફ રાખવો."
            )
        ),

        // GROUNDNUT TIKKA
        VerifiedPesticide(
            id = "pest_groundnut_tikka",
            cropId = "groundnut",
            diseasePestId = "groundnut_tikka",
            targetNameEn = "Tikka Leaf Spot",
            activeIngredient = "Carbendazim 12% + Mancozeb 63% WP",
            formulation = "Wettable Powder (WP)",
            applicationRateEn = "30 grams per 15 Litre knapsack spray pump (350-400 g per acre in 150-200 L water)",
            applicationRateHi = "30 ग्राम प्रति 15 लीटर पंप (350-400 ग्राम प्रति एकड़ 150-200 लीटर पानी में)",
            applicationRateGu = "30 ગ્રામ પ્રતિ 15 લિટર પંપ (350-400 ગ્રામ પ્રતિ એકર 150-200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "30 days",
            verifiedAuthority = "Directorate of Groundnut Research (ICAR-DGR Junagadh) / CIBRC",
            safetyWarningsEn = listOf(
                "Mix powder thoroughly in a bucket of water before pouring into the spray pump.",
                "Spray when morning dew has dried from leaves.",
                "Wear protective face mask and gloves.",
                "Do not feed sprayed crop foliage to dairy cattle before harvest."
            ),
            safetyWarningsHi = listOf(
                "दवा को पहले बाल्टी में घोलें, फिर पंप में डालें।",
                "सुबह की ओस सूखने के बाद ही छिड़काव करें।",
                "छिड़काव वाली हरी पत्तियां दुधारू पशुओं को न खिलाएं।"
            ),
            safetyWarningsGu = listOf(
                "દવાને પહેલા ડોલમાં પાણી સાથે બરાબર ઓગાળીને પછી જ પંપમાં રેડવી.",
                "સવારનું ઝાકળ સુકાઈ જાય પછી જ છંટકાવ કરવો.",
                "દવા છાંટેલ નીંદણ કે પાલા પશુઓને ખવડાવવા નહીં."
            )
        ),

        // RICE BLAST
        VerifiedPesticide(
            id = "pest_rice_blast",
            cropId = "rice",
            diseasePestId = "rice_blast",
            targetNameEn = "Leaf & Neck Blast",
            activeIngredient = "Tricyclazole 75% WP",
            formulation = "Wettable Powder (WP)",
            applicationRateEn = "8 to 10 grams per 15 Litre knapsack spray pump (120-150 g per acre in 200 L water)",
            applicationRateHi = "8 से 10 ग्राम प्रति 15 लीटर पंप (120-150 ग्राम प्रति एकड़ 200 लीटर पानी में)",
            applicationRateGu = "8 થી 10 ગ્રામ પ્રતિ 15 લિટર પંપ (120-150 ગ્રામ પ્રતિ એકર 200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "30 days",
            verifiedAuthority = "ICAR - National Rice Research Institute (NRRI) / CIBRC",
            safetyWarningsEn = listOf(
                "Best applied before neck emergence if weather is humid and overcast.",
                "Ensure spray reaches the base and canopy of rice plants.",
                "Wash spray equipment away from irrigation canals or fish ponds."
            ),
            safetyWarningsHi = listOf(
                "बाली निकलने से ठीक पहले छिड़काव सबसे प्रभावी होता है।",
                "मछली पालन वाले तालाबों के पास उपकरण न धोएं।"
            ),
            safetyWarningsGu = listOf(
                "કંકી બહાર આવે તે પહેલાં છંટકાવ કરવો સૌથી વધુ અસરકારક છે.",
                "મત્સ્ય તળાવ કે સિંચાઈ નહેર પાસે પંપ ધોવો નહીં."
            )
        ),

        // CHILLI THRIPS
        VerifiedPesticide(
            id = "pest_chilli_thrips",
            cropId = "chilli",
            diseasePestId = "chilli_thrips",
            targetNameEn = "Chilli Thrips & Fruit Borers",
            activeIngredient = "Spinosad 45% SC",
            formulation = "Suspension Concentrate (SC)",
            applicationRateEn = "3.5 to 4 ml per 15 Litre knapsack spray pump (50-60 ml per acre in 200 L water)",
            applicationRateHi = "3.5 से 4 मिली प्रति 15 लीटर पंप (50-60 मिली प्रति एकड़ 200 लीटर पानी में)",
            applicationRateGu = "3.5 થી 4 મિલી પ્રતિ 15 લિટર પંપ (50-60 મિલી પ્રતિ એકર 200 લિટર પાણીમાં)",
            preHarvestIntervalDays = "3 days",
            verifiedAuthority = "ICAR - Indian Institute of Horticultural Research (IIHR) / CIBRC",
            safetyWarningsEn = listOf(
                "Spray late in the afternoon after bee foraging hours.",
                "Rotate with different mode of action (e.g. Diafenthiuron) to avoid resistance.",
                "Short 3-day PHI suitable for continuous green chilli harvests."
            ),
            safetyWarningsHi = listOf(
                "शाम के समय छिड़काव करें ताकि मधुमक्खियों को नुकसान न हो।",
                "हरी मिर्च की तुड़ाई से 3 दिन पहले तक सुरक्षित।"
            ),
            safetyWarningsGu = listOf(
                "સાંજના સમયે છંટકાવ કરવો જેથી મધમાખીઓને નુકસાન ન થાય.",
                "લીલા મરચાં ઉતારવાના 3 દિવસ પહેલાં છંટકાવ બંધ કરવો."
            )
        )
    )

    private val dynamicCrops = java.util.concurrent.ConcurrentHashMap<String, Crop>()

    fun registerDynamicCrop(crop: Crop) {
        dynamicCrops[crop.id.lowercase()] = crop
    }

    fun getAllKnownCrops(): List<Crop> {
        return crops + dynamicCrops.values.toList()
    }

    fun getCropById(cropId: String): Crop {
        if (cropId.equals("non_crop", ignoreCase = true)) return Crop.NON_CROP
        if (cropId.equals("unknown_crop", ignoreCase = true) || cropId.equals("unknown", ignoreCase = true)) return Crop.UNKNOWN_CROP
        crops.find { it.id.equals(cropId, ignoreCase = true) }?.let { return it }
        dynamicCrops[cropId.lowercase()]?.let { return it }
        return Crop(
            id = cropId,
            nameEn = cropId.replaceFirstChar { it.uppercase() },
            nameHi = cropId,
            nameGu = cropId,
            scientificName = "Plantae",
            emoji = "🌿"
        )
    }

    fun getDiseasePest(id: String): DiseasePest? {
        return diseasesAndPests.find { it.id.equals(id, ignoreCase = true) }
    }

    fun getPesticideForDisease(diseaseId: String): VerifiedPesticide? {
        return verifiedPesticides.find { it.diseasePestId.equals(diseaseId, ignoreCase = true) }
    }

    /**
     * Complete offline library of verified crop disease prevention tips and sustainable IPM agronomic guidance.
     */
    fun generateDefaultPreventionTips(): List<PreventionTipEntity> {
        return listOf(
            // 1. COTTON - LEAF CURL
            PreventionTipEntity(
                cropId = "cotton",
                cropNameEn = "Cotton",
                cropNameHi = "कपास",
                cropNameGu = "કપાસ",
                diseaseId = "cotton_curl",
                diseaseNameEn = "Cotton Leaf Curl Virus (CLCuD)",
                diseaseNameHi = "कपास का लीफ कर्ल वायरस",
                diseaseNameGu = "કપાસમાં લીફ કર્લ (પાન વળવાનો રોગ)",
                category = "VIRAL",
                symptomsSummaryEn = "Leaves cup upwards, veins thicken unnaturally, and small leaf-like enations sprout beneath the leaf.",
                symptomsSummaryHi = "पत्ते ऊपर की तरफ मुड़ जाते हैं, नसें मोटी होती हैं और नीचे छोटी पत्ती जैसी गांठें बनती हैं।",
                symptomsSummaryGu = "પાન રકાબી જેવા ઉપર વળે છે, નસો અસામાન્ય જાડી થાય છે અને પાન નીચે નાની પાંદડી જેવી ગાંઠો થાય છે.",
                preventionEn = "Sow certified resistant Bt cotton varieties. Eradicate weed hosts like Abutilon (Kanghi/Khapati) and Parthenium from field borders before sowing.",
                preventionHi = "रोगरोधी प्रमाणित किस्मों की समय पर बुवाई करें। खेत की मेड़ों से कंघी और गाजरघास जैसे खरपतवार नष्ट करें।",
                preventionGu = "પ્રમાણિત રોગપ્રતિકારક જાતો વાવવી. વાવેતર પહેલાં શેઢા-પાળા પરથી ખપટી અને ગાજરઘાસ જેવા નીંદણ દૂર કરવા.",
                biologicalIpmEn = "Install 10-15 yellow sticky traps per acre at crop canopy level. Spray Neem oil (10,000 ppm) at 30 ml per 15L water to repel whiteflies.",
                biologicalIpmHi = "10-15 पीले चिपचिपे ट्रैप प्रति एकड़ लगाएं। सफेद मक्खी को रोकने के लिए नीम तेल (10,000 ppm) 30 मिली प्रति 15 लीटर पंप छिड़कें।",
                biologicalIpmGu = "એકર દીઠ 10-15 પીળા સ્ટીકી ટ્રેપ લગાવો. સફેદ માખીના નિયંત્રણ માટે લીંબોળીનું તેલ (10,000 ppm) 30 મિલી પ્રતિ 15 લિટર પંપમાં છાંટવું.",
                culturalPracticesEn = "Avoid planting alternative host crops like okra or brinjal near cotton plots. Roguing of infected plants within 45 days of sowing.",
                culturalPracticesHi = "कपास के पास भिंडी या बैंगन की खेती न करें। बुवाई के 45 दिनों के अंदर रोगग्रस्त पौधों को उखाड़कर गाड़ दें।",
                culturalPracticesGu = "કપાસની બાજુમાં ભીંડા કે રીંગણનું વાવેતર ન કરવું. વાવણીના 45 દિવસમાં રોગિષ્ઠ છોડ ઉપાડી જમીનમાં દાટી દેવા."
            ),

            // 2. COTTON - SUCKING PESTS (APHIDS & JASSIDS)
            PreventionTipEntity(
                cropId = "cotton",
                cropNameEn = "Cotton",
                cropNameHi = "कपास",
                cropNameGu = "કપાસ",
                diseaseId = "cotton_aphid",
                diseaseNameEn = "Aphids & Jassids (Sucking Pests)",
                diseaseNameHi = "माहू, तेला और हरा फुदका (रस चूसक कीट)",
                diseaseNameGu = "મોલો-મશી અને તડતડિયાં (રસ ચૂસતી જીવાત)",
                category = "PEST",
                symptomsSummaryEn = "Leaves cup downwards, yellow margins on leaf rims (hopper burn), honeydew secretion and sooty mold.",
                symptomsSummaryHi = "पत्तियां नीचे मुड़ती हैं, किनारे पीले-लाल पड़ते हैं (हॉपर बर्न) और काला फफूंद जमता है।",
                symptomsSummaryGu = "પાન નીચેની તરફ વળે છે, કિનારી પીળી-લાલ પડે છે (હોપર બર્ન) અને ચીકણો ગુંદર જેવો રસ ઝરે છે.",
                preventionEn = "Avoid excessive split doses of Urea/Nitrogen that make foliage succulent. Grow castor or marigold as trap crops.",
                preventionHi = "यूरिया का जरूरत से ज्यादा उपयोग न करें। खेत के किनारों पर अरंडी या गेंदा की कतारें लगाएं।",
                preventionGu = "વધારાનું યુરિયા ખાતર આપવાનું ટાળવું. ખેતરની ફરતે એરંડા કે ગલગોટાનું વાવેતર કરવું.",
                biologicalIpmEn = "Spray Verticillium lecanii or Beauveria bassiana @ 50g per 15L pump. Conserve natural predators like Ladybird beetles and Chrysoperla.",
                biologicalIpmHi = "वर्टिसिलियम लेकानी फफूंद (50 ग्राम/15 ली.) का छिड़काव करें। लेडीबर्ड बीटल जैसे मित्र कीटों का संरक्षण करें।",
                biologicalIpmGu = "વર્ટીસીલીયમ લેકાની 50 ગ્રામ પ્રતિ 15 લિટર પંપમાં છાંટવું. દાળિયા (લેડીબર્ડ બીટલ) અને ક્રાયસોપા જેવા મિત્ર કીટકોનું રક્ષણ કરવું.",
                culturalPracticesEn = "Intercrop with cowpea or green gram (1:4 ratio) to invite predator insects naturally.",
                culturalPracticesHi = "कपास के साथ मूंग या लोबिया की अंतःवर्तीय खेती (1:4 अनुपात) करें।",
                culturalPracticesGu = "કપાસમાં મગ અથવા ચોળીનું આંતરપાક વાવેતર કરવું જેથી મિત્ર કીટકો આકર્ષાય."
            ),

            // 3. COTTON - PINK BOLLWORM
            PreventionTipEntity(
                cropId = "cotton",
                cropNameEn = "Cotton",
                cropNameHi = "कपास",
                cropNameGu = "કપાસ",
                diseaseId = "cotton_bollworm",
                diseaseNameEn = "Pink Bollworm (PBW) Management",
                diseaseNameHi = "गुलाबी सुंडी (पिंक बॉलवॉर्म) रोकथाम",
                diseaseNameGu = "ગુલાબી ઇયળનું સંકલિત નિયંત્રણ",
                category = "PEST",
                symptomsSummaryEn = "Rosetted flowers that fail to open normally, bored holes in developing bolls with stained lint.",
                symptomsSummaryHi = "गुलाब जैसे बंद फूल (रोसेट फ्लावर) और गूलरों में छेद, जिससे कपास की रुई काली और खराब होती है।",
                symptomsSummaryGu = "રોઝેટ ફૂલ (ગુલાબ જેવું બંધ રહેતું ફૂલ), જીંડવામાં અંદર ઝીણા કાણાં અને રૂની ગુણવત્તા બગડવી.",
                preventionEn = "Install Pheromone traps (Gossyplure) @ 4 per acre from 45 days after sowing for monitoring moth spikes.",
                preventionHi = "बुवाई के 45 दिन बाद फेरोमोन ट्रैप (4 प्रति एकड़) निगरानी हेतु अवश्य लगाएं।",
                preventionGu = "વાવણીના 45 દિવસ બાદ એકર દીઠ 4 ફેરોમોન ટ્રેપ (ગોસીપ્લુર લ્યોર) લગાવી ફુદાંની દેખરેખ રાખવી.",
                biologicalIpmEn = "Release Trichogramma bactrae egg parasitoid cards @ 60,000 eggs/acre at flower initiation (60-70 DAS).",
                biologicalIpmHi = "फूल आने पर ट्राइकोग्रामा कार्ड (60,000 अंडे/एकड़) सप्ताह के अंतराल पर 3-4 बार लगाएं।",
                biologicalIpmGu = "ફૂલ બેસવાની અવસ્થાએ ટ્રાઈકોગ્રામા બેક્ટ્રીના ટ્રાઈકોકાર્ડ એકર દીઠ 2-3 પત્તા (60,000 ઈંડા) લગાવવા.",
                culturalPracticesEn = "Terminate crop by end of January to break pest lifecycle. Do not stack cotton stalks near fields.",
                culturalPracticesHi = "जनवरी अंत तक फसल चक्र समाप्त करें ताकि सुंडी का जीवनचक्र टूट सके। खेत में लकड़ियों का ढेर न रखें।",
                culturalPracticesGu = "જાન્યુઆરીના અંત સુધીમાં પાક પૂર્ણ કરી પરાઠીનો નિકાલ કરવો. ખેતરમાં સાંઠીના ઢગલા ન રાખવા."
            ),

            // 4. TOMATO - EARLY & LATE BLIGHT
            PreventionTipEntity(
                cropId = "tomato",
                cropNameEn = "Tomato",
                cropNameHi = "टमाटर",
                cropNameGu = "ટામેટા",
                diseaseId = "tomato_early_blight",
                diseaseNameEn = "Early & Late Blight of Tomato",
                diseaseNameHi = "टमाटर का अगेती व पछेती झुलसा",
                diseaseNameGu = "ટામેટાનો આગોતરો અને પાછોતરો સુકારો",
                category = "FUNGAL",
                symptomsSummaryEn = "Concentric target-board rings on lower leaves (Early blight) or water-soaked dark lesions with white mold (Late blight).",
                symptomsSummaryHi = "पुरानी पत्तियों पर गोल छल्लेदार काले धब्बे (अगेती) या गीले काले घाव के नीचे सफेद फफूंद (पछेती)।",
                symptomsSummaryGu = "પાન પર કુંડાળાવાળા કાળા ટપકાં (આગોતરો) અથવા પાણીપોચા કાળા ડાઘ અને પાન સુકાઈ જવા (પાછોતરો સુકારો).",
                preventionEn = "Stake tomato plants with bamboo poles to keep leaves elevated away from damp soil. Practice drip irrigation.",
                preventionHi = "टमाटर के पौधों को बांस के सहारे बांधें (स्टेकिंग)। ड्रिप सिंचाई से पानी दें ताकि पत्तियां सूखी रहें।",
                preventionGu = "છોડને વાંસના ટેકા (સ્ટેકિંગ) આપી જમીનથી ઊંચા રાખવા જેથી પાન ભીની માટીને ન અડે. ટપક પિયત પદ્ધતિ અપનાવવી.",
                biologicalIpmEn = "Prophylactic spray of Trichoderma harzianum @ 5g/L before rain spells. Apply copper oxychloride 50% WP @ 30g/15L if weather stays cloudy.",
                biologicalIpmHi = "बादल छाने पर ट्राइकोडर्मा (5 ग्राम/ली.) या कॉपर ऑक्सीक्लोराइड 50% (30 ग्राम/15 ली.) का छिड़काव करें।",
                biologicalIpmGu = "વાદળછાયા વાતાવરણમાં ટ્રાઈકોડર્મા 50 ગ્રામ અથવા કોપર ઓક્સીક્લોરાઇડ 35-40 ગ્રામ પ્રતિ 15 લિટર પંપમાં છાંટવું.",
                culturalPracticesEn = "Prune bottom 6-8 inches of foliage (suckers) to improve airflow and stop soil splash during irrigation.",
                culturalPracticesHi = "जमीन से 6-8 इंच तक की निचली पत्तियों को काट दें ताकि हवा का संचार हो और फंगस न फैले।",
                culturalPracticesGu = "જમીનથી નીચેની 6-8 ઇંચ સુધીની ડાળીઓ-પાન કાપી નાખવા જેથી હવા ઉજાસ રહે અને જમીનનો ભેજ પાનને ન અડે."
            ),

            // 5. TOMATO - BLOSSOM END ROT (CALCIUM DEFICIENCY)
            PreventionTipEntity(
                cropId = "tomato",
                cropNameEn = "Tomato",
                cropNameHi = "टमाटर",
                cropNameGu = "ટામેટા",
                diseaseId = "tomato_blossom_rot",
                diseaseNameEn = "Blossom End Rot (Calcium Deficiency)",
                diseaseNameHi = "ब्लॉसम एंड रॉट (कैल्शियम कमी व फल सड़न)",
                diseaseNameGu = "ટામેટામાં નીચેનો ભાગ કાળો પડવો (કેલ્શિયમની ખામી)",
                category = "DEFICIENCY",
                symptomsSummaryEn = "Bottom base (blossom end) of developing fruit turns flattened, leathery, dark brown or sunken black.",
                symptomsSummaryHi = "फल का निचला हिस्सा चपटा, चमड़े जैसा काला और धंसा हुआ हो जाता है।",
                symptomsSummaryGu = "ફળનો નીચેનો છેડો ચપટો, કાળો, ચાઠાવાળો અને ખાડા જેવો થઈ સુકાઈ જાય છે.",
                preventionEn = "Ensure uniform, steady soil moisture. Wide swings between heavy flooding and severe drying prevent calcium uptake.",
                preventionHi = "खेत में नमी का संतुलन बनाए रखें। अधिक सूखे के बाद अचानक भारी सिंचाई न करें।",
                preventionGu = "જમીનમાં નિયમિત અને સમાન ભેજ જાળવવો. લાંબા ગાળે વધુ પડતું પાણી આપવાને બદલે હળવું પિયત આપવું.",
                biologicalIpmEn = "Foliar spray of Chelated Calcium (EDTA 10%) @ 1g/L or Calcium Nitrate @ 5g/L during early fruit development.",
                biologicalIpmHi = "फल बनने की शुरुआत में कैल्शियम नाइट्रेट (5 ग्राम/ली.) का पर्णीय छिड़काव करें।",
                biologicalIpmGu = "ફળ બેસતી વખતે કેલ્શિયમ નાઈટ્રેટ 50-60 ગ્રામ પ્રતિ પંપ અથવા કિલેટેડ કેલ્શિયમ છાંટવું.",
                culturalPracticesEn = "Apply agricultural gypsum or dolomite lime in acidic soils prior to transplanting. Use organic mulch.",
                culturalPracticesHi = "रोपाई से पहले खेत में जिप्सम या डोलोमाइट मिलाएं। प्लास्टिक या पुआल की मल्चिंग करें।",
                culturalPracticesGu = "વાવેતર પહેલા જમીનમાં જીપ્સમ (ચિરોડી) આપવી અને પ્લાસ્ટિક મલ્ચિંગ કરવું."
            ),

            // 6. WHEAT - YELLOW STRIPE & BROWN RUST
            PreventionTipEntity(
                cropId = "wheat",
                cropNameEn = "Wheat",
                cropNameHi = "गेहूं",
                cropNameGu = "ઘઉં",
                diseaseId = "wheat_rust",
                diseaseNameEn = "Stripe / Yellow & Brown Rust",
                diseaseNameHi = "गेहूं का पीला व भूरा रतुआ (गेरुआ)",
                diseaseNameGu = "ઘઉંનો પીળો અને કથ્થાઈ ગેરુ રોગ",
                category = "FUNGAL",
                symptomsSummaryEn = "Parallel yellow powdery stripes on leaf blades or circular orange-brown pustules that leave powder on fingers.",
                symptomsSummaryHi = "पत्तियों पर सीधी पीली लकीरें या नारंगी-भूरे दाने जो छूने पर उंगलियों पर पाउडर छोड़ते हैं।",
                symptomsSummaryGu = "પાન પર નસોને સમાંતર પીળી પટ્ટીઓ અથવા કથ્થાઈ રંગના ફોલ્લા. આંગળી અડાડતા પીળો-કથ્થાઈ પાવડર ચોંટે છે.",
                preventionEn = "Sow certified rust-tolerant varieties like GW-451, GW-366, or HD-2967. Complete timely sowing before Nov 25.",
                preventionHi = "रतुआ रोधी प्रमाणित किस्मों (GW-451, HD-2967) की 25 नवंबर से पहले बुवाई पूरी करें।",
                preventionGu = "ગેરુ પ્રતિકારક સુધારેલી જાતો (જીડબલ્યુ-451, જીડબલ્યુ-366) નું 25 નવેમ્બર પહેલાં સમયસર વાવેતર કરવું.",
                biologicalIpmEn = "Scout early in morning for yellow patches. Preventive spray of Bio-fungicide Trichoderma viride @ 5g/L.",
                biologicalIpmHi = "सुबह के समय खेत में पीले घेरों की जांच करें। प्रारंभिक अवस्था में ट्राइकोडर्मा विरिडी का छिड़काव करें।",
                biologicalIpmGu = "સવારે ખેતરમાં ફરી પીળા કુંડાળા તપાસવા. શરૂઆતની અવસ્થામાં ટ્રાઈકોડર્મા વીરીડી 5 ગ્રામ/લિટર છાંટવું.",
                culturalPracticesEn = "Avoid excessive split urea applications during cool foggy days in December-January.",
                culturalPracticesHi = "दिसंबर-जनवरी में कोहरे के समय अत्यधिक यूरिया डालने से बचें।",
                culturalPracticesGu = "ડિસેમ્બર-જાન્યુઆરીના ઝાકળવાળા દિવસોમાં વધુ પડતો યુરિયા આપવો નહીં."
            ),

            // 7. WHEAT - LOOSE SMUT
            PreventionTipEntity(
                cropId = "wheat",
                cropNameEn = "Wheat",
                cropNameHi = "गेहूं",
                cropNameGu = "ઘઉં",
                diseaseId = "wheat_loose_smut",
                diseaseNameEn = "Loose Smut Seed-Borne Disease",
                diseaseNameHi = "गेहूं का खुला कंडुआ (लूज स्मट)",
                diseaseNameGu = "ઘઉંનો અંગારિયો / કાળી મેશ રોગ",
                category = "FUNGAL",
                symptomsSummaryEn = "Entire earhead transformed into black powdery mass of smut spores, leaving only bare central rachis.",
                symptomsSummaryHi = "गेहूं की पूरी बाली काले चूर्ण में बदल जाती है और केवल नंगा डंठल बचता है।",
                symptomsSummaryGu = "ઘઉંની આખી ઊંબી કાળી મેશ જેવી ભૂકીમાં ફેરવાઈ જાય છે અને માત્ર દાંડલી જ બાકી રહે છે.",
                preventionEn = "Mandatory seed treatment with Carboxin 37.5% + Thiram 37.5% DS @ 2.5g/kg or Trichoderma viride @ 10g/kg seed.",
                preventionHi = "कार्बोक्सिन + थीरम (2.5 ग्राम/किग्रा) या ट्राइकोडर्मा (10 ग्राम/किग्रा) से अनिवार्य बीज उपचार करें।",
                preventionGu = "વાવણી પહેલા બિયારણને કાર્બોક્સિન + થીરમ (2.5 ગ્રામ/કિલો) અથવા ટ્રાઈકોડર્મા (10 ગ્રામ/કિલો) નો પટ આપવો.",
                biologicalIpmEn = "Solar heat seed treatment: Soak seeds in water for 4-5 hours on sunny May day, then spread thin on tarpaulin under hot sun.",
                biologicalIpmHi = "मई के महीने में धूप में बीजों को पानी में भिगोकर तिरपाल पर सुखाएं (सौर उपचार)।",
                biologicalIpmGu = "મે મહિનામાં બીજને 4 કલાક પાણીમાં પલાળી કડકડતી તડકામાં તાડપત્રી પર સૂકવી સૂર્યતાપ ઉપચાર કરવો.",
                culturalPracticesEn = "Never collect seeds from infected crop fields for the next season's sowing.",
                culturalPracticesHi = "रोगग्रस्त खेत से अगले साल के लिए बीज कभी न रखें।",
                culturalPracticesGu = "અંગારિયાવાળા ખેતરમાંથી આવતી સાલ માટે ક્યારેય બિયારણ રાખવું નહીં."
            ),

            // 8. GROUNDNUT - TIKKA LEAF SPOT
            PreventionTipEntity(
                cropId = "groundnut",
                cropNameEn = "Groundnut",
                cropNameHi = "मूंगफली",
                cropNameGu = "મગફળી",
                diseaseId = "groundnut_tikka",
                diseaseNameEn = "Tikka Leaf Spot (Cercospora)",
                diseaseNameHi = "मूंगफली का टिक्का रोग (पर्ण धब्बा)",
                diseaseNameGu = "મગફળીનો ટીક્કો અને ગેરુ રોગ",
                category = "FUNGAL",
                symptomsSummaryEn = "Circular dark brown spots surrounded by bright yellow halo on older leaves, leading to premature leaf drop.",
                symptomsSummaryHi = "पत्तियों पर पीले घेरे वाले गहरे भूरे-काले गोल धब्बे और पत्तों का असमय झड़ना।",
                symptomsSummaryGu = "પાન પર ગોળ કાળા-કથ્થાઈ ટપકાં અને ફરતે પીળી કિનારી. પાન ખરી પડે છે અને દાણા બેસતા નથી.",
                preventionEn = "Treat kernels with Trichoderma harzianum @ 10g/kg seed or Mancozeb @ 3g/kg before sowing.",
                preventionHi = "बुवाई से पहले बीज को ट्राइकोडर्मा (10 ग्राम/किग्रा) या मैंकोजेब से उपचारित करें।",
                preventionGu = "વાવણી પહેલા દાણાને ટ્રાઈકોડર્મા (10 ગ્રામ/કિલો) અથવા મેન્કોઝેબનો પટ આપવો.",
                biologicalIpmEn = "Spray 5% Neem leaf extract (NSKE) or Cow urine (10%) with sour buttermilk at first appearance of spots (35-40 DAS).",
                biologicalIpmHi = "लक्षण दिखते ही 5% नीम अर्क या 10% गोमूत्र व खट्टी छाछ का घोल छिड़कें।",
                biologicalIpmGu = "રોગ દેખાય ત્યારે 5% લીમડાના અર્ક અથવા ખાટી છાશ + ગૌમૂત્રનું દ્રાવણ 35-40 દિવસે છાંટવું.",
                culturalPracticesEn = "Follow crop rotation with pearl millet (bajra), maize, or sorghum. Destroy infected crop debris after harvest.",
                culturalPracticesHi = "बाजरा, मक्का या ज्वार के साथ फसल चक्र अपनाएं। फसल कटाई बाद अवशेष जलाएं या नष्ट करें।",
                culturalPracticesGu = "બાજરી, જુવાર કે મકાઈ સાથે પાકની ફેરબદલી કરવી. કાપણી પછી કચરો ખેતરમાંથી દૂર કરવો."
            ),

            // 9. GROUNDNUT - COLLAR ROT & WHITE GRUB
            PreventionTipEntity(
                cropId = "groundnut",
                cropNameEn = "Groundnut",
                cropNameHi = "मूंगफली",
                cropNameGu = "મગફળી",
                diseaseId = "groundnut_collar_rot",
                diseaseNameEn = "Collar Rot & White Grub Pest",
                diseaseNameHi = "मूंगफली का तना गलन व सफेद लट",
                diseaseNameGu = "મગફળીનો થડનો કોહવારો અને ધૂણેટી (સફેદ ડોળ)",
                category = "FUNGAL",
                symptomsSummaryEn = "Seedling collar at ground level turns black/water-soaked with white fungal mycelium; plants wilt in patches.",
                symptomsSummaryHi = "जमीन की सतह पर तना काला पड़कर सड़ जाता है और पौधे सूख जाते हैं; सफेद लट जड़ें काट देती है।",
                symptomsSummaryGu = "જમીન પાસે થડ કાળું પડી સડી જાય છે અને છોડ સુકાઈ જાય છે. ધૂણેટી મૂળ કાપી નાખે છે.",
                preventionEn = "Apply well-decomposed FYM enriched with Trichoderma harzianum @ 2.5 kg mixed with 250 kg manure per acre.",
                preventionHi = "गोबर की खाद में ट्राइकोडर्मा (2.5 किग्रा/एकड़) मिलाकर खेत तैयार करते समय डालें।",
                preventionGu = "એકર દીઠ 2.5 કિલો ટ્રાઈકોડર્મા 250 કિલો સારા દેશી ખાતરમાં ભેળવી વાવણી વખતે ચાસમાં આપવું.",
                biologicalIpmEn = "For white grubs, apply Metarhizium anisopliae or Beauveria bassiana @ 2 kg/acre with irrigation or organic manure.",
                biologicalIpmHi = "सफेद लट हेतु मेटाराइजियम या ब्यूवेरिया (2 किग्रा/एकड़) सिंचाई के साथ डालें।",
                biologicalIpmGu = "ધૂણેટીના નિયંત્રણ માટે મેટારાઈઝીયમ એનિસોપ્લી 2 કિલો પ્રતિ એકર પિયતના પાણી સાથે કે ખાતરમાં આપવું.",
                culturalPracticesEn = "Deep summer ploughing exposes pupae and grubs to predatory birds and intense sun heat.",
                culturalPracticesHi = "मई-जून में गहरी जुताई करें ताकि धूप और पक्षी कीड़ों के प्यूपा को नष्ट कर सकें।",
                culturalPracticesGu = "ઉનાળામાં ઊંડી ખેડ કરવી જેથી જમીનમાં રહેલા કોશેટા અને ઈંડા તડકાથી તથા પક્ષીઓ દ્વારા નાશ પામે."
            ),

            // 10. RICE - BLAST & BACTERIAL LEAF BLIGHT
            PreventionTipEntity(
                cropId = "rice",
                cropNameEn = "Rice / Paddy",
                cropNameHi = "धान / चावल",
                cropNameGu = "ડાંગર",
                diseaseId = "rice_blast",
                diseaseNameEn = "Rice Blast (Pyricularia oryzae)",
                diseaseNameHi = "धान का झोंका रोग (ब्लास्ट)",
                diseaseNameGu = "ડાંગરનો કરમોડી (બ્લાસ્ટ) રોગ",
                category = "FUNGAL",
                symptomsSummaryEn = "Diamond or eye-shaped spindle lesions on leaf blades with gray center; neck blast turns earhead base dark.",
                symptomsSummaryHi = "पत्तियों पर आंख या नाव के आकार के धब्बे जिनका केंद्र धूसर होता है; बाली की गर्दन काली पड़ जाती है।",
                symptomsSummaryGu = "પાન પર ત્રાક આકારના ડાઘ જેની વચ્ચે રાખોડી રંગ હોય છે. કંકીની ડોક કાળી પડી ભાંગી પડે છે.",
                preventionEn = "Split nitrogen applications into 3-4 smaller doses rather than heavy bulk urea doses. Maintain uniform water level.",
                preventionHi = "यूरिया को 3-4 किस्तों में कम मात्रा में दें। खेत में पानी का स्तर संतुलित रखें।",
                preventionGu = "યુરિયા ખાતર એકસાથે આપવાને બદલે 3-4 હપ્તામાં વહેંચીને આપવું. ખેતરમાં પાણીનું સમાન સ્તર જાળવવું.",
                biologicalIpmEn = "Foliar spray with Pseudomonas fluorescens @ 10g/L or Trichoderma viride 5g/L at tillering and panicle emergence.",
                biologicalIpmHi = "स्यूडोमोनास फ्लोरोसेंस (10 ग्राम/ली.) का कल्ले फूटते समय व बाली निकलते समय छिड़काव करें।",
                biologicalIpmGu = "સ્યુડોમોનાસ ફ્લોરોસન્સ 10 ગ્રામ/લિટર ફૂટ અને કંકી નીકળતી વખતે છાંટવું.",
                culturalPracticesEn = "Temporarily drain standing water from field for 48 hours during severe vegetative blast to aerate roots.",
                culturalPracticesHi = "उग्र प्रकोप के समय 2 दिन के लिए खेत का पानी निकाल दें ताकि जड़ों को हवा मिल सके।",
                culturalPracticesGu = "રોગનો વધુ ઉપદ્રવ જણાય ત્યારે 2 દિવસ માટે ખેતરમાંથી પાણી નિતારી લેવું જેથી મૂળિયાને હવા મળે."
            ),

            // 11. CHILLI - THRIPS & DIEBACK
            PreventionTipEntity(
                cropId = "chilli",
                cropNameEn = "Chilli",
                cropNameHi = "मिर्च",
                cropNameGu = "મરચી",
                diseaseId = "chilli_thrips",
                diseaseNameEn = "Thrips, Mites & Dieback Complex",
                diseaseNameHi = "मिर्च का मरोड़िया (मुरडा) व फल सड़न",
                diseaseNameGu = "મરચીનો કોકડવા (મુરડા) અને ડાળીનો સુકારો",
                category = "PEST",
                symptomsSummaryEn = "Upward boat-shaped curl from thrips, downward curl from mites, and brown sunken fruit rot with black dots.",
                symptomsSummaryHi = "पत्तियां ऊपर की ओर नाव की तरह मुड़ती हैं (थ्रिप्स) और फलों पर काले धब्बे पड़ते हैं।",
                symptomsSummaryGu = "પાન ઉપર તરફ હોડી જેવા વળી જાય છે (થ્રિપ્સ) અને લાલ-લીલા મરચાં પર કાળા ડાઘ પડી ફળ સડી જાય છે.",
                preventionEn = "Plant border barriers of 2-3 rows of maize or sorghum around chilli plots to filter incoming winds and pests.",
                preventionHi = "खेत के चारों तरफ मक्का या ज्वार की 2-3 कतारें बॉर्डर क्रॉप के रूप में लगाएं।",
                preventionGu = "મરચીના ખેતર ફરતે મકાઈ કે જુવારની 2-3 હાર વાવવી (બોર્ડર ક્રોપ) જેથી પવન સાથે આવતી જીવાત અટકે.",
                biologicalIpmEn = "Install blue sticky traps (10/acre) for thrips and yellow traps for whiteflies. Spray 5% NSKE regularly.",
                biologicalIpmHi = "नीले स्टिकी ट्रैप (10/एकड़) लगाएं। 5% नीम तेल (NSKE) का नियमित छिड़काव करें।",
                biologicalIpmGu = "એકર દીઠ 10 વાદળી સ્ટીકી ટ્રેપ લગાવો. લીંબોળીનું તેલ 30 મિલી પ્રતિ પંપ દર 10-12 દિવસે છાંટવું.",
                culturalPracticesEn = "Dip seedlings in Trichoderma viride slurry (20g/L) for 20 minutes before transplanting.",
                culturalPracticesHi = "रोपाई से पहले पौध की जड़ों को ट्राइकोडर्मा के घोल (20 ग्राम/ली.) में 20 मिनट डुबोएं।",
                culturalPracticesGu = "ધરૂ રોપતા પહેલા તેના મૂળિયાને ટ્રાઈકોડર્માના દ્રાવણ (20 ગ્રામ/લિટર) માં 20 મિનિટ બોળીને રોપવા."
            ),

            // 12. POTATO - LATE BLIGHT
            PreventionTipEntity(
                cropId = "potato",
                cropNameEn = "Potato",
                cropNameHi = "आलू",
                cropNameGu = "બટાકા",
                diseaseId = "potato_late_blight",
                diseaseNameEn = "Late Blight of Potato (Phytophthora)",
                diseaseNameHi = "आलू का पछेती झुलसा",
                diseaseNameGu = "બટાકાનો પાછોતરો સુકારો (ઝુલસા)",
                category = "FUNGAL",
                symptomsSummaryEn = "Irregular water-soaked lesions that turn purplish-black rapidly during humid weather with white rim mold under leaves.",
                symptomsSummaryHi = "पत्तियों पर गीले काले धब्बे, नीचे सफेद रुई जैसी फफूंद और 3-4 दिन में पूरा पौधा काला पड़ना।",
                symptomsSummaryGu = "પાન પર પાણીપોચા કાળા ડાઘ, પાન નીચે સફેદ ફૂગ અને ભેજવાળા વાતાવરણમાં 3-4 દિવસમાં આખો છોડ કાળો થઈ સુકાવો.",
                preventionEn = "Use certified healthy seed tubers. Earth up soil well around ridges so exposed tubers are insulated from spore wash.",
                preventionHi = "प्रमाणित रोगमुक्त आलू बीज लगाएं। पौधों पर अच्छी मिट्टी चढ़ाएं ताकि कंदों तक फंगस न पहुंचे।",
                preventionGu = "પ્રમાણિત રોગમુક્ત બિયારણ વાપરવું. બટાકા પર સારી માટી ચડાવવી જેથી ઉઘાડા બટાકા પર ફૂગના કણો ન પડે.",
                biologicalIpmEn = "Preventive spray with Mancozeb 75% WP @ 35g/15L before fog and dew. If disease appears, spray Cymoxanil + Mancozeb.",
                biologicalIpmHi = "कोहरा शुरू होने से पहले मैंकोजेब (35 ग्राम/15 ली.) का सुरक्षात्मक छिड़काव करें।",
                biologicalIpmGu = "ઝાકળ કે ધુમ્મસ શરૂ થતા પહેલા મેન્કોઝેબ 35 ગ્રામ/પંપ છાંટવું. રોગ દેખાય તો સાયમોક્ઝાનિલ + મેન્કોઝેબ છાંટવું.",
                culturalPracticesEn = "Dehaulming (cut foliage 10-12 days before harvesting) prevents spore transfer to tubers during digging.",
                culturalPracticesHi = "खोदाई से 10-12 दिन पहले बेल काट दें (डिहॉल्मिंग) ताकि कंद सुरक्षित रहें।",
                culturalPracticesGu = "ખોદકામના 10-12 દિવસ પહેલાં વેલા કાપી નાખવા જેથી બટાકાની છાલ પાકી થાય અને ફૂગ ન લાગે."
            ),

            // 13. MAIZE - FALL ARMYWORM (FAW)
            PreventionTipEntity(
                cropId = "maize",
                cropNameEn = "Maize / Corn",
                cropNameHi = "मक्का",
                cropNameGu = "મકાઈ",
                diseaseId = "maize_faw",
                diseaseNameEn = "Fall Armyworm (Spodoptera frugiperda)",
                diseaseNameHi = "मक्का का फॉल आर्मीवॉर्म (सैनिक कीट)",
                diseaseNameGu = "મકાઈમાં લશ્કરી ઈયળ (ફોલ આર્મીવોર્મ)",
                category = "PEST",
                symptomsSummaryEn = "Ragged elongated shot holes on leaves and heavy sawdust-like fecal frass lodged deep inside whorl.",
                symptomsSummaryHi = "पत्तियों पर बड़े अनियमित छेद और पोंगे (भोंपू) के अंदर लकड़ी के बुरादे जैसा मल।",
                symptomsSummaryGu = "પાન પર કાણાં-કાણાં અને મકાઈની ભૂંગળી (પોંગા) માં લાકડાના વહેર જેવી કાળી હગાર અને અંદર સંતાયેલી ઈયળ.",
                preventionEn = "Install FAW Pheromone traps @ 4 per acre within 10 days of sowing to detect adult moths.",
                preventionHi = "बुवाई के 10 दिन के अंदर 4 फेरोमोन ट्रैप प्रति एकड़ लगाएं।",
                preventionGu = "વાવણીના 10 દિવસ બાદ એકર દીઠ 4 ફેરોમોન ટ્રેપ લગાવી ફુદાંનું નિરીક્ષણ કરવું.",
                biologicalIpmEn = "Apply dry sand or wood ash (mixed with lime 9:1) into central whorls of plants at 15-20 days stage.",
                biologicalIpmHi = "15-20 दिन की फसल के पोंगे में सूखी रेत या लकड़ी की राख डालें। नीम तेल (10,000 ppm) छिड़कें।",
                biologicalIpmGu = "છોડ 15-20 દિવસનો થાય ત્યારે ભૂંગળીમાં સૂકી રેતી અથવા રાખ નાખવી. લીંબોળીનું તેલ છાંટવું.",
                culturalPracticesEn = "Intercrop with pulses like cowpea or pigeon pea to disrupt pest egg-laying.",
                culturalPracticesHi = "मक्का के साथ अरहर या लोबिया की अंतःवर्तीय खेती करें।",
                culturalPracticesGu = "મકાઈ સાથે ચોળી કે તુવેરનું આંતરપાક વાવેતર કરવું જેથી ઈયળોનો ઉપદ્રવ ઘટે."
            ),

            // 14. SOYBEAN - YELLOW MOSAIC & POD BORER
            PreventionTipEntity(
                cropId = "soybean",
                cropNameEn = "Soybean",
                cropNameHi = "सोयाबीन",
                cropNameGu = "સોયાબીન",
                diseaseId = "soybean_mosaic",
                diseaseNameEn = "Yellow Mosaic Virus & Pod Borer",
                diseaseNameHi = "सोयाबीन का पीला मोजेक वायरस व फली छेदक",
                diseaseNameGu = "સોયાબીનમાં પીળો પચરંગીયો (મોઝેક) અને ઈયળ",
                category = "VIRAL",
                symptomsSummaryEn = "Irregular bright yellow and green patches on leaves; infected plants produce shriveled, unfilled pods.",
                symptomsSummaryHi = "पत्तियों पर चमकीले पीले और हरे धब्बे; पौधे में फलियां नहीं बनतीं या दाने छोटे रह जाते हैं।",
                symptomsSummaryGu = "પાન પર ઘાટા પીળા અને લીલા રંગના ધાબા; છોડમાં શિંગો ઓછી બેસે છે અને દાણા ચીમળાઈ જાય છે.",
                preventionEn = "Sow certified YMV-tolerant varieties like JS-20-29, JS-97-52, or NRC-86. Treat seed with Imidacloprid.",
                preventionHi = "रोगरोधी किस्मों (JS-20-29, JS-97-52) की बुवाई करें। बीज उपचार अवश्य करें।",
                preventionGu = "પીળા પચરંગીયા સામે પ્રતિકારક જાતો (જેએસ-20-29, જેએસ-97-52) વાવવી.",
                biologicalIpmEn = "Install yellow sticky traps (10/acre) to control Whitefly vectors. Spray 5% NSKE at 25-30 DAS.",
                biologicalIpmHi = "सफेद मक्खी की रोकथाम हेतु पीले कार्ड लगाएं और 5% नीम तेल का छिड़काव करें।",
                biologicalIpmGu = "સફેદ માખી માટે 10-15 પીળા સ્ટીકી ટ્રેપ લગાવો અને લીંબોળીનું તેલ 30 મિલી પ્રતિ પંપ છાંટવું.",
                culturalPracticesEn = "Immediately pull out and bury any early YMV-infected yellow plants seen in the first 30 days.",
                culturalPracticesHi = "शुरुआती 30 दिनों में जो पौधे पीले दिखें उन्हें तुरंत उखाड़कर जमीन में गाड़ दें।",
                culturalPracticesGu = "શરૂઆતના 30 દિવસમાં ખેતરમાં જે છોડ પીળા દેખાય તેને તરત જ ઉપાડી જમીનમાં દાટી દેવા."
            ),

            // 15. ONION - PURPLE BLOTCH & THRIPS
            PreventionTipEntity(
                cropId = "onion",
                cropNameEn = "Onion",
                cropNameHi = "प्याज",
                cropNameGu = "ડુંગળી",
                diseaseId = "onion_purple_blotch",
                diseaseNameEn = "Purple Blotch & Onion Thrips",
                diseaseNameHi = "प्याज का बैंगनी धब्बा रोग (पर्पल ब्लॉच) व थ्रिप्स",
                diseaseNameGu = "ડુંગળીનો જાંબલી ધાબા રોગ (પર્પલ બ્લોચ) અને થ્રિપ્સ",
                category = "FUNGAL",
                symptomsSummaryEn = "Small sunken whitish spots turning into dark purplish oval blotches with yellow halo on onion foliage.",
                symptomsSummaryHi = "पत्तियों पर पहले सफेद धब्बे, जो बाद में बैंगनी-भूरे रंग के बड़े घावों में बदल जाते हैं।",
                symptomsSummaryGu = "પાન પર સફેદ ટપકાં થાય છે જે ધીમે ધીમે મોટા થઈ જાંબલી-કથ્થાઈ રંગના લંબગોળ ચાઠામાં ફેરવાય છે.",
                preventionEn = "Raised bed planting ensures proper water drainage during winter dews and monsoon showers. Avoid overhead flooding.",
                preventionHi = "उठी हुई क्यारियों (रेज्ड बेड) पर रोपाई करें ताकि जलभराव न हो।",
                preventionGu = "ગાદી ક્યારા (રેઇઝ્ડ બેડ) બનાવી રોપણી કરવી જેથી પાણી ભરાઈ ન રહે અને હવા ઉજાસ રહે.",
                biologicalIpmEn = "Install blue sticky traps for thrips. Spray Trichoderma viride @ 5g/L + Mancozeb @ 30g/15L with sticker.",
                biologicalIpmHi = "थ्रिप्स के लिए नीले स्टिकी कार्ड लगाएं। मैंकोजेब (30 ग्राम/15 ली.) में स्टीकर मिलाकर छिड़कें।",
                biologicalIpmGu = "થ્રિપ્સ માટે વાદળી સ્ટીકી ટ્રેપ લગાવો. મેન્કોઝેબ 35 ગ્રામ સાથે સ્ટીકર (ડિટર્જન્ટ/ગુંદર) મેળવી છાંટવું.",
                culturalPracticesEn = "Always mix wetting agent / spreader sticker (Agri-80 or soap 5ml) when spraying waxy onion leaves.",
                culturalPracticesHi = "प्याज की चिकनी पत्तियों पर दवा चिपकाने के लिए हमेशा स्टिकर या चिपकू घोल में मिलाएं।",
                culturalPracticesGu = "ડુંગળીના પાન ચીકણા હોવાથી દવાની સાથે હંમેશા સ્ટીકર મેળવીને જ છંટકાવ કરવો જેથી દવા પાન પર ચોંટે."
            )
        )
    }

    /**
     * Pre-populated authentic agronomic advice cached for offline consultation by farmers.
     */
    fun generateDefaultCachedAdvice(): List<CachedAgriculturalAdviceEntity> {
        return listOf(
            CachedAgriculturalAdviceEntity(
                cropId = "cotton",
                cropName = "Cotton / કપાસ / कपास",
                diseaseOrPestId = "cotton_aphid",
                topic = "Integrated Sucking Pest Management (મોલો-મશી અને થ્રિપ્સ નિયંત્રણ)",
                adviceCategory = "BIOLOGICAL",
                titleEn = "Eco-friendly Control of Sucking Pests in Cotton",
                titleHi = "कपास में रस चूसक कीटों का जैविक प्रबंधन",
                titleGu = "કપાસમાં રસ ચૂસતી જીવાતોનું જૈવિક અને વૈજ્ઞાનિક નિયંત્રણ",
                contentEn = "Install 15 yellow sticky traps and 5 blue sticky traps per acre at crop canopy level. Spray 5% Neem Seed Kernel Extract (NSKE) or Neem Oil (10,000 ppm) @ 30ml/15L pump at initial nymph emergence to break the reproduction cycle without harming ladybird beetles.",
                contentHi = "प्रति एकड़ 15 पीले और 5 नीले चिपचिपे कार्ड लगाएं। नीम तेल (10,000 ppm) 30 मिली प्रति 15 लीटर पंप का छिड़काव करें ताकि मित्र कीट (लेडीबर्ड बीटल) सुरक्षित रहें।",
                contentGu = "એકર દીઠ 15 પીળા અને 5 વાદળી ચીકણા ટ્રેપ છોડની સપાટીથી 1 ફૂટ ઊંચે લગાવો. ઉપદ્રવની શરૂઆતમાં 5% લીંબોળીના મીંજનું અર્ક અથવા લીમડાનું તેલ 30 મિલી/પંપ છાંટવું જેથી ઢાલિયા જેવા મિત્ર કીટકો બચી રહે.",
                seasonalStage = "VEGETATIVE",
                expertSource = "ICAR - Central Institute for Cotton Research (CICR)"
            ),
            CachedAgriculturalAdviceEntity(
                cropId = "cotton",
                cropName = "Cotton / કપાસ / कपास",
                diseaseOrPestId = "cotton_bollworm",
                topic = "Pink Bollworm Monitoring and Trapping (ગુલાબી ઈયળ વ્યવસ્થાપન)",
                adviceCategory = "PREVENTION",
                titleEn = "Pheromone Trapping & ETL Guidelines for Pink Bollworm",
                titleHi = "गुलाबी सुंडी (पिंक बोलवर्म) की निगरानी एवं फेरोमोन ट्रैप",
                titleGu = "ગુલાબી ઈયળની ફેરોમોન ટ્રેપ દ્વારા દેખરેખ અને સમયસર પગલાં",
                contentEn = "Install 5 Pheromone Traps (Gossyplure) per acre from 45 days after sowing. If 8 or more moths are trapped continuously for 3 consecutive nights, trigger bio-pesticide spray (Beauveria bassiana @ 40g/pump or Bt formulation).",
                contentHi = "बुवाई के 45 दिन बाद प्रति एकड़ 5 फेरोमोन ट्रैप लगाएं। यदि लगातार 3 दिनों तक प्रति ट्रैप 8 या अधिक पतंगे पकड़े जाएं, तो तुरंत ब्यूवेरिया बैसियाना या बीटी कीटनाशक का छिड़काव करें।",
                contentGu = "વાવણીના 45 દિવસ પછી એકર દીઠ 5 ફેરોમોન ટ્રેપ (ગોસીપ્લુર લ્યોર) ગોઠવો. જો સળંગ 3 દિવસ સુધી રોજ ટ્રેપ દીઠ 8 કે વધુ ફૂદાં પકડાય તો બ્યુવેરિયા બાસિયાના (40 ગ્રામ/પંપ) અથવા પ્રોફેનોફોસનો છંટકાવ કરવો.",
                seasonalStage = "FLOWERING",
                expertSource = "Junagadh Agricultural University (JAU)"
            ),
            CachedAgriculturalAdviceEntity(
                cropId = "tomato",
                cropName = "Tomato / ટામેટા / टमाटर",
                diseaseOrPestId = "tomato_early_blight",
                topic = "Early & Late Blight Weather-Smart Fungicide Timing (સુકારો રોગ અટકાવ)",
                adviceCategory = "CHEMICAL",
                titleEn = "Target Spot and Blight Management During Cloudy Weather",
                titleHi = "टमाटर में झुलसा (ब्लाइट) रोग का मौसम आधारित नियंत्रण",
                titleGu = "ટામેટામાં આગોતરા અને પાછોતરા સુકારા સામે સંરક્ષણ",
                contentEn = "Cloudy and damp weather triggers Alternaria blight. Apply protective Mancozeb 75% WP @ 30g/15L pump before rain. If lesions appear, switch to systemic Azoxystrobin 18.2% + Difenoconazole 11.4% SC @ 15ml/15L pump.",
                contentHi = "बादल छाए रहने और नमी के मौसम में मैन्कोजेब 75% WP (30 ग्राम/15 ली.) का सुरक्षात्मक छिड़काव करें। यदि लक्षण दिखाई दें तो एजोक्सीस्ट्रोबिन + डिफेनोकोनाजोल (15 मिली/15 ली.) छिड़कें।",
                contentGu = "વાદળછાયા અને ભેજવાળા વાતાવરણમાં મેન્કોઝેબ 75% WP (30-35 ગ્રામ/પંપ) નો આગોતરો છંટકાવ કરો. પાન પર ટપકાં દેખાય તો એઝોક્સીસ્ટ્રોબિન + ડાયફેનોકોનાઝોલ (15 મિલી/પંપ) છાંટવું.",
                seasonalStage = "VEGETATIVE",
                expertSource = "Indian Institute of Horticultural Research (IIHR)"
            ),
            CachedAgriculturalAdviceEntity(
                cropId = "groundnut",
                cropName = "Groundnut / મગફળી / मूंगफली",
                diseaseOrPestId = "groundnut_tikka",
                topic = "Tikka Leaf Spot and Rust Resistance (ટિક્કા અને ગેરુ રોગ વ્યવસ્થાપન)",
                adviceCategory = "PREVENTION",
                titleEn = "Preventing Severe Defoliation from Groundnut Tikka",
                titleHi = "मूंगफली में टिक्का (पर्ण चित્ती) रोग का रोकथाम",
                titleGu = "મગફળીમાં પાનના ટપકાં (ટિક્કા) અને ગેરુ રોગનું નિયંત્રણ",
                contentEn = "Tikka causes premature leaf drop leading to poor pod filling. Spray Carbendazim 12% + Mancozeb 63% WP (Saaf) @ 30g/15L pump at first sign of circular dark spots with yellow chlorotic halos, repeated at 14 days interval.",
                contentHi = "टिक्का रोग से पत्तियां जल्दी झड़ जाती हैं जिससे फलियों में दाने नहीं भरते। साफ (कार्बेन्डाजिम + मैंकोजेब) 30 ग्राम प्रति 15 लीटर पंप का छिड़काव 14 दिन के अंतराल पर करें।",
                contentGu = "ટિક્કા રોગથી પાન ખરી પડે છે અને દાણા પુરા ભરાતા નથી. પાન પર પીળી કિનારીવાળા કાળા ટપકાં દેખાય કે તરત કાર્બેન્ડાઝીમ + મેન્કોઝેબ (સાફ) 30 ગ્રામ પ્રતિ પંપ છાંટવું.",
                seasonalStage = "VEGETATIVE",
                expertSource = "Directorate of Groundnut Research (ICAR-DGR)"
            ),
            CachedAgriculturalAdviceEntity(
                cropId = "wheat",
                cropName = "Wheat / ઘઉં / गेहूं",
                diseaseOrPestId = "wheat_rust",
                topic = "Yellow & Brown Rust Surveillance (પીળો અને કથ્થાઈ ગેરુ રોગ)",
                adviceCategory = "PREVENTION",
                titleEn = "Critical Surveillance for Stripe (Yellow) Rust in Northern & Western Plains",
                titleHi = "गेहूं में पीला रतुआ (येलो रस्ट) की समय पर पहचान और रोकथाम",
                titleGu = "ઘઉંમાં પીળા ગેરુ રોગની સમયસર ઓળખ અને આગોતરા પગલાં",
                contentEn = "Yellow powder lines on wheat leaves under cool temperatures (10-20°C) indicate Stripe Rust. Spray Propiconazole 25% EC @ 15ml per 15L water at the very first stripe observation across the field border.",
                contentHi = "ठंडे मौसम में पत्तियों पर पीली पाउडर जैसी धारियां दिखने पर तुरंत प्रोपिकोनाजोल 25% EC (15 मिली प्रति 15 लीटर) का छिड़काव करें।",
                contentGu = "ઠંડી ઋતુમાં પાન પર પીળી હળદર જેવી પાવડરની પટ્ટીઓ દેખાય તો તરત પ્રોપીકોનાઝોલ 25% EC (15 મિલી/15 લિટર પંપ) નો છંટકાવ કરવો.",
                seasonalStage = "VEGETATIVE",
                expertSource = "Indian Institute of Wheat & Barley Research (IIWBR)"
            ),
            CachedAgriculturalAdviceEntity(
                cropId = "general",
                cropName = "General / સર્વસામાન્ય / सामान्य",
                diseaseOrPestId = null,
                topic = "Water Quality & Pesticide Spraying Techniques (દવા છાંટવાની યોગ્ય પદ્ધતિ)",
                adviceCategory = "CULTURAL",
                titleEn = "Optimizing Water pH and Nozzle Selection for Spray Efficiency",
                titleHi = "कीटनाशक छिड़काव के लिए पानी की गुणवत्ता एवं नोजल का चयन",
                titleGu = "કીટનાશક છંટકાવ વખતે પાણીની ગુણવત્તા અને યોગ્ય નોઝલનો ઉપયોગ",
                contentEn = "Always use clear water with pH between 6.0 and 7.0 for preparing spray solutions. Hard or muddy water deactivates chemical molecules. Use hollow cone nozzles for insecticides and flat fan nozzles for herbicides. Never spray during peak afternoon heat (12 PM - 3 PM).",
                contentHi = "छिड़काव के लिए हमेशा साफ पानी का उपयोग करें जिसका pH 6 से 7 हो। दोपहर की तेज धूप (12 से 3 बजे) में कभी भी दवा न छिड़कें। कीटनाशकों के लिए हॉलो कोन नोजल का उपयोग करें।",
                contentGu = "દવા બનાવવા માટે ક્યારેય ડોહળું કે ખારું પાણી ન વાપરવું, હંમેશા ચોખ્ખું પાણી વાપરવું. બપોરે 12 થી 3 વાગ્યાના આકરા તડકામાં છંટકાવ ન કરવો. સવારે અથવા સાંજે પવનની દિશામાં છંટકાવ કરવો.",
                seasonalStage = null,
                expertSource = "Central Insecticides Board & Registration Committee (CIBRC)"
            )
        )
    }
}

