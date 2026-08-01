package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import com.jelyta.deviceguardian.domain.model.HealthReport

class CalculateHealthScoreUseCase {
    operator fun invoke(metrics: DeviceMetrics): HealthReport {
        var score = 100
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        if (metrics.ramPercent > 80) {
            score -= 20
            issues.add("RAM usage is high (${metrics.ramPercent}%)")
            recommendations.add("Run One-Tap Turbo Boost to free RAM")
        }

        if (metrics.storagePercent > 85) {
            score -= 20
            issues.add("Storage space is running low (${metrics.storagePercent}% full)")
            recommendations.add("Perform Cache Clean to clear temporary files")
        }

        if (metrics.batteryTempCelsius > 38.0f) {
            score -= 15
            issues.add("Battery temperature is elevated (${metrics.batteryTempCelsius}°C)")
            recommendations.add("Switch to Battery Saver mode and reduce screen brightness")
        }

        if (metrics.estimatedCpuUsagePercent > 75) {
            score -= 15
            issues.add("CPU load is intense (${metrics.estimatedCpuUsagePercent}%)")
            recommendations.add("Close unused background applications")
        }

        val finalScore = score.coerceIn(0, 100)
        val statusText = when {
            finalScore >= 85 -> "Excellent"
            finalScore >= 70 -> "Good"
            finalScore >= 50 -> "Fair"
            else -> "Needs Attention"
        }

        return HealthReport(
            score = finalScore,
            statusText = statusText,
            keyIssues = if (issues.isEmpty()) listOf("No critical hardware issues detected") else issues,
            recommendations = if (recommendations.isEmpty()) listOf("System running optimally") else recommendations
        )
    }
}
