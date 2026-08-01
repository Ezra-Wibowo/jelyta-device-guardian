package com.jelyta.deviceguardian

import com.jelyta.deviceguardian.domain.model.BatteryInfo
import com.jelyta.deviceguardian.domain.usecase.GetBatteryStatusUseCase
import com.jelyta.deviceguardian.presentation.battery.BatteryMonitorViewModel
import com.jelyta.deviceguardian.service.BatteryMonitorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TestBatteryMonitorService(private val mockInfo: BatteryInfo) : BatteryMonitorService(
    context = BatteryTestContext()
) {
    override fun getBatteryStatusFlow(): Flow<BatteryInfo> = flowOf(mockInfo)
    override fun getBatteryInfoOnce(): BatteryInfo = mockInfo
}

private class BatteryTestContext : android.content.ContextWrapper(null)

@OptIn(ExperimentalCoroutinesApi::class)
class BatteryMonitorTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testBatteryMonitorViewModel_EmitsBatteryInfoRealtime() = runTest {
        val dummyBatteryInfo = BatteryInfo(
            level = 80,
            scale = 100,
            batteryPercent = 80,
            temperatureCelsius = 32.5f,
            isCharging = true,
            plugType = "AC Charger",
            health = "Good",
            voltageMv = 4100,
            technology = "Li-ion",
            status = "Charging"
        )

        val fakeService = TestBatteryMonitorService(dummyBatteryInfo)

        val useCase = GetBatteryStatusUseCase(fakeService)
        val viewModel = BatteryMonitorViewModel(useCase)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.batteryInfo)
        assertEquals(80, state.batteryInfo?.batteryPercent)
        assertTrue(state.batteryInfo?.isCharging == true)
        assertEquals(32.5f, state.batteryInfo?.temperatureCelsius)
        assertFalse(state.isOverheating)
    }

    @Test
    fun testBatteryMonitorViewModel_DetectsOverheating() = runTest {
        val dummyBatteryInfo = BatteryInfo(
            level = 90,
            scale = 100,
            batteryPercent = 90,
            temperatureCelsius = 43.5f,
            isCharging = true,
            plugType = "AC Charger",
            health = "Overheat",
            voltageMv = 4300,
            technology = "Li-ion",
            status = "Charging"
        )

        val fakeService = TestBatteryMonitorService(dummyBatteryInfo)

        val useCase = GetBatteryStatusUseCase(fakeService)
        val viewModel = BatteryMonitorViewModel(useCase)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isOverheating)
        assertNotNull(state.warningMessage)
        assertTrue(state.warningMessage!!.contains("high"))
    }
}
