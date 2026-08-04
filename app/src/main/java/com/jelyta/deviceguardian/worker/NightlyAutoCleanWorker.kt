package com.jelyta.deviceguardian.worker

import android.content.Context
import androidx.work.*
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.AuditLogItem
import com.jelyta.deviceguardian.domain.model.OptimizationLog
import java.util.*
import java.util.concurrent.TimeUnit

class NightlyAutoCleanWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "NightlyAutoCleanWorker"
        const val PREFS_NAME = "guardian_nightly_settings"
        const val KEY_ENABLED = "nightly_clean_enabled"
        const val KEY_SCHEDULE_HOUR = "nightly_schedule_hour" // Default 2 (2 AM)

        fun schedule(context: Context, enabled: Boolean, hourOfDay: Int = 2) {
            val workManager = WorkManager.getInstance(context)
            if (!enabled) {
                workManager.cancelUniqueWork(WORK_NAME)
                return
            }

            // Calculate initial delay until target hour (e.g. 02:00 AM tonight)
            val calendar = Calendar.getInstance()
            val nowMillis = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (calendar.timeInMillis <= nowMillis) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            val initialDelayMillis = calendar.timeInMillis - nowMillis

            val constraints = Constraints.Builder()
                .setRequiresCharging(true) // Ideal for night time clean while charging
                .setRequiresDeviceIdle(true) // Avoid interrupting active phone usage
                .build()

            val request = PeriodicWorkRequestBuilder<NightlyAutoCleanWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val appContainer = AppContainer(applicationContext)
            val optimizeUseCase = appContainer.useCaseModule.optimizeDeviceUseCase
            val healthRepository = appContainer.repositoryModule.healthRepository
            val notificationHelper = appContainer.appModule.notificationHelper

            // Perform Cache Cleaning & Background RAM Purge
            val cacheLog = optimizeUseCase.runCacheClean()
            val ramLog = optimizeUseCase.runTurboBoost()
            val totalFreedMb = cacheLog.reclaimedMemoryMb + ramLog.reclaimedMemoryMb

            val timestamp = System.currentTimeMillis()

            val auditLog = AuditLogItem(
                auditTitle = "🌙 Automatic Nightly Cache & RAM Clean",
                outcome = "SUCCESS",
                details = "Pembersihan Otomatis Malam Hari Berhasil. Memory dibebaskan: ${totalFreedMb} MB. HP siap beroperasi cepat untuk esok hari.",
                timestamp = timestamp
            )
            healthRepository.saveAudit(auditLog)

            val optLog = OptimizationLog(
                actionName = "Pembersihan Malam Otomatis (WorkManager)",
                reclaimedMemoryMb = totalFreedMb,
                healthScoreImpact = 5,
                timestamp = timestamp
            )
            healthRepository.saveOptimizationLog(optLog)

            notificationHelper.showHealthNotification(
                "🌙 Pembersihan Malam Otomatis Selesai",
                "HP Bebas Sampah & RAM Optimal (+${totalFreedMb} MB Dibebaskan)."
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
