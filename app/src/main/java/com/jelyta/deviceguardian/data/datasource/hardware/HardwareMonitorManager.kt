package com.jelyta.deviceguardian.data.datasource.hardware

import android.content.Context
import com.jelyta.deviceguardian.domain.model.HardwareSnapshot
import com.jelyta.deviceguardian.service.BatteryMonitorService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

open class HardwareMonitorManager(private val context: Context) {

    private val batteryMonitorService by lazy {
        try {
            BatteryMonitorService(context)
        } catch (_: Exception) {
            null
        }
    }
    private val storageMonitor = StorageMonitor()
    private val memoryMonitor = MemoryMonitor(context)
    private val cpuMonitor = CpuMonitor()
    private val thermalMonitor = ThermalMonitor(context)
    private val healthCalculator = HardwareHealthCalculator()

    open fun getHardwareSnapshotFlow(): Flow<HardwareSnapshot> {
        val flow = batteryMonitorService?.getBatteryStatusFlow() ?: flowOf(
            com.jelyta.deviceguardian.domain.model.BatteryInfo(
                level = 85, scale = 100, batteryPercent = 85, temperatureCelsius = 30.0f,
                isCharging = false, plugType = "Not Plugged", health = "Good", voltageMv = 3800,
                technology = "Li-ion", status = "Discharging"
            )
        )
        return flow.map { batteryInfo ->
            val memoryInfo = memoryMonitor.getMemoryInfo()
            val storageInfo = storageMonitor.getStorageInfo()
            val cpuInfo = cpuMonitor.getCpuInfo()
            val thermalInfo = thermalMonitor.getThermalInfo(batteryInfo.temperatureCelsius)

            val health = healthCalculator.calculateHealth(
                batteryInfo = batteryInfo,
                memoryInfo = memoryInfo,
                storageInfo = storageInfo,
                cpuInfo = cpuInfo,
                thermalInfo = thermalInfo
            )

            HardwareSnapshot(
                batteryInfo = batteryInfo,
                memoryInfo = memoryInfo,
                storageInfo = storageInfo,
                cpuInfo = cpuInfo,
                thermalInfo = thermalInfo,
                health = health
            )
        }
    }

    fun getPeriodicHardwareSnapshotFlow(intervalMs: Long = 3000L): Flow<HardwareSnapshot> = flow {
        while (true) {
            val batteryInfo = batteryMonitorService?.getBatteryInfoOnce() ?: com.jelyta.deviceguardian.domain.model.BatteryInfo(
                level = 85, scale = 100, batteryPercent = 85, temperatureCelsius = 30.0f,
                isCharging = false, plugType = "Not Plugged", health = "Good", voltageMv = 3800,
                technology = "Li-ion", status = "Discharging"
            )
            val memoryInfo = memoryMonitor.getMemoryInfo()
            val storageInfo = storageMonitor.getStorageInfo()
            val cpuInfo = cpuMonitor.getCpuInfo()
            val thermalInfo = thermalMonitor.getThermalInfo(batteryInfo.temperatureCelsius)

            val health = healthCalculator.calculateHealth(
                batteryInfo = batteryInfo,
                memoryInfo = memoryInfo,
                storageInfo = storageInfo,
                cpuInfo = cpuInfo,
                thermalInfo = thermalInfo
            )

            emit(
                HardwareSnapshot(
                    batteryInfo = batteryInfo,
                    memoryInfo = memoryInfo,
                    storageInfo = storageInfo,
                    cpuInfo = cpuInfo,
                    thermalInfo = thermalInfo,
                    health = health
                )
            )
            delay(intervalMs)
        }
    }

    open fun getSingleHardwareSnapshot(): HardwareSnapshot {
        val batteryInfo = batteryMonitorService?.getBatteryInfoOnce() ?: com.jelyta.deviceguardian.domain.model.BatteryInfo(
            level = 85, scale = 100, batteryPercent = 85, temperatureCelsius = 30.0f,
            isCharging = false, plugType = "Not Plugged", health = "Good", voltageMv = 3800,
            technology = "Li-ion", status = "Discharging"
        )
        val memoryInfo = memoryMonitor.getMemoryInfo()
        val storageInfo = storageMonitor.getStorageInfo()
        val cpuInfo = cpuMonitor.getCpuInfo()
        val thermalInfo = thermalMonitor.getThermalInfo(batteryInfo.temperatureCelsius)

        val health = healthCalculator.calculateHealth(
            batteryInfo = batteryInfo,
            memoryInfo = memoryInfo,
            storageInfo = storageInfo,
            cpuInfo = cpuInfo,
            thermalInfo = thermalInfo
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
