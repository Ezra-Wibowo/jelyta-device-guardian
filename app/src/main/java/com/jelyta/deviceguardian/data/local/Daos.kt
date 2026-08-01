package com.jelyta.deviceguardian.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}

@Dao
interface HealthReportDao {
    @Query("SELECT * FROM health_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<HealthReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: HealthReportEntity)
}

@Dao
interface OptimizationLogDao {
    @Query("SELECT * FROM optimization_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<OptimizationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: OptimizationLogEntity)
}

@Dao
interface SecurityIncidentDao {
    @Query("SELECT COUNT(*) FROM cyber_incidents")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM cyber_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<SecurityIncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: SecurityIncidentEntity)
}

@Dao
interface DigitalEvidenceDao {
    @Query("SELECT COUNT(*) FROM digital_evidences")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM digital_evidences ORDER BY timestamp DESC")
    fun getAllEvidences(): Flow<List<DigitalEvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: DigitalEvidenceEntity)
}

@Dao
interface IocItemDao {
    @Query("SELECT COUNT(*) FROM ioc_items")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM ioc_items ORDER BY timestamp DESC")
    fun getAllIocs(): Flow<List<IocItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIoc(ioc: IocItemEntity)
}

@Dao
interface AssetItemDao {
    @Query("SELECT COUNT(*) FROM asset_items")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM asset_items ORDER BY timestamp DESC")
    fun getAllAssets(): Flow<List<AssetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetItemEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT COUNT(*) FROM audit_logs")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAudits(): Flow<List<AuditLogItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AuditLogItemEntity)
}

@Dao
interface ThreatItemDao {
    @Query("SELECT COUNT(*) FROM threat_items")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM threat_items ORDER BY timestamp DESC")
    fun getAllThreats(): Flow<List<ThreatItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(threat: ThreatItemEntity)
}
