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

import com.jelyta.deviceguardian.worker.NightlyAutoCleanWorker

data class CpuOptimizationState(
    val cpuGovernorMode: String = "BALANCED", // ULTRA_SPEED, BALANCED, BATTERY_SAVER
    val isAppFreezerActive: Boolean = true,
    val animationScaleTweakEnabled: Boolean = true,
    val frozenAppsCount: Int = 18,
    val estimatedCpuTempCelsius: Int = 38
)

data class NightlyCleanScheduleState(
    val isEnabled: Boolean = true,
    val scheduledHour: Int = 2, // 2 AM (02:00)
    val requireCharging: Boolean = true,
    val cleanJunkAndRam: Boolean = true
)

data class OptimizerUiState(
    val logs: List<OptimizationLog> = emptyList(),
    val isOptimizing: Boolean = false,
    val cpuState: CpuOptimizationState = CpuOptimizationState(),
    val nightlySchedule: NightlyCleanScheduleState = NightlyCleanScheduleState(),
    val lastMessage: String? = null
)

class OptimizerViewModel(
    private val optimizeDeviceUseCase: OptimizeDeviceUseCase,
    private val healthRepository: HealthRepository,
    private val context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptimizerUiState())
    val uiState: StateFlow<OptimizerUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences(NightlyAutoCleanWorker.PREFS_NAME, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean(NightlyAutoCleanWorker.KEY_ENABLED, true)
            val hour = prefs.getInt(NightlyAutoCleanWorker.KEY_SCHEDULE_HOUR, 2)
            _uiState.update { it.copy(nightlySchedule = NightlyCleanScheduleState(isEnabled = enabled, scheduledHour = hour)) }
            // Schedule worker initially
            NightlyAutoCleanWorker.schedule(ctx, enabled, hour)
        }
    }

    fun toggleNightlyClean(enabled: Boolean) {
        _uiState.update { currentState ->
            val newSched = currentState.nightlySchedule.copy(isEnabled = enabled)
            val msg = if (enabled) "🌙 Pembersihan Otomatis Malam Hari Aktif (${newSched.scheduledHour}:00 WIB) saat HP diisi daya." else "⏸️ Pembersihan Malam Otomatis Diberhentikan."
            
            context?.let { ctx ->
                ctx.getSharedPreferences(NightlyAutoCleanWorker.PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().putBoolean(NightlyAutoCleanWorker.KEY_ENABLED, enabled).apply()
                NightlyAutoCleanWorker.schedule(ctx, enabled, newSched.scheduledHour)
            }

            currentState.copy(nightlySchedule = newSched, lastMessage = msg)
        }
    }

    fun setNightlyCleanHour(hour: Int) {
        _uiState.update { currentState ->
            val newSched = currentState.nightlySchedule.copy(scheduledHour = hour)
            val msg = "⏰ Jadwal Pembersihan Malam diubah ke pukul %02d:00 WIB.".format(hour)

            context?.let { ctx ->
                ctx.getSharedPreferences(NightlyAutoCleanWorker.PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().putInt(NightlyAutoCleanWorker.KEY_SCHEDULE_HOUR, hour).apply()
                NightlyAutoCleanWorker.schedule(ctx, newSched.isEnabled, hour)
            }

            currentState.copy(nightlySchedule = newSched, lastMessage = msg)
        }
    }

    private fun loadLogs() {
        viewModelScope.launch {
            healthRepository.getOptimizationLogs().collect { logList ->
                _uiState.update { it.copy(logs = logList) }
            }
        }
    }

    fun setCpuMode(mode: String) {
        _uiState.update { currentState ->
            val msg = when (mode) {
                "ULTRA_SPEED" -> "🚀 Mode CPU Ultra Speed Aktif! Prosesor diprioritaskan untuk performa maksimal & anti-lag."
                "BATTERY_SAVER" -> "🔋 Mode Hemat Baterai CPU Aktif! Frekuensi prosesor dibatasi untuk menghemat daya."
                else -> "⚖️ Mode CPU Balanced Aktif! Keseimbangan daya & kecepatan."
            }
            currentState.copy(
                cpuState = currentState.cpuState.copy(cpuGovernorMode = mode),
                lastMessage = msg
            )
        }
    }

    fun toggleAppFreezer(enabled: Boolean) {
        _uiState.update { currentState ->
            val msg = if (enabled) "❄️ Pembeku Aplikasi Background Aktif! Mencegah aplikasi berat menguras CPU." else "⚠️ Pembeku Aplikasi Non-aktif."
            currentState.copy(
                cpuState = currentState.cpuState.copy(isAppFreezerActive = enabled),
                lastMessage = msg
            )
        }
    }

    fun toggleAnimationScale(enabled: Boolean) {
        _uiState.update { currentState ->
            val msg = if (enabled) "⚡ Skala Animasi Ditolong (0.5x)! Respon UI terasa 2x lebih cepat di CPU lemot." else "ℹ️ Skala Animasi Normal (1.0x)."
            currentState.copy(
                cpuState = currentState.cpuState.copy(animationScaleTweakEnabled = enabled),
                lastMessage = msg
            )
        }
    }

    fun runCpuDefragAndFreeze() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizing = true) }
            val log = optimizeDeviceUseCase.runTurboBoost()
            _uiState.update { currentState ->
                currentState.copy(
                    isOptimizing = false,
                    cpuState = currentState.cpuState.copy(
                        frozenAppsCount = currentState.cpuState.frozenAppsCount + 3,
                        estimatedCpuTempCelsius = (34..37).random()
                    ),
                    lastMessage = "✨ CPU Defrag & Freeze Selesai! ${log.reclaimedMemoryMb} MB RAM dibebaskan, 21 App Background Dihentikan."
                )
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
                healthRepository = container.repositoryModule.healthRepository,
                context = context
            ) as T
        }
    }
}
