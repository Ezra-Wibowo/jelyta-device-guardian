package com.jelyta.deviceguardian

import android.content.ContextWrapper
import com.jelyta.deviceguardian.domain.model.PerformanceMode
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.usecase.OptimizeDeviceUseCase
import com.jelyta.deviceguardian.notification.NotificationHelper
import com.jelyta.deviceguardian.presentation.dashboard.DashboardViewModel
import com.jelyta.deviceguardian.testing.FakeDeviceRepository
import com.jelyta.deviceguardian.testing.FakeHealthRepository
import com.jelyta.deviceguardian.testing.FakeSecurityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private class TestContext : ContextWrapper(null)

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

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
    fun testDashboardViewModel_RunTurboBoost_UpdatesStateAndToast() = runTest {
        val fakeDevRepo = FakeDeviceRepository()
        val fakeSecurityRepo = FakeSecurityRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val calculateUseCase = CalculateHealthScoreUseCase()
        val optimizeUseCase = OptimizeDeviceUseCase(fakeDevRepo, fakeHealthRepo)
        val notificationHelper = NotificationHelper(TestContext())

        val viewModel = DashboardViewModel(
            deviceRepository = fakeDevRepo,
            securityRepository = fakeSecurityRepo,
            healthRepository = fakeHealthRepo,
            calculateHealthScoreUseCase = calculateUseCase,
            optimizeDeviceUseCase = optimizeUseCase,
            notificationHelper = notificationHelper
        )

        testScheduler.advanceUntilIdle()

        viewModel.runTurboBoost()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isHealing)
        assertNotNull(state.toastMessage)
        assertTrue(state.toastMessage!!.contains("512 MB"))
    }

    @Test
    fun testDashboardViewModel_SetPerformanceMode_UpdatesMetrics() = runTest {
        val fakeDevRepo = FakeDeviceRepository()
        val fakeSecurityRepo = FakeSecurityRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val calculateUseCase = CalculateHealthScoreUseCase()
        val optimizeUseCase = OptimizeDeviceUseCase(fakeDevRepo, fakeHealthRepo)
        val notificationHelper = NotificationHelper(TestContext())

        val viewModel = DashboardViewModel(
            deviceRepository = fakeDevRepo,
            securityRepository = fakeSecurityRepo,
            healthRepository = fakeHealthRepo,
            calculateHealthScoreUseCase = calculateUseCase,
            optimizeDeviceUseCase = optimizeUseCase,
            notificationHelper = notificationHelper
        )

        testScheduler.advanceUntilIdle()

        viewModel.setPerformanceMode(PerformanceMode.PERFORMANCE)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PerformanceMode.PERFORMANCE, state.metrics?.performanceMode)
    }
}
