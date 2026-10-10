package com.example.engine

import com.example.model.*

object LocalChatEngine {

    fun generateReply(
        question: String,
        diagnosis: DiagnosisResult?,
        language: Language
    ): String {
        val q = question.lowercase().trim()

        if (diagnosis == null) {
            return when (language) {
                Language.GUJARATI -> "કૃપા કરીને પહેલા તમારા પાકનો ફોટો પાડો અથવા પાક પસંદ કરો જેથી હું યોગ્ય સલાહ આપી શકું."
                Language.HINDI -> "कृपया पहले अपनी फसल की फोटो खींचें ताकि मैं सही सलाह दे सकूं।"
                Language.ENGLISH -> "Please take a photo of your crop or select a diagnosis first so I can assist you accurately."
            }
        }

        val cropName = diagnosis.crop.getLocalizedName(language)
        val problemName = diagnosis.problemName
        val pesticide = diagnosis.verifiedPesticide

        // Check intents
        val isPesticideQuery = q.contains("pesticide") || q.contains("spray") || q.contains("medicine") ||
                q.contains("દવા") || q.contains("છાંટ") || q.contains("કેમિકલ") ||
                q.contains("दवा") || q.contains("छिड़काव") || q.contains("कीटनाशक")

        val isWhenToSprayQuery = q.contains("when") || q.contains("time") || q.contains("morning") ||
                q.contains("ક્યારે") || q.contains("સમય") || q.contains("સાંજ") ||
                q.contains("कब") || q.contains("समय")

        val isPreventionQuery = q.contains("prevent") || q.contains("stop") || q.contains("avoid") ||
                q.contains("અટકાવ") || q.contains("બચાવ") || q.contains("ઉપાય") ||
                q.contains("रोकथाम") || q.contains("बचाव")

        val isNeemQuery = q.contains("neem") || q.contains("organic") || q.contains("desi") ||
                q.contains("લીમડા") || q.contains("લીંબોળી") || q.contains("દેશી") ||
                q.contains("नीम") || q.contains("जैविक")

        val isDangerQuery = q.contains("danger") || q.contains("loss") || q.contains("damage") ||
                q.contains("નુકસાન") || q.contains("જોખમ") || q.contains("ખતરનાક") ||
                q.contains("नुकसान") || q.contains("खतरा") || q.contains("गंभीर")

        return when {
            isPesticideQuery -> {
                if (pesticide != null) {
                    when (language) {
                        Language.GUJARATI -> "💊 $cropName માં $problemName માટે ભલામણ કરેલ દવા:\n" +
                                "• મુખ્ય ઘટક: ${pesticide.activeIngredient}\n" +
                                "• છંટકાવ પ્રમાણ: ${pesticide.getLocalizedApplicationRate(language)}\n" +
                                "• કાપણી પહેલાં ગાળો (PHI): ${pesticide.preHarvestIntervalDays}\n" +
                                "⚠️ સાવચેતી: રબરના મોજા અને માસ્ક પહેરીને જ છંટકાવ કરવો."
                        Language.HINDI -> "💊 $cropName में $problemName के लिए अनुशंसित दवा:\n" +
                                "• सक्रिय घटक: ${pesticide.activeIngredient}\n" +
                                "• छिड़काव मात्रा: ${pesticide.getLocalizedApplicationRate(language)}\n" +
                                "• तुड़ाई से पहले अंतराल (PHI): ${pesticide.preHarvestIntervalDays}\n" +
                                "⚠️ सावधानी: रबर के दस्ताने व मास्क पहनकर ही छिड़काव करें।"
                        Language.ENGLISH -> "💊 Verified chemical control for $problemName in $cropName:\n" +
                                "• Active Ingredient: ${pesticide.activeIngredient}\n" +
                                "• Application Rate: ${pesticide.getLocalizedApplicationRate(language)}\n" +
                                "• Pre-Harvest Interval (PHI): ${pesticide.preHarvestIntervalDays}\n" +
                                "⚠️ Safety: Always wear protective rubber gloves and a face mask."
                    }
                } else {
                    when (language) {
                        Language.GUJARATI -> "$cropName ના $problemName માટે કેમિકલ દવાની જરૂર નથી. જૈવિક કે દેશી ઉપાયોથી નિયંત્રણ કરો."
                        Language.HINDI -> "$cropName के $problemName के लिए रासायनिक दवा की आवश्यकता नहीं है। जैविक उपायों से नियंत्रित करें।"
                        Language.ENGLISH -> "Chemical pesticide is not required for $problemName. Cultural and biological methods are sufficient."
                    }
                }
            }

            isWhenToSprayQuery -> {
                when (language) {
                    Language.GUJARATI -> "🕒 છંટકાવનો શ્રેષ્ઠ સમય:\n" +
                            "• સવારે ૭ થી ૧૦ વાગ્યા દરમિયાન (ઝાકળ સુકાયા પછી)\n" +
                            "• અથવા સાંજે ૪ થી ૬ વાગ્યા પછી\n" +
                            "• બપોરે આકરા તડકામાં કે પવન ચાલતો હોય ત્યારે ક્યારેય દવા ન છાંટવી."
                    Language.HINDI -> "🕒 छिड़काव का सबसे सही समय:\n" +
                            "• सुबह 7 से 10 बजे के बीच (ओस सूखने के बाद)\n" +
                            "• या शाम 4 से 6 बजे के बीच\n" +
                            "• तेज धूप या हवा में कभी भी छिड़काव न करें।"
                    Language.ENGLISH -> "🕒 Best spraying schedule:\n" +
                            "• Morning (7 AM - 10 AM) after dew has dried, or late afternoon (4 PM - 6 PM).\n" +
                            "• Never spray during peak midday sun or strong windy conditions."
                }
            }

            isNeemQuery -> {
                when (language) {
                    Language.GUJARATI -> "🌿 લીંબોળીના તેલ અંગે:\n" +
                            "હા! લીંબોળીનું તેલ (10,000 ppm) 30 મિલી પ્રતિ 15 લિટર પંપમાં ભેળવી છાંટવાથી રસ ચૂસતી જીવાત અને ઈંડાંનો નાશ થાય છે. આ જમીન અને મિત્ર કીટકો માટે સંપૂર્ણ સુરક્ષિત છે."
                    Language.HINDI -> "🌿 नीम तेल के बारे में:\n" +
                            "हाँ! नीम का तेल (10,000 ppm) 30 मिली प्रति 15 लीटर पंप में मिलाकर छिड़कने से रस चूसने वाले कीटों व अंडों का प्रभावी नियंत्रण होता है। यह पर्यावरण के लिए सुरक्षित है।"
                    Language.ENGLISH -> "🌿 About Neem formulation:\n" +
                            "Yes! Spraying Neem oil (10,000 ppm) @ 30 ml per 15L pump effectively repels sucking insects and prevents egg laying while protecting beneficial predators."
                }
            }

            isPreventionQuery -> {
                when (language) {
                    Language.GUJARATI -> "🛡️ અટકાવવાના મુખ્ય ઉપાયો:\n" +
                            "• ${diagnosis.howToPrevent}\n" +
                            "• ${diagnosis.nonChemicalControl}"
                    Language.HINDI -> "🛡️ मुख्य रोकथाम उपाय:\n" +
                            "• ${diagnosis.howToPrevent}\n" +
                            "• ${diagnosis.nonChemicalControl}"
                    Language.ENGLISH -> "🛡️ Key preventive measures:\n" +
                            "• ${diagnosis.howToPrevent}\n" +
                            "• ${diagnosis.nonChemicalControl}"
                }
            }

            isDangerQuery -> {
                when (language) {
                    Language.GUJARATI -> "⚠️ નુકસાનની શક્યતા:\n" +
                            "જો સમયસર પગલાં ન લેવામાં આવે તો ઉત્પાદનમાં 20 થી 35% સુધી ઘટાડો થઈ શકે છે. તરત જ અસરગ્રસ્ત ભાગ દૂર કરો અથવા ભલામણ કરેલ ઉપાય શરૂ કરો."
                    Language.HINDI -> "⚠️ नुकसान की संभावना:\n" +
                            "यदि समय रहते उपचार न किया गया तो पैदावार में 20 से 35% तक की कमी आ सकती है। तुरंत प्रभावित हिस्सों को हटाएं और सलाह अनुसार उपचार करें।"
                    Language.ENGLISH -> "⚠️ Yield impact:\n" +
                            "If left untreated, yield loss can reach 20-35%. Act promptly by removing infected leaves and applying the recommended control."
                }
            }

            else -> {
                when (language) {
                    Language.GUJARATI -> "તમારા $cropName ના $problemName વિશે વધુ વિગત:\n" +
                            "• શું થઈ રહ્યું છે: ${diagnosis.whatIsHappening}\n" +
                            "• સલાહ: ${diagnosis.howToPrevent}\n" +
                            "તમે દવા, છંટકાવના સમય કે દેશી ઉપાયો વિશે પણ પૂછી શકો છો."
                    Language.HINDI -> "आपकी $cropName की फसल के $problemName के बारे में जानकारी:\n" +
                            "• क्या हो रहा है: ${diagnosis.whatIsHappening}\n" +
                            "• बचाव: ${diagnosis.howToPrevent}\n" +
                            "आप दवा, छिड़काव के समय या देसी नुस्खों के बारे में भी पूछ सकते हैं।"
                    Language.ENGLISH -> "Regarding $problemName on $cropName:\n" +
                            "• Symptoms: ${diagnosis.whatIsHappening}\n" +
                            "• Action: ${diagnosis.howToPrevent}\n" +
                            "You can ask about pesticide rates, best spraying times, or organic controls."
                }
            }
        }
    }
}
