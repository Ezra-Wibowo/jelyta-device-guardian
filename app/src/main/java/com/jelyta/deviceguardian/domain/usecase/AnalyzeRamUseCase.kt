package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.DeviceMetrics

class AnalyzeRamUseCase {
    operator fun invoke(metrics: DeviceMetrics): String {
        return "RAM Usage: ${metrics.ramUsedMb} MB / ${metrics.ramTotalMb} MB (${metrics.ramPercent}%)."
    }
}
