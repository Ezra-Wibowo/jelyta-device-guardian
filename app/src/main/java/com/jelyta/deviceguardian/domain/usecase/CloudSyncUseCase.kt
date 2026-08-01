package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import com.jelyta.deviceguardian.domain.repository.CloudRepository

class CloudSyncUseCase(
    private val cloudRepository: CloudRepository
) {
    suspend operator fun invoke(metrics: DeviceMetrics, healthScore: Int): Result<String> {
        return cloudRepository.syncDeviceMetrics(metrics, healthScore)
    }
}
