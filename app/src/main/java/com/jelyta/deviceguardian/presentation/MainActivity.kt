package com.jelyta.deviceguardian.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jelyta.deviceguardian.core.navigation.MainAppNavigation
import com.jelyta.deviceguardian.presentation.theme.JelytaGuardianTheme
import com.jelyta.deviceguardian.worker.JunkThresholdWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        scheduleJunkThresholdWorker()

        setContent {
            JelytaGuardianTheme {
                MainAppNavigation()
            }
        }
    }

    private fun scheduleJunkThresholdWorker() {
        try {
            val workData = workDataOf(JunkThresholdWorker.KEY_THRESHOLD_MB to 250)

            val periodicRequest = PeriodicWorkRequestBuilder<JunkThresholdWorker>(6, TimeUnit.HOURS)
                .setInputData(workData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                JunkThresholdWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )

            // Also trigger an immediate check on startup
            val oneTimeRequest = OneTimeWorkRequestBuilder<JunkThresholdWorker>()
                .setInputData(workData)
                .build()

            WorkManager.getInstance(applicationContext).enqueue(oneTimeRequest)
        } catch (_: Exception) {}
    }
}
