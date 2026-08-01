package com.jelyta.deviceguardian.domain.model

enum class HardwareStatus {
    EXCELLENT,
    GOOD,
    WARNING,
    CRITICAL
}

data class HardwareHealth(
    val score: Int,
    val status: HardwareStatus,
    val summaryText: String,
    val issues: List<String>,
    val recommendations: List<String>,
    val scoreBreakdown: Map<String, Int>
)

data class StorageHardwareInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val usagePercentage: Int,
    val totalGb: Double,
    val freeGb: Double,
    val usedGb: Double
)

data class MemoryHardwareInfo(
    val totalMemoryBytes: Long,
    val availMemoryBytes: Long,
    val usedMemoryBytes: Long,
    val usagePercentage: Int,
    val isLowMemory: Boolean,
    val totalMb: Long,
    val usedMb: Long,
    val availMb: Long
)

data class CpuHardwareInfo(
    val availableProcessors: Int,
    val estimatedCpuUsagePercent: Int,
    val isEstimation: Boolean = true,
    val statusDescription: String
)

data class ThermalHardwareInfo(
    val isSupported: Boolean,
    val thermalStatus: String,
    val temperatureCelsius: Float,
    val statusDescription: String
)

data class HardwareSnapshot(
    val batteryInfo: BatteryInfo,
    val memoryInfo: MemoryHardwareInfo,
    val storageInfo: StorageHardwareInfo,
    val cpuInfo: CpuHardwareInfo,
    val thermalInfo: ThermalHardwareInfo,
    val health: HardwareHealth,
    val timestamp: Long = System.currentTimeMillis()
)
