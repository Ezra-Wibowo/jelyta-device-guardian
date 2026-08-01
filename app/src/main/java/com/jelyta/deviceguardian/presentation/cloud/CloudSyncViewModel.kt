package com.jelyta.deviceguardian.presentation.cloud

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.domain.usecase.CalculateHealthScoreUseCase
import com.jelyta.deviceguardian.domain.usecase.CloudSyncUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CloudSyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncStatus: String? = null,
    val isSuccess: Boolean = false
)

class CloudSyncViewModel(
    private val cloudSyncUseCase: CloudSyncUseCase,
    private val hardwareDataSource: SystemHardwareDataSource,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CloudSyncUiState())
    val uiState: StateFlow<CloudSyncUiState> = _uiState.asStateFlow()

    fun performSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val metrics = hardwareDataSource.fetchCurrentMetrics()
            val healthReport = calculateHealthScoreUseCase(metrics)

            val result = cloudSyncUseCase(metrics, healthReport.score)
            result.onSuccess { msg ->
                _uiState.update {
                    it.copy(isSyncing = false, lastSyncStatus = msg, isSuccess = true)
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        lastSyncStatus = "Cloud Sync Notice: ${err.localizedMessage ?: "FastAPI server unreachable (10.0.2.2:8000)."}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return CloudSyncViewModel(
                cloudSyncUseCase = container.useCaseModule.cloudSyncUseCase,
                hardwareDataSource = container.appModule.hardwareDataSource,
                calculateHealthScoreUseCase = container.useCaseModule.calculateHealthScoreUseCase
            ) as T
        }
    }
}
