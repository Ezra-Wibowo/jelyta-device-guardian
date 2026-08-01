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
