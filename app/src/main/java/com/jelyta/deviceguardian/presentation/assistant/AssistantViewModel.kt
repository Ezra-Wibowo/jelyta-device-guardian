package com.jelyta.deviceguardian.presentation.assistant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jelyta.deviceguardian.core.di.AppContainer
import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.domain.model.ChatMessage
import com.jelyta.deviceguardian.domain.usecase.ChatAssistantUseCase
import com.jelyta.deviceguardian.domain.usecase.TranslateTextUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val translationResult: String? = null
)

class AssistantViewModel(
    private val chatAssistantUseCase: ChatAssistantUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val hardwareDataSource: SystemHardwareDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            chatAssistantUseCase.getChatHistory().collect { chatList ->
                if (chatList.isEmpty()) {
                    val defaultGreeting = ChatMessage(
                        content = "👋 **Halo! Saya Jelyta Guardian AI Assistant.**\nBagaimana kondisi HP Anda hari ini? Ketik pertanyaan atau pilih topik rekomendasi di bawah untuk menganalisis RAM, baterai, dan keamanan!",
                        isUser = false
                    )
                    _uiState.update { it.copy(messages = listOf(defaultGreeting)) }
                } else {
                    _uiState.update { it.copy(messages = chatList) }
                }
            }
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val metrics = hardwareDataSource.fetchCurrentMetrics()
            chatAssistantUseCase(query, metrics)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun translateText(text: String, targetLang: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = translateTextUseCase(text, targetLang)
            _uiState.update { it.copy(translationResult = result, isLoading = false) }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatAssistantUseCase.clearChatHistory()
            val defaultGreeting = ChatMessage(
                content = "👋 **Riwayat chat dibersihkan.**\nHalo! Saya Jelyta Guardian AI Assistant. Ada yang bisa saya bantu dengan HP Anda?",
                isUser = false
            )
            _uiState.update { it.copy(messages = listOf(defaultGreeting)) }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = AppContainer(context)
            return AssistantViewModel(
                chatAssistantUseCase = container.useCaseModule.chatAssistantUseCase,
                translateTextUseCase = container.useCaseModule.translateTextUseCase,
                hardwareDataSource = container.appModule.hardwareDataSource
            ) as T
        }
    }
}
