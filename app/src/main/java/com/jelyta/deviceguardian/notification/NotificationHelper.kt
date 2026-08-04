package com.jelyta.deviceguardian.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

open class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_HEALTH = "guardian_health_channel"
        const val CHANNEL_SECURITY = "guardian_security_channel"
        const val CHANNEL_JUNK = "guardian_junk_channel"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val healthChannel = NotificationChannel(
                    CHANNEL_HEALTH,
                    "Device Health Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Alerts regarding battery temperature and memory usage." }

                val securityChannel = NotificationChannel(
                    CHANNEL_SECURITY,
                    "Security & Privacy Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Alerts regarding application permissions and privacy risks." }

                val junkChannel = NotificationChannel(
                    CHANNEL_JUNK,
                    "Silent Junk & Temp File Monitor",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Silent non-intrusive notifications when temp junk files exceed threshold."
                    setShowBadge(true)
                }

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.createNotificationChannel(healthChannel)
                manager?.createNotificationChannel(securityChannel)
                manager?.createNotificationChannel(junkChannel)
            } catch (_: Exception) {
                // Ignore in headless / test environment
            }
        }
    }

    open fun showHealthNotification(title: String, message: String) {
        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_HEALTH)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(1001, builder.build())
        } catch (_: Exception) {
            // Ignore in headless / test environment
        }
    }

    open fun showSecurityNotification(title: String, message: String) {
        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_SECURITY)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(1002, builder.build())
        } catch (_: Exception) {
            // Ignore in headless / test environment
        }
    }

    open fun showJunkThresholdNotification(junkMb: Int, thresholdMb: Int, contentIntent: android.app.PendingIntent? = null) {
        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_JUNK)
                .setSmallIcon(android.R.drawable.ic_menu_delete)
                .setContentTitle("🧹 Junk Files Exceed ${thresholdMb}MB (${junkMb}MB Found)")
                .setContentText("Tap for one-tap clean to reclaim storage and boost CPU performance.")
                .setPriority(NotificationCompat.PRIORITY_LOW) // Silent / non-intrusive
                .setSilent(true)
                .setAutoCancel(true)

            if (contentIntent != null) {
                builder.setContentIntent(contentIntent)
                builder.addAction(
                    android.R.drawable.ic_menu_delete,
                    "⚡ One-Tap Clean Now",
                    contentIntent
                )
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(1003, builder.build())
        } catch (_: Exception) {
            // Ignore in headless / test environment
        }
    }
}
