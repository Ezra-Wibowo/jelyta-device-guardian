package com.jelyta.deviceguardian.data.datasource.hardware

import android.content.Context
import android.os.Build
import android.os.PowerManager
import com.jelyta.deviceguardian.domain.model.ThermalHardwareInfo

class ThermalMonitor(private val context: Context) {

    fun getThermalInfo(batteryTempCelsius: Float): ThermalHardwareInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val statusInt = powerManager?.currentThermalStatus ?: PowerManager.THERMAL_STATUS_NONE

            val statusStr = when (statusInt) {
                PowerManager.THERMAL_STATUS_NONE -> "Normal"
                PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttling"
                PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Thermal Load"
                PowerManager.THERMAL_STATUS_SEVERE -> "Severe Heat"
                PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Thermal State"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Thermal Shutdown Imminent"
                PowerManager.THERMAL_STATUS_SHUTDOWN -> "Thermal Shutdown"
                else -> "Unknown"
            }

            ThermalHardwareInfo(
                isSupported = true,
                thermalStatus = statusStr,
                temperatureCelsius = batteryTempCelsius,
                statusDescription = "Android Thermal Framework API Active: $statusStr ($batteryTempCelsius°C)"
            )
        } else {
            ThermalHardwareInfo(
                isSupported = false,
                thermalStatus = if (batteryTempCelsius > 40.0f) "Warm" else "Normal",
                temperatureCelsius = batteryTempCelsius,
                statusDescription = "Hardware Thermal API requires Android 10+. Fallback to battery sensor ($batteryTempCelsius°C)."
            )
        }
    }
}
