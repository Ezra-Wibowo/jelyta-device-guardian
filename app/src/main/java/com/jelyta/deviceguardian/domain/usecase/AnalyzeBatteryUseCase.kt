package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.DeviceMetrics

class AnalyzeBatteryUseCase {
    operator fun invoke(metrics: DeviceMetrics): String {
        return when {
            metrics.batteryTempCelsius > 40.0f -> "Critical thermal status. Cool down the device immediately."
            metrics.batteryTempCelsius > 36.0f -> "Warm battery temperature detected. Avoid heavy gaming while charging."
            metrics.batteryPercent < 20 -> "Battery level low (${metrics.batteryPercent}%). Enable Battery Saver mode."
            else -> "Battery status normal (${metrics.batteryPercent}%, ${metrics.batteryTempCelsius}°C)."
        }
    }
}
