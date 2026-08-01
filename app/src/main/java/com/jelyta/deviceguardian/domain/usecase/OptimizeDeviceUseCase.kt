package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.OptimizationLog
import com.jelyta.deviceguardian.domain.repository.DeviceRepository
import com.jelyta.deviceguardian.domain.repository.HealthRepository

class OptimizeDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val healthRepository: HealthRepository
) {
    suspend fun runTurboBoost(): OptimizationLog {
        val freedMb = deviceRepository.performTurboBoost()
        val log = OptimizationLog(
            actionName = "Turbo Boost RAM Cleanup",
            reclaimedMemoryMb = freedMb,
            healthScoreImpact = 10
        )
        healthRepository.saveOptimizationLog(log)
        return log
    }

    suspend fun runCacheClean(): OptimizationLog {
        val freedMb = deviceRepository.performCacheClean()
        val log = OptimizationLog(
            actionName = "System Cache Purge",
            reclaimedMemoryMb = freedMb,
            healthScoreImpact = 8
        )
        healthRepository.saveOptimizationLog(log)
        return log
    }
}
