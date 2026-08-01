package com.jelyta.deviceguardian.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChatMessageEntity::class,
        HealthReportEntity::class,
        OptimizationLogEntity::class,
        SecurityIncidentEntity::class,
        DigitalEvidenceEntity::class,
        IocItemEntity::class,
        AssetItemEntity::class,
        AuditLogItemEntity::class,
        ThreatItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GuardianDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun healthReportDao(): HealthReportDao
    abstract fun optimizationLogDao(): OptimizationLogDao
    abstract fun securityIncidentDao(): SecurityIncidentDao
    abstract fun digitalEvidenceDao(): DigitalEvidenceDao
    abstract fun iocItemDao(): IocItemDao
    abstract fun assetItemDao(): AssetItemDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun threatItemDao(): ThreatItemDao

    companion object {
        @Volatile
        private var INSTANCE: GuardianDatabase? = null

        fun getDatabase(context: Context): GuardianDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GuardianDatabase::class.java,
                    "jelyta_guardian_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
