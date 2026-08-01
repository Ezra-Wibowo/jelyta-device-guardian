package com.jelyta.deviceguardian.presentation.hardware

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.HardwareSnapshot
import com.jelyta.deviceguardian.domain.usecase.GetHardwareSnapshotUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HardwareDashboardUiState(
    val snapshot: HardwareSnapshot? = null,
    val isLoading: Boolean = true,
    val selectedCategoryIndex: Int = 0,
    val lastRefreshTime: Long = System.currentTimeMillis()
)

class HardwareDashboardViewModel(
    private val getHardwareSnapshotUseCase: GetHardwareSnapshotUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HardwareDashboardUiState())
    val uiState: StateFlow<HardwareDashboardUiState> = _uiState.asStateFlow()

    init {
        observeHardwareData()
    }

    private fun observeHardwareData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getHardwareSnapshotUseCase.observeHardwareSnapshot()
                .catch {
                    val single = getHardwareSnapshotUseCase.getSingleSnapshot()
                    _uiState.update { state -> state.copy(snapshot = single, isLoading = false) }
                }
                .collect { snapshot ->
                    _uiState.update { state ->
                        state.copy(
                            snapshot = snapshot,
                            isLoading = false,
                            lastRefreshTime = System.currentTimeMillis()
                        )
                    }
                }
        }
    }

    fun selectCategory(index: Int) {
        _uiState.update { it.copy(selectedCategoryIndex = index) }
    }

    fun refreshManually() {
        val snapshot = getHardwareSnapshotUseCase.getSingleSnapshot()
        _uiState.update {
            it.copy(
                snapshot = snapshot,
                lastRefreshTime = System.currentTimeMillis()
            )
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return HardwareDashboardViewModel(
                getHardwareSnapshotUseCase = container.useCaseModule.getHardwareSnapshotUseCase
            ) as T
        }
    }
}
