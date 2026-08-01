package com.jelyta.deviceguardian.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jelyta.deviceguardian.core.di.NetworkModule
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.data.repository.CloudRepositoryImpl
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase

class CloudSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val hardwareDataSource = SystemHardwareDataSource(applicationContext)
            val networkModule = NetworkModule()
            val cloudRepository = CloudRepositoryImpl(networkModule.fastApiService)
            val calculateHealthScoreUseCase = CalculateHealthScoreUseCase()

            val metrics = hardwareDataSource.fetchCurrentMetrics()
            val report = calculateHealthScoreUseCase(metrics)

            val syncResult = cloudRepository.syncDeviceMetrics(metrics, report.score)
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
