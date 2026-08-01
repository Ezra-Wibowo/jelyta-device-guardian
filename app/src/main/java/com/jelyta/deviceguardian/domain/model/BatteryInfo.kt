package com.jelyta.deviceguardian.domain.model

data class BatteryInfo(
    val level: Int,
    val scale: Int,
    val batteryPercent: Int,
    val temperatureCelsius: Float,
    val isCharging: Boolean,
    val plugType: String,
    val health: String,
    val voltageMv: Int,
    val technology: String,
    val status: String,
    val currentNowMicroAmperes: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)
