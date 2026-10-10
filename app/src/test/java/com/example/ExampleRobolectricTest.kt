package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.*
import com.example.data.remote.NetworkMonitor
import com.example.engine.CropAndDiseaseDetector
import com.example.engine.LocalChatEngine
import com.example.model.Language
import com.example.service.GeminiCropAnalysisService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var testDb: KisanDatabase

    @Before
    fun setupDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testDb = Room.inMemoryDatabaseBuilder(context, KisanDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Krushi Sahayak", appName)
    }

    @Test
    fun `verify agricultural knowledge base crops and pesticides`() {
        assertTrue(AgriculturalKnowledgeBase.crops.isNotEmpty())
        val cotton = AgriculturalKnowledgeBase.getCropById("cotton")
        assertEquals("કપાસ", cotton.nameGu)
        assertEquals("कपास", cotton.nameHi)

        val pesticide = AgriculturalKnowledgeBase.getPesticideForDisease("cotton_aphid")
        assertNotNull(pesticide)
        assertEquals("Imidacloprid 17.8% SL", pesticide?.activeIngredient)
    }

    @Test
    fun `verify offline detector generates localized diagnosis`() {
        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        for (x in 0 until 100) {
            for (y in 0 until 100) {
                bmp.setPixel(x, y, Color.rgb(30, 160, 40))
            }
        }

        val result = CropAndDiseaseDetector.detectOffline(
            bitmap = bmp,
            language = Language.GUJARATI
        )

        assertNotNull(result)
        assertTrue(result.problemName.isNotBlank())
        assertTrue(result.whatIsHappening.isNotBlank())
        assertTrue(result.audioExplanation.isNotBlank())
    }

    @Test
    fun `verify local chat engine generates context aware advice`() {
        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val diag = CropAndDiseaseDetector.detectOffline(
            bitmap = bmp,
            language = Language.GUJARATI
        )

        val reply = LocalChatEngine.generateReply(
            question = "કઈ દવા છાંટવી?",
            diagnosis = diag,
            language = Language.GUJARATI
        )

        assertTrue(reply.contains("દવા") || reply.contains("સલાહ"))
    }

    @Test
    fun `verify GeminiCropAnalysisService offline and cached scenario`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val networkMonitor = NetworkMonitor(context)
        val service = GeminiCropAnalysisService(networkMonitor)

        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        for (x in 0 until 100) {
            for (y in 0 until 100) {
                bmp.setPixel(x, y, Color.rgb(40, 150, 45))
            }
        }

        val result = service.analyzeCrop(
            bitmap = bmp,
            language = Language.GUJARATI,
            forceOffline = true
        )

        assertNotNull(result)
        assertNotNull(result.crop)
        assertTrue(result.problemName.isNotBlank())

        val followUp = service.askFollowUp(
            question = "છંટકાવ ક્યારે કરવો?",
            currentDiagnosis = result,
            language = Language.GUJARATI
        )
        assertTrue(followUp.isNotBlank())
    }

    @Test
    fun `verify TextToSpeech manager functionality`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val ttsManager = com.example.speech.TtsManager(context)
        assertNotNull(ttsManager)

        // Initialize and test speak call
        ttsManager.onInit(android.speech.tts.TextToSpeech.SUCCESS)
        ttsManager.speak("પાક નિદાન", Language.GUJARATI, "લક્ષણો")

        assertEquals("લક્ષણો", ttsManager.activeUtterance.value)
        assertTrue(ttsManager.isSpeaking.value)

        ttsManager.stop()
        org.junit.Assert.assertFalse(ttsManager.isSpeaking.value)
        assertNull(ttsManager.activeUtterance.value)

        ttsManager.shutdown()
    }

    @Test
    fun `verify Room database diagnosis history operations`() = runBlocking {
        val dao = testDb.diagnosisDao()

        val entity = DiagnosisEntity(
            id = 101L,
            timestamp = System.currentTimeMillis(),
            imageUri = "file:///test/crop.jpg",
            cropId = "cotton",
            cropName = "કપાસ (Cotton)",
            problemName = "મોલો-મશી (Aphids)",
            healthStatus = "PEST_INFESTATION",
            confidenceLevel = "HIGH",
            confidencePercent = 88,
            isOnline = false,
            languageCode = "gu",
            whatIsHappening = "પાન નીચે ચીકણો રસ છે.",
            whyItHappens = "સુકા વાતાવરણમાં વધે છે.",
            whatToLookFor = "ઝીણી જીવાત.",
            howToPrevent = "યુરિયા ઘટાડો.",
            nonChemicalControl = "લીંબોળીનું તેલ છાંટો.",
            pesticideActiveIngredient = "Imidacloprid 17.8% SL",
            pesticideDilution = "3-4 ml / 15L",
            pesticidePhi = "40 days",
            pesticideAuthority = "CIBRC",
            audioExplanation = "કપાસમાં મોલો-મશી માટે ઈમિડાક્લોપ્રિડ છાંટો."
        )

        // 1. Insert
        val insertedId = dao.insertDiagnosis(entity)
        assertEquals(101L, insertedId)

        // 2. Query all via Flow
        val allDiagnoses = dao.getAllDiagnoses().first()
        assertEquals(1, allDiagnoses.size)
        assertEquals("કપાસ (Cotton)", allDiagnoses[0].cropName)

        // 3. Query by ID
        val retrieved = dao.getDiagnosisById(101L)
        assertNotNull(retrieved)
        assertEquals("Imidacloprid 17.8% SL", retrieved?.pesticideActiveIngredient)

        // 4. Query by Crop
        val byCrop = dao.getDiagnosesByCrop("cotton").first()
        assertEquals(1, byCrop.size)

        // 5. Search diagnoses
        val searchResults = dao.searchDiagnoses("Aphids").first()
        assertEquals(1, searchResults.size)

        // 6. Update voice notes and verify search on notes
        dao.updateNotes(101L, "North field block A sprayed with neem")
        val updated = dao.getDiagnosisById(101L)
        assertEquals("North field block A sprayed with neem", updated?.notes)

        val noteSearch = dao.searchDiagnoses("North field").first()
        assertEquals(1, noteSearch.size)

        // 7. Delete by ID
        dao.deleteDiagnosisById(101L)
        val afterDelete = dao.getAllDiagnoses().first()
        assertTrue(afterDelete.isEmpty())
        assertNull(dao.getDiagnosisById(101L))
    }

    @Test
    fun `verify SpeechRecognizerHelper intent creation`() {
        val intentGu = com.example.speech.SpeechRecognizerHelper.createSpeechIntent(Language.GUJARATI, "Test prompt")
        assertNotNull(intentGu)
        assertEquals("gu-IN", intentGu.getStringExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE))

        val intentHi = com.example.speech.SpeechRecognizerHelper.createSpeechIntent(Language.HINDI)
        assertEquals("hi-IN", intentHi.getStringExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE))
    }

    @Test
    fun `verify Room database prevention tips library operations`() = runBlocking {
        val dao = testDb.preventionTipDao()
        assertEquals(0, dao.getCount())

        // 1. Seed default offline prevention tips
        val defaultTips = AgriculturalKnowledgeBase.generateDefaultPreventionTips()
        assertTrue(defaultTips.isNotEmpty())
        dao.insertTips(defaultTips)

        val totalCount = dao.getCount()
        assertTrue(totalCount >= 15)

        // 2. Query all tips
        val allTips = dao.getAllTips().first()
        assertEquals(totalCount, allTips.size)

        // 3. Query tips by crop
        val cottonTips = dao.getTipsByCrop("cotton").first()
        assertTrue(cottonTips.isNotEmpty())
        assertTrue(cottonTips.all { it.cropId == "cotton" })

        val tomatoTips = dao.getTipsByCrop("tomato").first()
        assertTrue(tomatoTips.isNotEmpty())
        assertTrue(tomatoTips.all { it.cropId == "tomato" })

        // 4. Search tips by keyword
        val neemSearch = dao.searchTips("Neem").first()
        assertTrue(neemSearch.isNotEmpty())

        val gujaratiSearch = dao.searchTips("લીમડા").first()
        assertTrue(gujaratiSearch.isNotEmpty())

        // 5. Bookmark operations
        val firstTip = allTips.first()
        assertEquals(false, firstTip.isBookmarked)

        dao.updateBookmark(firstTip.id, true)
        val updatedTip = dao.getTipById(firstTip.id)
        assertNotNull(updatedTip)
        assertEquals(true, updatedTip?.isBookmarked)

        val bookmarkedList = dao.getBookmarkedTips().first()
        assertEquals(1, bookmarkedList.size)
        assertEquals(firstTip.id, bookmarkedList[0].id)

        // 6. Filter by category
        val fungalTips = dao.getTipsByCategory("FUNGAL").first()
        assertTrue(fungalTips.isNotEmpty())
        assertTrue(fungalTips.all { it.category == "FUNGAL" })
    }

    @Test
    fun `verify Room database cached agricultural advice schema and DAO operations`() = runBlocking {
        val adviceDao = testDb.cachedAdviceDao()
        assertEquals(0, adviceDao.getAdviceCount())

        // 1. Seed default cached advice
        val defaultAdvice = AgriculturalKnowledgeBase.generateDefaultCachedAdvice()
        assertTrue(defaultAdvice.isNotEmpty())
        adviceDao.insertAll(defaultAdvice)

        val totalAdviceCount = adviceDao.getAdviceCount()
        assertTrue(totalAdviceCount >= 5)

        // 2. Query all advice via Flow
        val allAdvice = adviceDao.getAllAdvice().first()
        assertEquals(totalAdviceCount, allAdvice.size)

        // 3. Query advice by crop
        val cottonAdvice = adviceDao.getAdviceByCrop("cotton").first()
        assertTrue(cottonAdvice.isNotEmpty())
        assertTrue(cottonAdvice.any { it.cropId == "cotton" })

        // 4. Query advice by category
        val biologicalAdvice = adviceDao.getAdviceByCategory("BIOLOGICAL").first()
        assertTrue(biologicalAdvice.isNotEmpty())
        assertEquals("BIOLOGICAL", biologicalAdvice.first().adviceCategory)

        // 5. Multi-lingual search
        val neemSearch = adviceDao.searchAdvice("Neem").first()
        assertTrue(neemSearch.isNotEmpty())

        val gujaratiSearch = adviceDao.searchAdvice("લીમડા").first()
        assertTrue(gujaratiSearch.isNotEmpty())

        val hindiSearch = adviceDao.searchAdvice("कीट").first()
        assertTrue(hindiSearch.isNotEmpty())

        // 6. Bookmark operation
        val firstItem = allAdvice.first()
        assertEquals(false, firstItem.isBookmarked)

        adviceDao.updateBookmark(firstItem.id, true)
        val updatedItem = adviceDao.getAdviceById(firstItem.id)
        assertNotNull(updatedItem)
        assertEquals(true, updatedItem?.isBookmarked)

        val bookmarkedAdvice = adviceDao.getBookmarkedAdvice().first()
        assertEquals(1, bookmarkedAdvice.size)
        assertEquals(firstItem.id, bookmarkedAdvice[0].id)

        // 7. Insert custom cached advice
        val customAdvice = CachedAgriculturalAdviceEntity(
            cropId = "rice",
            cropName = "Rice / ડાંગર",
            diseaseOrPestId = "bacterial_leaf_blight",
            topic = "Zinc Application & Water Drainage",
            adviceCategory = "NUTRITION",
            titleEn = "Correcting Zinc Deficiency to Prevent Secondary Blight",
            titleHi = "धान में जिंक की कमी और झुलसा रोकथाम",
            titleGu = "ડાંગરમાં ઝીંકની ઉણપ અને સુકારા નિયંત્રણ",
            contentEn = "Apply Zinc Sulphate 21% @ 10 kg/acre mixed with sand at 15-20 days after transplanting.",
            contentHi = "रोपाई के 15-20 दिन बाद 10 किग्रा जिंक सल्फेट प्रति एकड़ डालें।",
            contentGu = "રોપણીના 15-20 દિવસ બાદ એકર દીઠ 10 કિલો ઝીંક સલ્ફેટ રેતી સાથે મેળવી આપવું.",
            expertSource = "ICAR - National Rice Research Institute"
        )
        val insertedId = adviceDao.insertAdvice(customAdvice)
        assertTrue(insertedId > 0)
        assertEquals(totalAdviceCount + 1, adviceDao.getAdviceCount())

        // 8. Delete advice
        adviceDao.deleteAdviceById(insertedId)
        assertEquals(totalAdviceCount, adviceDao.getAdviceCount())
    }

    @Test
    fun `verify extended crop disease detection offline storage and status queries`() = runBlocking {
        val dao = testDb.diagnosisDao()

        val detection1 = DiagnosisEntity(
            id = 201L,
            timestamp = System.currentTimeMillis() - 1000,
            imageUri = "file:///storage/crop_tomato.jpg",
            cropId = "tomato",
            cropName = "ટામેટા (Tomato)",
            problemName = "આગોતરો સુકારો (Early Blight)",
            healthStatus = "DISEASE",
            confidenceLevel = "HIGH",
            confidencePercent = 92,
            isOnline = false,
            languageCode = "gu",
            whatIsHappening = "પાન પર કથ્થાઈ ચક્રાકાર ટપકાં છે.",
            whyItHappens = "અલ્ટરનેરિયા ફૂગના કારણે થાય છે.",
            whatToLookFor = "ગોળ રિંગ જેવા ટપકાં.",
            howToPrevent = "મેન્કોઝેબ છાંટો.",
            nonChemicalControl = "ટ્રાઇકોડર્મા વાપરો.",
            pesticideActiveIngredient = "Mancozeb 75% WP",
            pesticideDilution = "30g / 15L",
            pesticidePhi = "3 days",
            pesticideAuthority = "CIBRC",
            audioExplanation = "ટામેટામાં આગોતરા સુકારા માટે મેન્કોઝેબ છાંટો.",
            severity = "MODERATE"
        )

        val detection2 = DiagnosisEntity(
            id = 202L,
            timestamp = System.currentTimeMillis(),
            imageUri = "file:///storage/crop_cotton.jpg",
            cropId = "cotton",
            cropName = "કપાસ (Cotton)",
            problemName = "તંદુરસ્ત પાક (Healthy Crop)",
            healthStatus = "HEALTHY",
            confidenceLevel = "HIGH",
            confidencePercent = 98,
            isOnline = false,
            languageCode = "gu",
            whatIsHappening = "છોડ સંપૂર્ણ સ્વસ્થ છે.",
            whyItHappens = "કોઈ રોગ કે જીવાત નથી.",
            whatToLookFor = "લીલાછમ પાન.",
            howToPrevent = "યોગ્ય ખાતર અને પાણી આપતા રહો.",
            nonChemicalControl = "લીમડાના અર્કનો સામાન્ય છંટકાવ.",
            pesticideActiveIngredient = null,
            pesticideDilution = null,
            pesticidePhi = null,
            pesticideAuthority = null,
            audioExplanation = "પાક તંદુરસ્ત છે."
        )

        dao.insertDiagnoses(listOf(detection1, detection2))
        assertEquals(2, dao.getDiagnosesCount())

        // Query by status
        val diseaseOnly = dao.getDiagnosesByStatus("DISEASE").first()
        assertEquals(1, diseaseOnly.size)
        assertEquals("tomato", diseaseOnly[0].cropId)

        val healthyOnly = dao.getDiagnosesByStatus("HEALTHY").first()
        assertEquals(1, healthyOnly.size)
        assertEquals("cotton", healthyOnly[0].cropId)

        // Bookmark detection
        dao.updateBookmark(201L, true)
        val bookmarked = dao.getBookmarkedDiagnoses().first()
        assertEquals(1, bookmarked.size)
        assertEquals(201L, bookmarked[0].id)

        // Recent limit query
        val recentOne = dao.getRecentDiagnoses(1).first()
        assertEquals(1, recentOne.size)
        assertEquals(202L, recentOne[0].id)

        dao.clearAll()
        assertEquals(0, dao.getDiagnosesCount())
    }

    @Test
    fun `verify pesticide dosage calculator engine calculations and units`() {
        // 1. Test 1 Vigha Gujarat with 15L Knapsack Pump (4 ml / pump)
        val vighaResult = com.example.model.PesticideDosageCalculatorEngine.calculate(
            areaValue = 1.0,
            areaUnit = com.example.model.AreaUnit.VIGHA_GUJARAT, // 0.4 acre
            tankCapacityLiters = 15.0,
            formulationType = com.example.model.ChemicalFormulationType.LIQUID,
            inputMode = com.example.model.DosageInputMode.PER_15L_PUMP,
            enteredDoseValue = 4.0,
            waterPerAcreLiters = 180.0,
            language = com.example.model.Language.GUJARATI
        )

        assertEquals(0.40, vighaResult.areaInAcres, 0.001)
        assertEquals(72.0, vighaResult.totalWaterLiters, 0.01) // 0.4 * 180 = 72L
        assertEquals(4.8, vighaResult.totalPumpsCountExact, 0.01) // 72 / 15 = 4.8 pumps
        assertEquals(5, vighaResult.totalPumpsRounded) // ceil(4.8) = 5
        assertEquals(4.0, vighaResult.chemicalDosePerPump, 0.01)
        assertEquals(19.2, vighaResult.totalChemicalRequired, 0.01) // 72L * (4/15) = 19.2 ml
        assertEquals("ml", vighaResult.chemicalUnit)
        assertTrue(vighaResult.measuringCapGuidance.isNotEmpty())
        assertTrue(vighaResult.mixingSequence.size >= 4)

        // 2. Test 2 Acres with 16L pump and powder formulation (Mancozeb 35g/pump)
        val powderResult = com.example.model.PesticideDosageCalculatorEngine.calculate(
            areaValue = 2.0,
            areaUnit = com.example.model.AreaUnit.ACRE,
            tankCapacityLiters = 16.0,
            formulationType = com.example.model.ChemicalFormulationType.POWDER,
            inputMode = com.example.model.DosageInputMode.PER_15L_PUMP,
            enteredDoseValue = 35.0,
            waterPerAcreLiters = 200.0,
            bottlePrice = 500.0,
            bottleSize = 1000.0, // 1000g pack @ Rs 500
            language = com.example.model.Language.HINDI
        )

        assertEquals(2.0, powderResult.areaInAcres, 0.001)
        assertEquals(400.0, powderResult.totalWaterLiters, 0.01) // 2 * 200 = 400L
        assertEquals(25.0, powderResult.totalPumpsCountExact, 0.01) // 400 / 16 = 25 pumps
        assertEquals(25, powderResult.totalPumpsRounded)
        assertEquals(37.33, powderResult.chemicalDosePerPump, 0.1) // 35 * (16/15) = 37.33g
        assertEquals("gm", powderResult.chemicalUnit)
        assertNotNull(powderResult.totalCost)
        assertTrue(powderResult.totalCost!! > 0)

        // 3. Test Verified Presets catalog
        val presets = com.example.data.local.PesticidePresetsData.presets
        assertTrue(presets.size >= 8)
        assertTrue(presets.all { it.dosePer15LPump > 0 })
        assertTrue(presets.all { it.tradeOrCommonName.isNotBlank() })
        assertTrue(presets.all { it.activeIngredient.isNotBlank() })

        val cottonPresets = com.example.data.local.PesticidePresetsData.findPresetByCrop("cotton")
        assertTrue(cottonPresets.isNotEmpty())
    }
}
