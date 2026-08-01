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
