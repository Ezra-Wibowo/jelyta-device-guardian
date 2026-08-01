package com.jelyta.deviceguardian.testing

import com.jelyta.deviceguardian.domain.model.*
import com.jelyta.deviceguardian.domain.repository.DeviceRepository
import com.jelyta.deviceguardian.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDeviceRepository : DeviceRepository {
    private val metricsFlow = MutableStateFlow(
        DeviceMetrics(
            ramUsedMb = 2000,
            ramTotalMb = 4000,
            ramPercent = 50,
            storageFreeGb = 32.0,
            storageTotalGb = 64.0,
            storagePercent = 50,
            batteryPercent = 85,
            batteryTempCelsius = 30.0f,
            isCharging = false,
            estimatedCpuUsagePercent = 25,
            performanceMode = PerformanceMode.NORMAL
        )
    )

    override fun getDeviceMetrics(): Flow<DeviceMetrics> = metricsFlow

    override suspend fun performTurboBoost(): Int = 512

    override suspend fun performCacheClean(): Int = 256

    override suspend fun setPerformanceMode(mode: PerformanceMode) {
        metricsFlow.value = metricsFlow.value.copy(performanceMode = mode)
    }

    override suspend fun getPerformanceMode(): PerformanceMode = metricsFlow.value.performanceMode
}

class FakeHealthRepository : HealthRepository {
    private val logs = mutableListOf<OptimizationLog>()
    private val logsFlow = MutableStateFlow<List<OptimizationLog>>(emptyList())

    override suspend fun saveOptimizationLog(log: OptimizationLog) {
        logs.add(log)
        logsFlow.value = logs.toList()
    }

    override fun getOptimizationLogs(): Flow<List<OptimizationLog>> = logsFlow

    override suspend fun saveHealthReport(report: HealthReport) {}

    override fun getHealthReports(): Flow<List<HealthReport>> = MutableStateFlow(emptyList())
}
