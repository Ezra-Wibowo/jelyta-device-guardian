package com.jelyta.deviceguardian

import android.content.ContextWrapper
import com.jelyta.deviceguardian.domain.usecase.AnalyzePermissionUseCase
import com.jelyta.deviceguardian.notification.NotificationHelper
import com.jelyta.deviceguardian.presentation.security.SecurityViewModel
import com.jelyta.deviceguardian.testing.FakeHealthRepository
import com.jelyta.deviceguardian.testing.FakeSecurityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private class SecurityTestContext : ContextWrapper(null)

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityViewModelTest {

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
    fun testPogaPhoneLookup_OfficialBankNumber_ReturnsVerifiedEnterprise() = runTest {
        val fakeSecurityRepo = FakeSecurityRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val analyzePermissionUseCase = AnalyzePermissionUseCase(fakeSecurityRepo)
        val notificationHelper = NotificationHelper(SecurityTestContext())

        val viewModel = SecurityViewModel(
            analyzePermissionUseCase = analyzePermissionUseCase,
            healthRepository = fakeHealthRepo,
            notificationHelper = notificationHelper
        )

        testScheduler.advanceUntilIdle()

        // Input Official Bank Number with +62 prefix
        viewModel.updateCallerInput("+628121886710")
        viewModel.executeCallerLookup()

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isScanning)
        assertNotNull(state.callerResult)
        
        val result = state.callerResult!!
        assertTrue(result.isVerified)
        assertEquals(1, result.spamScore)
        assertTrue(result.callerName.contains("Bank BCA"))
        assertEquals("LOW_RISK_ENTERPRISE", result.osintRiskFactor)
        assertTrue(result.osintProfile.contains("OSINT Clean"))
    }

    @Test
    fun testPogaPhoneLookup_SpamPinjolNumber_ReturnsCriticalSpamRisk() = runTest {
        val fakeSecurityRepo = FakeSecurityRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val analyzePermissionUseCase = AnalyzePermissionUseCase(fakeSecurityRepo)
        val notificationHelper = NotificationHelper(SecurityTestContext())

        val viewModel = SecurityViewModel(
            analyzePermissionUseCase = analyzePermissionUseCase,
            healthRepository = fakeHealthRepo,
            notificationHelper = notificationHelper
        )

        testScheduler.advanceUntilIdle()

        // Input Spam Pinjol DC number
        viewModel.updateCallerInput("081299887711")
        viewModel.executeCallerLookup()

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.callerResult)

        val result = state.callerResult!!
        assertFalse(result.isVerified)
        assertTrue(result.spamScore > 90)
        assertEquals("CRITICAL_SPAM_RISK", result.osintRiskFactor)
        assertTrue(result.communityTags.contains("#DebtCollectorAbal"))
    }

    @Test
    fun testSelectTab_UpdatesSelectedTabState() = runTest {
        val fakeSecurityRepo = FakeSecurityRepository()
        val fakeHealthRepo = FakeHealthRepository()
        val analyzePermissionUseCase = AnalyzePermissionUseCase(fakeSecurityRepo)
        val notificationHelper = NotificationHelper(SecurityTestContext())

        val viewModel = SecurityViewModel(
            analyzePermissionUseCase = analyzePermissionUseCase,
            healthRepository = fakeHealthRepo,
            notificationHelper = notificationHelper
        )

        testScheduler.advanceUntilIdle()

        viewModel.selectTab(2) // Caller ID (PogaPhone OSINT) tab
        assertEquals(2, viewModel.uiState.value.selectedTab)
    }
}
