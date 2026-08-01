package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.repository.AssistantRepository

class TranslateTextUseCase(
    private val assistantRepository: AssistantRepository
) {
    suspend operator fun invoke(text: String, targetLang: String): String {
        return assistantRepository.translateText(text, targetLang)
    }
}
