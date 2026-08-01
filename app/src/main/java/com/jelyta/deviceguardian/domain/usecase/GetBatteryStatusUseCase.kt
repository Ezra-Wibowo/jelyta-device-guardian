package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.BatteryInfo
import com.jelyta.deviceguardian.service.BatteryMonitorService
import kotlinx.coroutines.flow.Flow

class GetBatteryStatusUseCase(
    private val batteryMonitorService: BatteryMonitorService
) {
    fun observeBatteryStatus(): Flow<BatteryInfo> {
        return batteryMonitorService.getBatteryStatusFlow()
    }

    fun getCurrentBatteryInfo(): BatteryInfo {
        return batteryMonitorService.getBatteryInfoOnce()
    }
}
