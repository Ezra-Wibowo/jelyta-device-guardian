package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.DeviceMetrics

class AnalyzeStorageUseCase {
    operator fun invoke(metrics: DeviceMetrics): String {
        return "Free Space: ${String.format("%.1f", metrics.storageFreeGb)} GB / ${String.format("%.1f", metrics.storageTotalGb)} GB (${100 - metrics.storagePercent}% available)."
    }
}
