package com.jelyta.deviceguardian.domain.model

data class DeviceMetrics(
    val ramUsedMb: Long,
    val ramTotalMb: Long,
    val ramPercent: Int,
    val storageFreeGb: Double,
    val storageTotalGb: Double,
    val storagePercent: Int,
    val batteryPercent: Int,
    val batteryTempCelsius: Float,
    val isCharging: Boolean,
    val estimatedCpuUsagePercent: Int,
    val performanceMode: PerformanceMode = PerformanceMode.NORMAL
)

data class HealthReport(
    val id: Long = 0,
    val score: Int,
    val statusText: String,
    val keyIssues: List<String>,
    val recommendations: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class OptimizationLog(
    val id: Long = 0,
    val actionName: String,
    val reclaimedMemoryMb: Int,
    val healthScoreImpact: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSecurityInfo(
    val appName: String,
    val packageName: String,
    val requestedPermissions: List<String>,
    val riskLevel: RiskLevel,
    val isSystemApp: Boolean
)

data class ChatMessage(
    val id: Long = 0,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class CyberIncident(
    val id: Long = 0,
    val title: String,
    val severity: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DigitalEvidence(
    val id: Long = 0,
    val artifactName: String,
    val artifactType: String,
    val hashSha256: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class IocItem(
    val id: Long = 0,
    val indicatorType: String,
    val indicatorValue: String,
    val threatCategory: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssetItem(
    val id: Long = 0,
    val assetName: String,
    val assetType: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AuditLogItem(
    val id: Long = 0,
    val auditTitle: String,
    val outcome: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ThreatItem(
    val id: Long = 0,
    val threatName: String,
    val riskLevel: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SocMetrics(
    val incidentsCount: Int = 0,
    val evidencesCount: Int = 0,
    val iocCount: Int = 0,
    val assetsCount: Int = 0,
    val auditCount: Int = 0,
    val threatsCount: Int = 0
)
