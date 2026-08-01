package com.jelyta.deviceguardian.testing

import com.jelyta.deviceguardian.domain.model.*

class FakeHardwareDataSource {

    fun getOptimalHardwareSnapshot(): HardwareSnapshot {
        val batteryInfo = BatteryInfo(
            level = 95,
            scale = 100,
            batteryPercent = 95,
            temperatureCelsius = 28.5f,
            isCharging = false,
            plugType = "Not Plugged",
            health = "Good",
            voltageMv = 4000,
            technology = "Li-ion",
            status = "Discharging"
        )

        val memoryInfo = MemoryHardwareInfo(
            totalMemoryBytes = 4096L * 1024 * 1024,
            availMemoryBytes = 2500L * 1024 * 1024,
            usedMemoryBytes = 1596L * 1024 * 1024,
            usagePercentage = 38,
            isLowMemory = false,
            totalMb = 4096,
            usedMb = 1596,
            availMb = 2500
        )

        val storageInfo = StorageHardwareInfo(
            totalBytes = 64L * 1024 * 1024 * 1024,
            freeBytes = 40L * 1024 * 1024 * 1024,
            usedBytes = 24L * 1024 * 1024 * 1024,
            usagePercentage = 37,
            totalGb = 64.0,
            freeGb = 40.0,
            usedGb = 24.0
        )

        val cpuInfo = CpuHardwareInfo(
            availableProcessors = 8,
            estimatedCpuUsagePercent = 20,
            isEstimation = true,
            statusDescription = "Optimal 8-Core Runtime Load"
        )

        val thermalInfo = ThermalHardwareInfo(
            isSupported = true,
            thermalStatus = "Normal",
            temperatureCelsius = 28.5f,
            statusDescription = "Android Thermal Status: Normal"
        )

        val health = HardwareHealth(
            score = 100,
            status = HardwareStatus.EXCELLENT,
            summaryText = "Optimal state",
            issues = emptyList(),
            recommendations = emptyList(),
            scoreBreakdown = mapOf("RAM Engine" to 25, "Storage Engine" to 25, "Thermal & Battery" to 25, "CPU Engine" to 25)
        )

        return HardwareSnapshot(
            batteryInfo = batteryInfo,
            memoryInfo = memoryInfo,
            storageInfo = storageInfo,
            cpuInfo = cpuInfo,
            thermalInfo = thermalInfo,
            health = health
        )
    }

    fun getStressedHardwareSnapshot(): HardwareSnapshot {
        val batteryInfo = BatteryInfo(
            level = 15,
            scale = 100,
            batteryPercent = 15,
            temperatureCelsius = 44.0f,
            isCharging = true,
            plugType = "AC Charger",
            health = "Overheat",
            voltageMv = 4400,
            technology = "Li-ion",
            status = "Charging"
        )

        val memoryInfo = MemoryHardwareInfo(
            totalMemoryBytes = 4096L * 1024 * 1024,
            availMemoryBytes = 300L * 1024 * 1024,
            usedMemoryBytes = 3796L * 1024 * 1024,
            usagePercentage = 92,
            isLowMemory = true,
            totalMb = 4096,
            usedMb = 3796,
            availMb = 300
        )

        val storageInfo = StorageHardwareInfo(
            totalBytes = 64L * 1024 * 1024 * 1024,
            freeBytes = 3L * 1024 * 1024 * 1024,
            usedBytes = 61L * 1024 * 1024 * 1024,
            usagePercentage = 95,
            totalGb = 64.0,
            freeGb = 3.0,
            usedGb = 61.0
        )

        val cpuInfo = CpuHardwareInfo(
            availableProcessors = 8,
            estimatedCpuUsagePercent = 88,
            isEstimation = true,
            statusDescription = "Severe CPU Load"
        )

        val thermalInfo = ThermalHardwareInfo(
            isSupported = true,
            thermalStatus = "Severe Heat",
            temperatureCelsius = 44.0f,
            statusDescription = "Severe Thermal Stress"
        )

        val health = HardwareHealth(
            score = 35,
            status = HardwareStatus.CRITICAL,
            summaryText = "Critical stress state",
            issues = listOf("RAM Low Memory", "Storage full", "Thermal overheating"),
            recommendations = listOf("Disconnect charger", "Reclaim memory"),
            scoreBreakdown = mapOf("RAM Engine" to 10, "Storage Engine" to 10, "Thermal & Battery" to 0, "CPU Engine" to 13)
        )

        return HardwareSnapshot(
            batteryInfo = batteryInfo,
            memoryInfo = memoryInfo,
            storageInfo = storageInfo,
            cpuInfo = cpuInfo,
            thermalInfo = thermalInfo,
            health = health
        )
    }
}
