package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.ChatMessage
import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import com.jelyta.deviceguardian.domain.repository.AssistantRepository
import kotlinx.coroutines.flow.Flow

class ChatAssistantUseCase(
    private val assistantRepository: AssistantRepository
) {
    suspend operator fun invoke(query: String, context: DeviceMetrics): String {
        val userMsg = ChatMessage(content = query, isUser = true)
        assistantRepository.saveChatMessage(userMsg)

        val answer = assistantRepository.sendMessage(query, context)
        val aiMsg = ChatMessage(content = answer, isUser = false)
        assistantRepository.saveChatMessage(aiMsg)

        return answer
    }

    fun getChatHistory(): Flow<List<ChatMessage>> = assistantRepository.getChatHistory()
}
