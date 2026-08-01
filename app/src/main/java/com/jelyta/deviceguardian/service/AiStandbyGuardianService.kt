package com.jelyta.deviceguardian.service

import android.content.Context
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.domain.model.AuditLogItem
import com.jelyta.deviceguardian.domain.model.OptimizationLog
import com.jelyta.deviceguardian.domain.repository.HealthRepository
import com.jelyta.deviceguardian.notification.NotificationHelper

class AiStandbyGuardianService(
    private val context: Context,
    private val hardwareDataSource: SystemHardwareDataSource,
    private val healthRepository: HealthRepository,
    private val notificationHelper: NotificationHelper
) {
    suspend fun runStandbyInspectionAndAutoRepair(): String {
        val metrics = hardwareDataSource.fetchCurrentMetrics()
        val repairsApplied = mutableListOf<String>()

        if (metrics.ramPercent > 75) {
            val freedRam = hardwareDataSource.performTurboBoost()
            repairsApplied.add("Auto-freed $freedRam MB RAM")
            healthRepository.saveOptimizationLog(
                OptimizationLog(
                    actionName = "AI Standby Auto-Boost",
                    reclaimedMemoryMb = freedRam,
                    healthScoreImpact = 5
                )
            )
        }

        if (metrics.storagePercent > 80) {
            val freedCache = hardwareDataSource.performCacheClean()
            repairsApplied.add("Auto-cleared $freedCache MB corrupted cache")
            healthRepository.saveOptimizationLog(
                OptimizationLog(
                    actionName = "AI Standby Cache Auto-Repair",
                    reclaimedMemoryMb = freedCache,
                    healthScoreImpact = 5
                )
            )
        }

        val outcomeMessage = if (repairsApplied.isNotEmpty()) {
            val message = "AI Standby Guardian auto-repaired software degradation: ${repairsApplied.joinToString(", ")}"
            notificationHelper.showHealthNotification("AI Guardian Active", message)
            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "AI Standby Auto-Repair",
                    outcome = "REPAIRED",
                    details = message
                )
            )
            message
        } else {
            val message = "AI Standby Guardian verified software health: System running smoothly."
            healthRepository.saveAudit(
                AuditLogItem(
                    auditTitle = "AI Standby Health Inspection",
                    outcome = "OPTIMAL",
                    details = message
                )
            )
            message
        }

        return outcomeMessage
    }
}
