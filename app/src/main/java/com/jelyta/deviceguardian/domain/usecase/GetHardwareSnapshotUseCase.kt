package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.data.datasource.hardware.HardwareMonitorManager
import com.jelyta.deviceguardian.domain.model.HardwareSnapshot
import kotlinx.coroutines.flow.Flow

class GetHardwareSnapshotUseCase(
    private val hardwareMonitorManager: HardwareMonitorManager
) {
    fun observeHardwareSnapshot(): Flow<HardwareSnapshot> {
        return hardwareMonitorManager.getHardwareSnapshotFlow()
    }

    fun getSingleSnapshot(): HardwareSnapshot {
        return hardwareMonitorManager.getSingleHardwareSnapshot()
    }
}
