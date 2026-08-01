package com.jelyta.deviceguardian.testing

import com.jelyta.deviceguardian.domain.model.*
import com.jelyta.deviceguardian.domain.repository.DeviceRepository
import com.jelyta.deviceguardian.domain.repository.HealthRepository
import com.jelyta.deviceguardian.domain.repository.SecurityRepository
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

class FakeSecurityRepository : SecurityRepository {
    override suspend fun scanInstalledApps(): List<AppSecurityInfo> {
        return listOf(
            AppSecurityInfo(
                appName = "Test App",
                packageName = "com.test.app",
                requestedPermissions = emptyList(),
                riskLevel = RiskLevel.SAFE,
                isSystemApp = false
            )
        )
    }
}

class FakeHealthRepository : HealthRepository {
    private val logs = mutableListOf<OptimizationLog>()
    private val logsFlow = MutableStateFlow<List<OptimizationLog>>(emptyList())
    private val socMetricsFlow = MutableStateFlow(SocMetrics())
    private val incidentsFlow = MutableStateFlow<List<CyberIncident>>(emptyList())
    private val evidencesFlow = MutableStateFlow<List<DigitalEvidence>>(emptyList())
    private val iocsFlow = MutableStateFlow<List<IocItem>>(emptyList())
    private val assetsFlow = MutableStateFlow<List<AssetItem>>(emptyList())
    private val auditsFlow = MutableStateFlow<List<AuditLogItem>>(emptyList())
    private val threatsFlow = MutableStateFlow<List<ThreatItem>>(emptyList())

    override suspend fun saveOptimizationLog(log: OptimizationLog) {
        logs.add(log)
        logsFlow.value = logs.toList()
    }

    override fun getOptimizationLogs(): Flow<List<OptimizationLog>> = logsFlow

    override suspend fun saveHealthReport(report: HealthReport) {}

    override fun getHealthReports(): Flow<List<HealthReport>> = MutableStateFlow(emptyList())

    override fun getSocMetrics(): Flow<SocMetrics> = socMetricsFlow

    override fun getIncidents(): Flow<List<CyberIncident>> = incidentsFlow

    override suspend fun saveIncident(incident: CyberIncident) {
        incidentsFlow.value = incidentsFlow.value + incident
    }

    override fun getEvidences(): Flow<List<DigitalEvidence>> = evidencesFlow

    override suspend fun saveEvidence(evidence: DigitalEvidence) {
        evidencesFlow.value = evidencesFlow.value + evidence
    }

    override fun getIocs(): Flow<List<IocItem>> = iocsFlow

    override suspend fun saveIoc(ioc: IocItem) {
        iocsFlow.value = iocsFlow.value + ioc
    }

    override fun getAssets(): Flow<List<AssetItem>> = assetsFlow

    override suspend fun saveAsset(asset: AssetItem) {
        assetsFlow.value = assetsFlow.value + asset
    }

    override fun getAudits(): Flow<List<AuditLogItem>> = auditsFlow

    override suspend fun saveAudit(audit: AuditLogItem) {
        auditsFlow.value = auditsFlow.value + audit
    }

    override fun getThreats(): Flow<List<ThreatItem>> = threatsFlow

    override suspend fun saveThreat(threat: ThreatItem) {
        threatsFlow.value = threatsFlow.value + threat
    }
}
