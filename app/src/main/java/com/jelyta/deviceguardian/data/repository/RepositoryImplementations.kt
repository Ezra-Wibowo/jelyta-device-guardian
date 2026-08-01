package com.jelyta.deviceguardian.data.repository

import com.jelyta.deviceguardian.data.datasource.SystemHardwareDataSource
import com.jelyta.deviceguardian.data.local.*
import com.jelyta.deviceguardian.data.remote.*
import com.jelyta.deviceguardian.domain.model.*
import com.jelyta.deviceguardian.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepositoryImpl(
    private val hardwareDataSource: SystemHardwareDataSource
) : DeviceRepository {
    override fun getDeviceMetrics(): Flow<DeviceMetrics> = hardwareDataSource.getDeviceMetricsFlow()
    override suspend fun setPerformanceMode(mode: PerformanceMode) = hardwareDataSource.setPerformanceMode(mode)
    override suspend fun getPerformanceMode(): PerformanceMode = hardwareDataSource.getPerformanceMode()
    override suspend fun performTurboBoost(): Int = hardwareDataSource.performTurboBoost()
    override suspend fun performCacheClean(): Int = hardwareDataSource.performCacheClean()
}

class SecurityRepositoryImpl(
    private val hardwareDataSource: SystemHardwareDataSource
) : SecurityRepository {
    override suspend fun scanInstalledApps(): List<AppSecurityInfo> = hardwareDataSource.scanAppsPermissions()
}

class AssistantRepositoryImpl(
    private val geminiService: GeminiService,
    private val chatMessageDao: ChatMessageDao
) : AssistantRepository {
    override suspend fun sendMessage(query: String, context: DeviceMetrics): String {
        return geminiService.queryAssistant(query, context)
    }

    override suspend fun translateText(text: String, targetLanguage: String): String {
        return geminiService.translateText(text, targetLanguage)
    }

    override fun getChatHistory(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessages().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(ChatMessageEntity.fromDomain(message))
    }
}

class CloudRepositoryImpl(
    private val fastApiService: FastApiService
) : CloudRepository {
    override suspend fun syncDeviceMetrics(metrics: DeviceMetrics, healthScore: Int): Result<String> {
        return try {
            val request = FastApiDeviceSyncRequest(
                deviceId = "GUARDIAN-DEV-001",
                healthScore = healthScore,
                cpuUsage = metrics.estimatedCpuUsagePercent,
                ramUsage = metrics.ramPercent,
                storageUsage = metrics.storagePercent,
                batteryPercent = metrics.batteryPercent,
                batteryTemp = metrics.batteryTempCelsius,
                performanceMode = metrics.performanceMode.name
            )
            val response = fastApiService.syncDeviceMetrics(request)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class HealthRepositoryImpl(
    private val healthReportDao: HealthReportDao,
    private val optimizationLogDao: OptimizationLogDao
) : HealthRepository {
    override suspend fun saveHealthReport(report: HealthReport) {
        healthReportDao.insertReport(HealthReportEntity.fromDomain(report))
    }

    override fun getHealthReports(): Flow<List<HealthReport>> {
        return healthReportDao.getAllReports().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getOptimizationLogs(): Flow<List<OptimizationLog>> {
        return optimizationLogDao.getAllLogs().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveOptimizationLog(log: OptimizationLog) {
        optimizationLogDao.insertLog(OptimizationLogEntity.fromDomain(log))
    }
}
