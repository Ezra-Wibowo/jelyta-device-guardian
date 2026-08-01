package com.jelyta.deviceguardian.presentation.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.*
import com.jelyta.deviceguardian.domain.repository.DeviceRepository
import com.jelyta.deviceguardian.domain.repository.HealthRepository
import com.jelyta.deviceguardian.domain.repository.SecurityRepository
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.usecase.OptimizeDeviceUseCase
import com.jelyta.deviceguardian.notification.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest

data class DashboardUiState(
    val metrics: DeviceMetrics? = null,
    val healthReport: HealthReport? = null,
    val socMetrics: SocMetrics = SocMetrics(),
    val auditHistory: List<AuditLogItem> = emptyList(),
    val isHealing: Boolean = false,
    val toastMessage: String? = null
)

class DashboardViewModel(
    private val deviceRepository: DeviceRepository,
    private val securityRepository: SecurityRepository,
    private val healthRepository: HealthRepository,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase,
    private val optimizeDeviceUseCase: OptimizeDeviceUseCase,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDeviceMetrics()
        observeSocMetrics()
        observeAuditHistory()
    }

    private fun observeDeviceMetrics() {
        viewModelScope.launch {
            deviceRepository.getDeviceMetrics().collect { metrics ->
                val report = calculateHealthScoreUseCase(metrics)
                _uiState.update { it.copy(metrics = metrics, healthReport = report) }
            }
        }
    }

    private fun observeSocMetrics() {
        viewModelScope.launch {
            healthRepository.getSocMetrics().collect { metrics ->
                _uiState.update { it.copy(socMetrics = metrics) }
            }
        }
    }

    private fun observeAuditHistory() {
        viewModelScope.launch {
            healthRepository.getAudits().collect { history ->
                _uiState.update { it.copy(auditHistory = history) }
            }
        }
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        viewModelScope.launch {
            deviceRepository.setPerformanceMode(mode)
        }
    }

    fun runCyberAudit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val apps = securityRepository.scanInstalledApps()
            val highRiskCount = apps.count { it.riskLevel == RiskLevel.HIGH }

            // Save Assets
            healthRepository.saveAsset(AssetItem(assetName = "CPU Core Cluster", assetType = "Hardware", status = "ONLINE"))
            healthRepository.saveAsset(AssetItem(assetName = "System RAM Module", assetType = "Memory", status = "ACTIVE"))
            healthRepository.saveAsset(AssetItem(assetName = "Installed App Catalog (${apps.size})", assetType = "Software", status = "INSPECTED"))

            // Save Incident / Threat if high risk apps found
            if (highRiskCount > 0) {
                healthRepository.saveIncident(
                    CyberIncident(
                        title = "High Risk Application Permission Threat",
                        severity = "HIGH",
                        description = "Detected $highRiskCount apps with elevated dangerous permissions."
                    )
                )
                healthRepository.saveThreat(
                    ThreatItem(
                        threatName = "Unrestricted Permission Vector",
                        riskLevel = "HIGH",
                        status = "ANALYZED"
                    )
                )
                healthRepository.saveIoc(
                    IocItem(
                        indicatorType = "Package Risk Flag",
                        indicatorValue = apps.firstOrNull { it.riskLevel == RiskLevel.HIGH }?.packageName ?: "unknown.pkg",
                        threatCategory = "Permission Abuse"
                    )
                )
            }

            // Save Audit Log
            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "Enterprise Cyber Security Audit",
                    outcome = if (highRiskCount > 0) "ATTENTION REQUIRED" else "PASSED",
                    details = "Inspected ${apps.size} installed applications. $highRiskCount security threats identified."
                )
            )

            notificationHelper.showSecurityNotification(
                "Audit Completed",
                "Inspected ${apps.size} apps. $highRiskCount security threat flags."
            )

            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Cyber Audit Completed: ${apps.size} apps inspected, $highRiskCount threat flags found."
                )
            }
        }
    }

    fun runTurboBoost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val log = optimizeDeviceUseCase.runTurboBoost()
            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "One-Tap Turbo RAM Boost",
                    outcome = "SUCCESS",
                    details = "Reclaimed ${log.reclaimedMemoryMb} MB of active memory."
                )
            )
            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Turbo Boost Complete! ${log.reclaimedMemoryMb} MB memory freed."
                )
            }
        }
    }

    fun runSelfHealing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val log = optimizeDeviceUseCase.runCacheClean()
            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "AI Standby Auto-Repair",
                    outcome = "REPAIRED",
                    details = "Cleared temporary cache degradation (${log.reclaimedMemoryMb} MB)."
                )
            )
            notificationHelper.showHealthNotification(
                "AI Software Auto-Repair",
                "System health optimized. Cache degradation repaired."
            )
            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Self-Healing Complete! Software health optimized, cache cleared (${log.reclaimedMemoryMb} MB)."
                )
            }
        }
    }

    fun captureDigitalEvidence() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val currentMetrics = _uiState.value.metrics
            val time = System.currentTimeMillis()
            val sampleData = "SNAPSHOT_${time}_${currentMetrics?.ramPercent ?: 0}_${currentMetrics?.batteryPercent ?: 0}"
            val hash = MessageDigest.getInstance("SHA-256")
                .digest(sampleData.toByteArray())
                .joinToString("") { "%02x".format(it) }

            healthRepository.saveEvidence(
                DigitalEvidence(
                    artifactName = "System State Forensics Snapshot",
                    artifactType = "Memory & Power State",
                    hashSha256 = hash
                )
            )

            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "Digital Forensics Capture",
                    outcome = "CAPTURED",
                    details = "Digital evidence hash SHA-256 generated."
                )
            )

            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Digital Evidence Captured! SHA-256: ${hash.take(12)}..."
                )
            }
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return DashboardViewModel(
                deviceRepository = container.repositoryModule.deviceRepository,
                securityRepository = container.repositoryModule.securityRepository,
                healthRepository = container.repositoryModule.healthRepository,
                calculateHealthScoreUseCase = container.useCaseModule.calculateHealthScoreUseCase,
                optimizeDeviceUseCase = container.useCaseModule.optimizeDeviceUseCase,
                notificationHelper = container.appModule.notificationHelper
            ) as T
        }
    }
}
