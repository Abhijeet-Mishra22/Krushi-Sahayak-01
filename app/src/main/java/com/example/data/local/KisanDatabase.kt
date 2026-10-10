package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DiagnosisEntity::class,
        ChatMessageEntity::class,
        PreventionTipEntity::class,
        CachedAgriculturalAdviceEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class KisanDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun chatDao(): ChatDao
    abstract fun preventionTipDao(): PreventionTipDao
    abstract fun cachedAdviceDao(): CachedAgriculturalAdviceDao

    companion object {
        @Volatile
        private var INSTANCE: KisanDatabase? = null

        fun getDatabase(context: Context): KisanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KisanDatabase::class.java,
                    "kisan_drishti_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
