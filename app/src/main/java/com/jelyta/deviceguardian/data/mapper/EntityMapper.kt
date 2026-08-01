package com.jelyta.deviceguardian.data.mapper

import com.jelyta.deviceguardian.data.local.ChatMessageEntity
import com.jelyta.deviceguardian.data.local.HealthReportEntity
import com.jelyta.deviceguardian.data.local.OptimizationLogEntity
import com.jelyta.deviceguardian.domain.model.ChatMessage
import com.jelyta.deviceguardian.domain.model.HealthReport
import com.jelyta.deviceguardian.domain.model.OptimizationLog

object EntityMapper {
    fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
        id = id,
        content = content,
        isUser = isUser,
        timestamp = timestamp
    )

    fun ChatMessage.toEntity(): ChatMessageEntity = ChatMessageEntity(
        id = id,
        content = content,
        isUser = isUser,
        timestamp = timestamp
    )

    fun OptimizationLogEntity.toDomain(): OptimizationLog = OptimizationLog(
        id = id,
        actionName = actionName,
        reclaimedMemoryMb = reclaimedMemoryMb,
        timestamp = timestamp,
        healthScoreImpact = healthScoreImpact
    )

    fun OptimizationLog.toEntity(): OptimizationLogEntity = OptimizationLogEntity(
        id = id,
        actionName = actionName,
        reclaimedMemoryMb = reclaimedMemoryMb,
        timestamp = timestamp,
        healthScoreImpact = healthScoreImpact
    )

    fun HealthReportEntity.toDomain(): HealthReport = HealthReport(
        id = id,
        score = score,
        statusText = statusText,
        keyIssues = keyIssuesJson.split("||").filter { it.isNotBlank() },
        recommendations = recommendationsJson.split("||").filter { it.isNotBlank() },
        timestamp = timestamp
    )

    fun HealthReport.toEntity(): HealthReportEntity = HealthReportEntity(
        id = id,
        score = score,
        statusText = statusText,
        keyIssuesJson = keyIssues.joinToString("||"),
        recommendationsJson = recommendations.joinToString("||"),
        timestamp = timestamp
    )
}
