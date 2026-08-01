package com.jelyta.deviceguardian.domain.repository

import com.jelyta.deviceguardian.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceMetrics(): Flow<DeviceMetrics>
    suspend fun setPerformanceMode(mode: PerformanceMode)
    suspend fun getPerformanceMode(): PerformanceMode
    suspend fun performTurboBoost(): Int
    suspend fun performCacheClean(): Int
}

interface SecurityRepository {
    suspend fun scanInstalledApps(): List<AppSecurityInfo>
}

interface AssistantRepository {
    suspend fun sendMessage(query: String, context: DeviceMetrics): String
    suspend fun translateText(text: String, targetLanguage: String): String
    fun getChatHistory(): Flow<List<ChatMessage>>
    suspend fun saveChatMessage(message: ChatMessage)
}

interface CloudRepository {
    suspend fun syncDeviceMetrics(metrics: DeviceMetrics, healthScore: Int): Result<String>
}

interface HealthRepository {
    suspend fun saveHealthReport(report: HealthReport)
    fun getHealthReports(): Flow<List<HealthReport>>
    fun getOptimizationLogs(): Flow<List<OptimizationLog>>
    suspend fun saveOptimizationLog(log: OptimizationLog)
}
