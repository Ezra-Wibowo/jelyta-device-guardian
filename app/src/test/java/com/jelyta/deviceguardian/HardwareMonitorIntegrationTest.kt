package com.jelyta.deviceguardian

import com.jelyta.deviceguardian.data.datasource.hardware.HardwareMonitorManager
import com.jelyta.deviceguardian.domain.model.HardwareSnapshot
import com.jelyta.deviceguardian.domain.usecase.GetHardwareSnapshotUseCase
import com.jelyta.deviceguardian.presentation.hardware.HardwareDashboardViewModel
import com.jelyta.deviceguardian.testing.FakeHardwareDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private class HardwareTestContext : android.content.ContextWrapper(null)

class TestHardwareMonitorManager(private val dummySnapshot: HardwareSnapshot) : HardwareMonitorManager(
    context = HardwareTestContext()
) {
    override fun getHardwareSnapshotFlow(): Flow<HardwareSnapshot> = flowOf(dummySnapshot)
    override fun getSingleHardwareSnapshot(): HardwareSnapshot = dummySnapshot
}

@OptIn(ExperimentalCoroutinesApi::class)
class HardwareMonitorIntegrationTest {

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
    fun testHardwareDashboardViewModel_CollectsSnapshotFlowSuccessfully() = runTest {
        val fakeDataSource = FakeHardwareDataSource()
        val dummySnapshot = fakeDataSource.getOptimalHardwareSnapshot()
        val fakeManager = TestHardwareMonitorManager(dummySnapshot)

        val useCase = GetHardwareSnapshotUseCase(fakeManager)
        val viewModel = HardwareDashboardViewModel(useCase)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.snapshot)
        assertEquals(100, state.snapshot?.health?.score)
        assertEquals("Li-ion", state.snapshot?.batteryInfo?.technology)
    }

    @Test
    fun testHardwareDashboardViewModel_SelectCategory_UpdatesCategoryIndex() = runTest {
        val fakeDataSource = FakeHardwareDataSource()
        val dummySnapshot = fakeDataSource.getOptimalHardwareSnapshot()
        val fakeManager = TestHardwareMonitorManager(dummySnapshot)

        val useCase = GetHardwareSnapshotUseCase(fakeManager)
        val viewModel = HardwareDashboardViewModel(useCase)

        testScheduler.advanceUntilIdle()

        viewModel.selectCategory(2)
        assertEquals(2, viewModel.uiState.value.selectedCategoryIndex)
    }
}
