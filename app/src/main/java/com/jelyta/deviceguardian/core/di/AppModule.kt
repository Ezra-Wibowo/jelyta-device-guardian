package com.jelyta.deviceguardian.core.di

import android.content.Context
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.notification.NotificationHelper
import com.jelyta.deviceguardian.service.BatteryMonitorService

class AppModule(private val context: Context) {
    val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(context)
    }

    val hardwareDataSource: SystemHardwareDataSource by lazy {
        SystemHardwareDataSource(context)
    }

    val batteryMonitorService: BatteryMonitorService by lazy {
        BatteryMonitorService(context)
    }

    val hardwareMonitorManager: com.jelyta.deviceguardian.data.datasource.hardware.HardwareMonitorManager by lazy {
        com.jelyta.deviceguardian.data.datasource.hardware.HardwareMonitorManager(context)
    }
}
