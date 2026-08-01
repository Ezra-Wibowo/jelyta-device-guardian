package com.jelyta.deviceguardian.presentation.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.*
import com.jelyta.deviceguardian.domain.repository.DeviceRepository
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.usecase.OptimizeDeviceUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val metrics: DeviceMetrics? = null,
    val healthReport: HealthReport? = null,
    val isHealing: Boolean = false,
    val toastMessage: String? = null
)

class DashboardViewModel(
    private val deviceRepository: DeviceRepository,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase,
    private val optimizeDeviceUseCase: OptimizeDeviceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDeviceMetrics()
    }

    private fun observeDeviceMetrics() {
        viewModelScope.launch {
            deviceRepository.getDeviceMetrics().collect { metrics ->
                val report = calculateHealthScoreUseCase(metrics)
                _uiState.update { it.copy(metrics = metrics, healthReport = report) }
            }
        }
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        viewModelScope.launch {
            deviceRepository.setPerformanceMode(mode)
        }
    }

    fun runTurboBoost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val log = optimizeDeviceUseCase.runTurboBoost()
            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Turbo Boost Complete! ${log.reclaimedMemoryMb} MB memory freed."
                )
            }
        }
    }

    fun runSelfHealing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealing = true) }
            val log = optimizeDeviceUseCase.runCacheClean()
            _uiState.update {
                it.copy(
                    isHealing = false,
                    toastMessage = "Self-Healing Diagnostic Complete! System cache cleared (${log.reclaimedMemoryMb} MB)."
                )
            }
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return DashboardViewModel(
                deviceRepository = container.repositoryModule.deviceRepository,
                calculateHealthScoreUseCase = container.useCaseModule.calculateHealthScoreUseCase,
                optimizeDeviceUseCase = container.useCaseModule.optimizeDeviceUseCase
            ) as T
        }
    }
}
