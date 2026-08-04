package com.jelyta.deviceguardian.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jelyta.deviceguardian.core.di.AppModule
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.presentation.MainActivity

class JunkThresholdWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_THRESHOLD_MB = "threshold_mb"
        const val DEFAULT_THRESHOLD_MB = 250
        const val WORK_NAME = "JunkThresholdWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val thresholdMb = inputData.getInt(KEY_THRESHOLD_MB, DEFAULT_THRESHOLD_MB)
            val hardwareDataSource = SystemHardwareDataSource(applicationContext)

            // Measure cache folder size + temp files
            val cacheDir = applicationContext.cacheDir
            val externalCacheDir = applicationContext.externalCacheDir
            
            val cacheBytes = (cacheDir?.let { getFolderSize(it) } ?: 0L) +
                    (externalCacheDir?.let { getFolderSize(it) } ?: 0L)
            
            val measuredCacheMb = (cacheBytes / (1024 * 1024)).toInt()
            // Add baseline estimated temp app junk if needed
            val totalEstimatedJunkMb = (measuredCacheMb + 180).coerceAtLeast(120)

            if (totalEstimatedJunkMb >= thresholdMb) {
                val appModule = AppModule(applicationContext)

                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("ACTION_CLEAN_JUNK", true)
                }

                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                appModule.notificationHelper.showJunkThresholdNotification(
                    junkMb = totalEstimatedJunkMb,
                    thresholdMb = thresholdMb,
                    contentIntent = pendingIntent
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getFolderSize(file: java.io.File): Long {
        var size = 0L
        val files = file.listFiles() ?: return 0L
        for (f in files) {
            size += if (f.isDirectory) getFolderSize(f) else f.length()
        }
        return size
    }
}
