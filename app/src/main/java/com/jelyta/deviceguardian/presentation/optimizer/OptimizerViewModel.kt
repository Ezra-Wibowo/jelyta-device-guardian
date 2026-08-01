package com.jelyta.deviceguardian.presentation.optimizer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.OptimizationLog
import com.jelyta.deviceguardian.domain.repository.HealthRepository
import com.jelyta.deviceguardian.domain.usecase.OptimizeDeviceUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OptimizerUiState(
    val logs: List<OptimizationLog> = emptyList(),
    val isOptimizing: Boolean = false,
    val lastMessage: String? = null
)

class OptimizerViewModel(
    private val optimizeDeviceUseCase: OptimizeDeviceUseCase,
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptimizerUiState())
    val uiState: StateFlow<OptimizerUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            healthRepository.getOptimizationLogs().collect { logList ->
                _uiState.update { it.copy(logs = logList) }
            }
        }
    }

    fun runTurboBoost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizing = true) }
            val log = optimizeDeviceUseCase.runTurboBoost()
            _uiState.update {
                it.copy(
                    isOptimizing = false,
                    lastMessage = "Turbo Boost Freed ${log.reclaimedMemoryMb} MB of RAM."
                )
            }
        }
    }

    fun runCacheClean() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizing = true) }
            val log = optimizeDeviceUseCase.runCacheClean()
            _uiState.update {
                it.copy(
                    isOptimizing = false,
                    lastMessage = "Cache Cleaned ${log.reclaimedMemoryMb} MB."
                )
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return OptimizerViewModel(
                optimizeDeviceUseCase = container.useCaseModule.optimizeDeviceUseCase,
                healthRepository = container.repositoryModule.healthRepository
            ) as T
        }
    }
}
