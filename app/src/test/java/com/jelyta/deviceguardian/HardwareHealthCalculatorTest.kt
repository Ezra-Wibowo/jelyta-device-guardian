package com.jelyta.deviceguardian

import com.jelyta.deviceguardian.data.datasource.hardware.HardwareHealthCalculator
import com.jelyta.deviceguardian.domain.model.HardwareStatus
import com.jelyta.deviceguardian.testing.FakeHardwareDataSource
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HardwareHealthCalculatorTest {

    private lateinit var calculator: HardwareHealthCalculator
    private lateinit var fakeData: FakeHardwareDataSource

    @Before
    fun setup() {
        calculator = HardwareHealthCalculator()
        fakeData = FakeHardwareDataSource()
    }

    @Test
    fun testCalculateHealth_OptimalHardware_Returns100ScoreAndExcellent() {
        val snapshot = fakeData.getOptimalHardwareSnapshot()

        val health = calculator.calculateHealth(
            batteryInfo = snapshot.batteryInfo,
            memoryInfo = snapshot.memoryInfo,
            storageInfo = snapshot.storageInfo,
            cpuInfo = snapshot.cpuInfo,
            thermalInfo = snapshot.thermalInfo
        )

        assertEquals(100, health.score)
        assertEquals(HardwareStatus.EXCELLENT, health.status)
        assertTrue(health.issues.first().contains("No hardware"))
    }

    @Test
    fun testCalculateHealth_StressedHardware_DeductsPointsAndReturnsCritical() {
        val snapshot = fakeData.getStressedHardwareSnapshot()

        val health = calculator.calculateHealth(
            batteryInfo = snapshot.batteryInfo,
            memoryInfo = snapshot.memoryInfo,
            storageInfo = snapshot.storageInfo,
            cpuInfo = snapshot.cpuInfo,
            thermalInfo = snapshot.thermalInfo
        )

        assertTrue(health.score < 50)
        assertEquals(HardwareStatus.CRITICAL, health.status)
        assertTrue(health.issues.size >= 3)
        assertTrue(health.recommendations.isNotEmpty())
    }
}
