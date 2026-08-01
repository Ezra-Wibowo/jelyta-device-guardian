package com.jelyta.deviceguardian.presentation.battery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.BatteryInfo
import com.jelyta.deviceguardian.domain.usecase.GetBatteryStatusUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BatteryMonitorUiState(
    val batteryInfo: BatteryInfo? = null,
    val isOverheating: Boolean = false,
    val warningMessage: String? = null,
    val isMonitoring: Boolean = true,
    val batteryHistory: List<BatteryInfo> = emptyList()
)

class BatteryMonitorViewModel(
    private val getBatteryStatusUseCase: GetBatteryStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatteryMonitorUiState())
    val uiState: StateFlow<BatteryMonitorUiState> = _uiState.asStateFlow()

    init {
        startRealtimeMonitoring()
    }

    fun startRealtimeMonitoring() {
        viewModelScope.launch {
            _uiState.update { it.copy(isMonitoring = true) }
            getBatteryStatusUseCase.observeBatteryStatus()
                .catch { e ->
                    _uiState.update { it.copy(warningMessage = "Battery monitor error: ${e.message}") }
                }
                .collect { info ->
                    val isOverheat = info.temperatureCelsius > 40.0f
                    val warning = when {
                        isOverheat -> "Battery temperature high (${info.temperatureCelsius}°C)! Consider disconnecting charger or reducing CPU load."
                        info.batteryPercent <= 15 && !info.isCharging -> "Battery critical (${info.batteryPercent}%). Connect charger."
                        else -> null
                    }
                    _uiState.update { state ->
                        val updatedHistory = (state.batteryHistory + info).takeLast(30)
                        state.copy(
                            batteryInfo = info,
                            isOverheating = isOverheat,
                            warningMessage = warning,
                            batteryHistory = updatedHistory
                        )
                    }
                }
        }
    }

    fun refreshBatteryInfo() {
        val currentInfo = getBatteryStatusUseCase.getCurrentBatteryInfo()
        val isOverheat = currentInfo.temperatureCelsius > 40.0f
        val warning = when {
            isOverheat -> "Battery temperature high (${currentInfo.temperatureCelsius}°C)!"
            currentInfo.batteryPercent <= 15 && !currentInfo.isCharging -> "Battery critical (${currentInfo.batteryPercent}%)."
            else -> null
        }
        _uiState.update { state ->
            state.copy(
                batteryInfo = currentInfo,
                isOverheating = isOverheat,
                warningMessage = warning
            )
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return BatteryMonitorViewModel(
                getBatteryStatusUseCase = container.useCaseModule.getBatteryStatusUseCase
            ) as T
        }
    }
}
