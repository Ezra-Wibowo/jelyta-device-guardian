package com.jelyta.deviceguardian.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jelyta.deviceguardian.core.di.AppModule
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase

class PeriodicHealthWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val appModule = AppModule(applicationContext)
            val hardwareDataSource = SystemHardwareDataSource(applicationContext)
            val metrics = hardwareDataSource.fetchCurrentMetrics()
            val calculateHealthScoreUseCase = CalculateHealthScoreUseCase()
            val report = calculateHealthScoreUseCase(metrics)

            if (report.score < 60) {
                appModule.notificationHelper.showHealthNotification(
                    "Device Guardian Health Alert",
                    "Health Score dropped to ${report.score}. Run One-Tap Boost."
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
