package com.jelyta.deviceguardian.core.di

import com.jelyta.deviceguardian.data.remote.GeminiService

class AiModule {
    val geminiService: GeminiService by lazy {
        GeminiService()
    }
}
