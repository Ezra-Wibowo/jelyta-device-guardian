package com.jelyta.deviceguardian.core.di

import com.jelyta.deviceguardian.domain.usecase.*

class UseCaseModule(
    private val repositoryModule: RepositoryModule,
    private val appModule: AppModule
) {
    val calculateHealthScoreUseCase by lazy { CalculateHealthScoreUseCase() }
    val analyzeBatteryUseCase by lazy { AnalyzeBatteryUseCase() }
    val analyzeRamUseCase by lazy { AnalyzeRamUseCase() }
    val analyzeStorageUseCase by lazy { AnalyzeStorageUseCase() }
    val analyzePermissionUseCase by lazy { AnalyzePermissionUseCase(repositoryModule.securityRepository) }
    val optimizeDeviceUseCase by lazy { OptimizeDeviceUseCase(repositoryModule.deviceRepository, repositoryModule.healthRepository) }
    val chatAssistantUseCase by lazy { ChatAssistantUseCase(repositoryModule.assistantRepository) }
    val translateTextUseCase by lazy { TranslateTextUseCase(repositoryModule.assistantRepository) }
    val cloudSyncUseCase by lazy { CloudSyncUseCase(repositoryModule.cloudRepository) }
    val getBatteryStatusUseCase by lazy { GetBatteryStatusUseCase(appModule.batteryMonitorService) }
    val getHardwareSnapshotUseCase by lazy { GetHardwareSnapshotUseCase(appModule.hardwareMonitorManager) }
}
