package com.jelyta.deviceguardian.presentation.security

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.domain.model.AppSecurityInfo
import com.jelyta.deviceguardian.domain.model.RiskLevel
import com.jelyta.deviceguardian.domain.usecase.AnalyzePermissionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SecurityUiState(
    val apps: List<AppSecurityInfo> = emptyList(),
    val isScanning: Boolean = false,
    val securityScore: Int = 95
)

class SecurityViewModel(
    private val analyzePermissionUseCase: AnalyzePermissionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        runScan()
    }

    fun runScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            val appList = analyzePermissionUseCase()
            val highRiskCount = appList.count { it.riskLevel == RiskLevel.HIGH }
            val score = (100 - (highRiskCount * 5)).coerceAtLeast(60)

            _uiState.update {
                it.copy(
                    apps = appList,
                    isScanning = false,
                    securityScore = score
                )
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return SecurityViewModel(
                analyzePermissionUseCase = container.useCaseModule.analyzePermissionUseCase
            ) as T
        }
    }
}
