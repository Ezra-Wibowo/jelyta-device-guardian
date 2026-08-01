package com.jelyta.deviceguardian.core.di

import com.jelyta.deviceguardian.data.repository.*
import com.jelyta.deviceguardian.domain.repository.*

class RepositoryModule(
    private val appModule: AppModule,
    private val databaseModule: DatabaseModule,
    private val networkModule: NetworkModule,
    private val aiModule: AiModule
) {
    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(appModule.hardwareDataSource)
    }

    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(appModule.hardwareDataSource)
    }

    val assistantRepository: AssistantRepository by lazy {
        AssistantRepositoryImpl(aiModule.geminiService, databaseModule.chatMessageDao)
    }

    val cloudRepository: CloudRepository by lazy {
        CloudRepositoryImpl(networkModule.fastApiService)
    }

    val healthRepository: HealthRepository by lazy {
        HealthRepositoryImpl(databaseModule.healthReportDao, databaseModule.optimizationLogDao)
    }
}
