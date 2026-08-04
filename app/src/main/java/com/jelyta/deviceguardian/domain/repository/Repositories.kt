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
    suspend fun clearChatHistory()
}

interface CloudRepository {
    suspend fun syncDeviceMetrics(metrics: DeviceMetrics, healthScore: Int): Result<String>
}

interface HealthRepository {
    suspend fun saveHealthReport(report: HealthReport)
    fun getHealthReports(): Flow<List<HealthReport>>
    fun getOptimizationLogs(): Flow<List<OptimizationLog>>
    suspend fun saveOptimizationLog(log: OptimizationLog)

    fun getSocMetrics(): Flow<SocMetrics>
    fun getIncidents(): Flow<List<CyberIncident>>
    suspend fun saveIncident(incident: CyberIncident)
    fun getEvidences(): Flow<List<DigitalEvidence>>
    suspend fun saveEvidence(evidence: DigitalEvidence)
    fun getIocs(): Flow<List<IocItem>>
    suspend fun saveIoc(ioc: IocItem)
    fun getAssets(): Flow<List<AssetItem>>
    suspend fun saveAsset(asset: AssetItem)
    fun getAudits(): Flow<List<AuditLogItem>>
    suspend fun saveAudit(audit: AuditLogItem)
    fun getThreats(): Flow<List<ThreatItem>>
    suspend fun saveThreat(threat: ThreatItem)
}
