package com.jelyta.deviceguardian.data.datasource.hardware

import com.jelyta.deviceguardian.domain.model.*

class HardwareHealthCalculator {

    fun calculateHealth(
        batteryInfo: BatteryInfo,
        memoryInfo: MemoryHardwareInfo,
        storageInfo: StorageHardwareInfo,
        cpuInfo: CpuHardwareInfo,
        thermalInfo: ThermalHardwareInfo
    ): HardwareHealth {

        var totalDeductions = 0
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        val breakdown = mutableMapOf<String, Int>()

        // 1. RAM Score Calculation (Max 25 pts)
        var ramScore = 25
        if (memoryInfo.isLowMemory) {
            ramScore -= 15
            issues.add("System is in Low Memory condition (${memoryInfo.usagePercentage}% RAM used)")
            recommendations.add("Run One-Tap Turbo Boost immediately to reclaim RAM.")
        } else if (memoryInfo.usagePercentage > 85) {
            ramScore -= 10
            issues.add("High RAM utilization (${memoryInfo.usagePercentage}%)")
            recommendations.add("Close memory-intensive background apps.")
        }
        breakdown["RAM Engine"] = ramScore.coerceAtLeast(0)

        // 2. Storage Score Calculation (Max 25 pts)
        var storageScore = 25
        if (storageInfo.usagePercentage > 90) {
            storageScore -= 15
            issues.add("Storage space is critically low (${storageInfo.usagePercentage}% filled)")
            recommendations.add("Clear temporary system cache files.")
        } else if (storageInfo.usagePercentage > 80) {
            storageScore -= 8
            issues.add("Storage space is over 80% full.")
            recommendations.add("Clean up residual app data.")
        }
        breakdown["Storage Engine"] = storageScore.coerceAtLeast(0)

        // 3. Battery & Thermal Score Calculation (Max 25 pts)
        var batteryThermalScore = 25
        if (batteryInfo.temperatureCelsius > 42.0f) {
            batteryThermalScore -= 15
            issues.add("Battery temperature critical (${batteryInfo.temperatureCelsius}°C)")
            recommendations.add("Disconnect charger and let device cool down.")
        } else if (batteryInfo.temperatureCelsius > 38.0f) {
            batteryThermalScore -= 8
            issues.add("Battery temperature elevated (${batteryInfo.temperatureCelsius}°C)")
            recommendations.add("Avoid gaming or heavy tasks while charging.")
        }

        if (batteryInfo.health != "Good" && batteryInfo.health != "Unknown") {
            batteryThermalScore -= 10
            issues.add("Battery hardware health issue reported: ${batteryInfo.health}")
            recommendations.add("Consider inspecting device battery health.")
        }
        breakdown["Thermal & Battery"] = batteryThermalScore.coerceAtLeast(0)

        // 4. CPU Load Score Calculation (Max 25 pts)
        var cpuScore = 25
        if (cpuInfo.estimatedCpuUsagePercent > 80) {
            cpuScore -= 12
            issues.add("CPU usage heavy (${cpuInfo.estimatedCpuUsagePercent}%)")
            recommendations.add("Reduce background app activity.")
        }
        breakdown["CPU Engine"] = cpuScore.coerceAtLeast(0)

        val finalScore = (ramScore + storageScore + batteryThermalScore + cpuScore).coerceIn(0, 100)

        val status = when {
            finalScore >= 85 -> HardwareStatus.EXCELLENT
            finalScore >= 70 -> HardwareStatus.GOOD
            finalScore >= 50 -> HardwareStatus.WARNING
            else -> HardwareStatus.CRITICAL
        }

        val summaryText = when (status) {
            HardwareStatus.EXCELLENT -> "All hardware sub-systems are operating at peak efficiency."
            HardwareStatus.GOOD -> "Hardware is functioning normally with light memory/battery load."
            HardwareStatus.WARNING -> "Hardware status requires attention. Optimization recommended."
            HardwareStatus.CRITICAL -> "Critical hardware thermal or memory stress detected."
        }

        return HardwareHealth(
            score = finalScore,
            status = status,
            summaryText = summaryText,
            issues = if (issues.isEmpty()) listOf("No hardware performance issues detected.") else issues,
            recommendations = if (recommendations.isEmpty()) listOf("System running optimally.") else recommendations,
            scoreBreakdown = breakdown
        )
    }
}
