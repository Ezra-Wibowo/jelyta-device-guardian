package com.jelyta.deviceguardian

import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import com.jelyta.deviceguardian.domain.model.PerformanceMode
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.usecase.OptimizeDeviceUseCase
import com.jelyta.deviceguardian.testing.FakeDeviceRepository
import com.jelyta.deviceguardian.testing.FakeHealthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTest {

    @Test
    fun testCalculateHealthScoreUseCase_OptimalMetrics_Returns100Score() {
        val useCase = CalculateHealthScoreUseCase()
        val metrics = DeviceMetrics(
            ramUsedMb = 1500,
            ramTotalMb = 4000,
            ramPercent = 37,
            storageFreeGb = 40.0,
            storageTotalGb = 64.0,
            storagePercent = 37,
            batteryPercent = 90,
            batteryTempCelsius = 28.0f,
            isCharging = false,
            estimatedCpuUsagePercent = 20,
            performanceMode = PerformanceMode.NORMAL
        )

        val report = useCase(metrics)

        assertEquals(100, report.score)
        assertEquals("Excellent", report.statusText)
    }

    @Test
    fun testCalculateHealthScoreUseCase_HighRAMAndBatteryTemp_DeductsScore() {
        val useCase = CalculateHealthScoreUseCase()
        val metrics = DeviceMetrics(
            ramUsedMb = 3600,
            ramTotalMb = 4000,
            ramPercent = 90, // -20 score
            storageFreeGb = 40.0,
            storageTotalGb = 64.0,
            storagePercent = 37,
            batteryPercent = 90,
            batteryTempCelsius = 42.0f, // -15 score
            isCharging = false,
            estimatedCpuUsagePercent = 20,
            performanceMode = PerformanceMode.NORMAL
        )

        val report = useCase(metrics)

        assertEquals(65, report.score)
        assertEquals("Fair", report.statusText)
        assertTrue(report.keyIssues.size >= 2)
    }

    @Test
    fun testOptimizeDeviceUseCase_RunTurboBoost_SavesLogAndReturnsReclaimedMemory() = runTest {
        val fakeDevRepo = FakeDeviceRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val useCase = OptimizeDeviceUseCase(fakeDevRepo, fakeHealthRepo)

        val log = useCase.runTurboBoost()

        assertEquals("Turbo Boost RAM Cleanup", log.actionName)
        assertEquals(512, log.reclaimedMemoryMb)
    }
}
