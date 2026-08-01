package com.jelyta.deviceguardian

import com.jelyta.deviceguardian.domain.model.PerformanceMode
import com.jelyta.deviceguardian.domain.model.RiskLevel
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRiskLevelOrder() {
        assertTrue(RiskLevel.HIGH.ordinal < RiskLevel.MEDIUM.ordinal)
        assertTrue(RiskLevel.MEDIUM.ordinal < RiskLevel.SAFE.ordinal)
    }

    @Test
    fun testHealthScoreCalculation() {
        val useCase = CalculateHealthScoreUseCase()
        val metrics = DeviceMetrics(
            ramUsedMb = 2000,
            ramTotalMb = 4000,
            ramPercent = 50,
            storageFreeGb = 30.0,
            storageTotalGb = 64.0,
            storagePercent = 53,
            batteryPercent = 80,
            batteryTempCelsius = 30.0f,
            isCharging = false,
            estimatedCpuUsagePercent = 30,
            performanceMode = PerformanceMode.NORMAL
        )
        val report = useCase(metrics)
        assertEquals(100, report.score)
        assertEquals("Excellent", report.statusText)
    }
}
