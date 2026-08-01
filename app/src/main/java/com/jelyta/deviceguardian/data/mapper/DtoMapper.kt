package com.jelyta.deviceguardian.data.mapper

import com.jelyta.deviceguardian.data.remote.FastApiDeviceSyncRequest
import com.jelyta.deviceguardian.domain.model.DeviceMetrics

object DtoMapper {
    fun DeviceMetrics.toDto(deviceId: String, healthScore: Int): FastApiDeviceSyncRequest = FastApiDeviceSyncRequest(
        deviceId = deviceId,
        healthScore = healthScore,
        cpuUsage = estimatedCpuUsagePercent,
        ramUsage = ramPercent,
        storageUsage = storagePercent,
        batteryPercent = batteryPercent,
        batteryTemp = batteryTempCelsius,
        performanceMode = performanceMode.name
    )
}
