package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "diagnosis_history",
    indices = [
        Index(value = ["cropId"]),
        Index(value = ["timestamp"]),
        Index(value = ["healthStatus"])
    ]
)
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String,
    val cropId: String,
    val cropName: String,
    val problemName: String,
    val healthStatus: String,
    val confidenceLevel: String,
    val confidencePercent: Int,
    val isOnline: Boolean,
    val languageCode: String,
    val whatIsHappening: String,
    val whyItHappens: String,
    val whatToLookFor: String,
    val howToPrevent: String,
    val nonChemicalControl: String,
    val pesticideActiveIngredient: String?,
    val pesticideDilution: String?,
    val pesticidePhi: String?,
    val pesticideAuthority: String?,
    val audioExplanation: String,
    val notes: String? = null,
    val isBookmarked: Boolean = false,
    val severity: String? = null
)

@Entity(tableName = "chat_history")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diagnosisId: Long,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedImageUri: String? = null
)

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnosis_history ORDER BY timestamp DESC")
    fun getAllDiagnoses(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosis_history WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisById(id: Long): DiagnosisEntity?

    @Query("SELECT * FROM diagnosis_history WHERE cropId = :cropId ORDER BY timestamp DESC")
    fun getDiagnosesByCrop(cropId: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosis_history WHERE healthStatus = :healthStatus ORDER BY timestamp DESC")
    fun getDiagnosesByStatus(healthStatus: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosis_history WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedDiagnoses(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosis_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentDiagnoses(limit: Int): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnosis_history WHERE cropName LIKE '%' || :query || '%' OR problemName LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchDiagnoses(query: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT COUNT(*) FROM diagnosis_history")
    suspend fun getDiagnosesCount(): Int

    @Query("UPDATE diagnosis_history SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("UPDATE diagnosis_history SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: DiagnosisEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnoses(diagnoses: List<DiagnosisEntity>)

    @Update
    suspend fun updateDiagnosis(diagnosis: DiagnosisEntity)

    @Query("DELETE FROM diagnosis_history WHERE id = :id")
    suspend fun deleteDiagnosisById(id: Long)

    @Query("DELETE FROM diagnosis_history")
    suspend fun clearAll()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_history WHERE diagnosisId = :diagnosisId ORDER BY timestamp ASC")
    fun getMessagesForDiagnosis(diagnosisId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_history WHERE diagnosisId = :diagnosisId")
    suspend fun deleteMessagesForDiagnosis(diagnosisId: Long)
}

/**
 * Local database entity storing offline crop disease prevention tips and verified agronomic advice
 */
@Entity(tableName = "prevention_tips")
data class PreventionTipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: String,
    val cropNameEn: String,
    val cropNameHi: String,
    val cropNameGu: String,
    val diseaseId: String,
    val diseaseNameEn: String,
    val diseaseNameHi: String,
    val diseaseNameGu: String,
    val category: String, // FUNGAL, VIRAL, BACTERIAL, PEST, CULTURAL, DEFICIENCY
    val symptomsSummaryEn: String,
    val symptomsSummaryHi: String,
    val symptomsSummaryGu: String,
    val preventionEn: String,
    val preventionHi: String,
    val preventionGu: String,
    val biologicalIpmEn: String,
    val biologicalIpmHi: String,
    val biologicalIpmGu: String,
    val culturalPracticesEn: String,
    val culturalPracticesHi: String,
    val culturalPracticesGu: String,
    val isBookmarked: Boolean = false
)

@Dao
interface PreventionTipDao {
    @Query("SELECT * FROM prevention_tips ORDER BY id ASC")
    fun getAllTips(): Flow<List<PreventionTipEntity>>

    @Query("SELECT * FROM prevention_tips WHERE cropId = :cropId ORDER BY id ASC")
    fun getTipsByCrop(cropId: String): Flow<List<PreventionTipEntity>>

    @Query("SELECT * FROM prevention_tips WHERE category = :category ORDER BY id ASC")
    fun getTipsByCategory(category: String): Flow<List<PreventionTipEntity>>

    @Query("SELECT * FROM prevention_tips WHERE isBookmarked = 1 ORDER BY id ASC")
    fun getBookmarkedTips(): Flow<List<PreventionTipEntity>>

    @Query("SELECT * FROM prevention_tips WHERE id = :id LIMIT 1")
    suspend fun getTipById(id: Long): PreventionTipEntity?

    @Query("SELECT * FROM prevention_tips WHERE diseaseNameEn LIKE '%' || :query || '%' OR diseaseNameHi LIKE '%' || :query || '%' OR diseaseNameGu LIKE '%' || :query || '%' OR cropNameEn LIKE '%' || :query || '%' OR cropNameHi LIKE '%' || :query || '%' OR cropNameGu LIKE '%' || :query || '%' OR preventionEn LIKE '%' || :query || '%' OR preventionHi LIKE '%' || :query || '%' OR preventionGu LIKE '%' || :query || '%' OR biologicalIpmEn LIKE '%' || :query || '%' OR biologicalIpmHi LIKE '%' || :query || '%' OR biologicalIpmGu LIKE '%' || :query || '%'")
    fun searchTips(query: String): Flow<List<PreventionTipEntity>>

    @Query("SELECT COUNT(*) FROM prevention_tips")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<PreventionTipEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: PreventionTipEntity): Long

    @Query("UPDATE prevention_tips SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM prevention_tips")
    suspend fun clearAllTips()
}

/**
 * Local Room entity storing cached agricultural advice and agronomic best practices
 * for offline availability in fields without cellular connectivity.
 */
@Entity(
    tableName = "cached_agricultural_advice",
    indices = [
        Index(value = ["cropId"]),
        Index(value = ["adviceCategory"]),
        Index(value = ["cachedTimestamp"])
    ]
)
data class CachedAgriculturalAdviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: String,                  // e.g., "cotton", "tomato", "wheat", "general"
    val cropName: String,                // e.g., "Cotton / કપાસ"
    val diseaseOrPestId: String? = null, // e.g., "cotton_aphid", "late_blight"
    val topic: String,                   // e.g., "Organic Sucking Pest Management"
    val adviceCategory: String,          // "PREVENTION", "BIOLOGICAL", "CHEMICAL", "IRRIGATION", "NUTRITION"
    val titleEn: String,
    val titleHi: String,
    val titleGu: String,
    val contentEn: String,
    val contentHi: String,
    val contentGu: String,
    val seasonalStage: String? = null,   // "SOWING", "VEGETATIVE", "FLOWERING", "FRUITING", "HARVEST"
    val expertSource: String = "CIBRC / State Agri Dept",
    val cachedTimestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
)

@Dao
interface CachedAgriculturalAdviceDao {
    @Query("SELECT * FROM cached_agricultural_advice ORDER BY cachedTimestamp DESC")
    fun getAllAdvice(): Flow<List<CachedAgriculturalAdviceEntity>>

    @Query("SELECT * FROM cached_agricultural_advice WHERE cropId = :cropId OR cropId = 'general' ORDER BY cachedTimestamp DESC")
    fun getAdviceByCrop(cropId: String): Flow<List<CachedAgriculturalAdviceEntity>>

    @Query("SELECT * FROM cached_agricultural_advice WHERE adviceCategory = :category ORDER BY cachedTimestamp DESC")
    fun getAdviceByCategory(category: String): Flow<List<CachedAgriculturalAdviceEntity>>

    @Query("SELECT * FROM cached_agricultural_advice WHERE isBookmarked = 1 ORDER BY cachedTimestamp DESC")
    fun getBookmarkedAdvice(): Flow<List<CachedAgriculturalAdviceEntity>>

    @Query("SELECT * FROM cached_agricultural_advice WHERE id = :id LIMIT 1")
    suspend fun getAdviceById(id: Long): CachedAgriculturalAdviceEntity?

    @Query("""
        SELECT * FROM cached_agricultural_advice 
        WHERE titleEn LIKE '%' || :query || '%' 
           OR titleHi LIKE '%' || :query || '%' 
           OR titleGu LIKE '%' || :query || '%' 
           OR contentEn LIKE '%' || :query || '%' 
           OR contentHi LIKE '%' || :query || '%' 
           OR contentGu LIKE '%' || :query || '%' 
           OR topic LIKE '%' || :query || '%' 
           OR cropName LIKE '%' || :query || '%'
        ORDER BY cachedTimestamp DESC
    """)
    fun searchAdvice(query: String): Flow<List<CachedAgriculturalAdviceEntity>>

    @Query("SELECT COUNT(*) FROM cached_agricultural_advice")
    suspend fun getAdviceCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvice(advice: CachedAgriculturalAdviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adviceList: List<CachedAgriculturalAdviceEntity>)

    @Update
    suspend fun updateAdvice(advice: CachedAgriculturalAdviceEntity)

    @Query("UPDATE cached_agricultural_advice SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM cached_agricultural_advice WHERE id = :id")
    suspend fun deleteAdviceById(id: Long)

    @Query("DELETE FROM cached_agricultural_advice")
    suspend fun clearAllAdvice()
}


