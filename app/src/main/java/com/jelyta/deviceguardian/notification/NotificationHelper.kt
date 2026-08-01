package com.jelyta.deviceguardian.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_HEALTH = "guardian_health_channel"
        const val CHANNEL_SECURITY = "guardian_security_channel"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(healthChannel)
            manager.createNotificationChannel(securityChannel)
        }
    }

    fun showHealthNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_HEALTH)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, builder.build())
    }

    fun showSecurityNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_SECURITY)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1002, builder.build())
    }
}
