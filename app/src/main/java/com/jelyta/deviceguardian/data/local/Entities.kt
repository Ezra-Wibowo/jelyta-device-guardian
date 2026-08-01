package com.jelyta.deviceguardian.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jelyta.deviceguardian.domain.model.ChatMessage
import com.jelyta.deviceguardian.domain.model.HealthReport
import com.jelyta.deviceguardian.domain.model.OptimizationLog

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long
) {
    fun toDomain() = ChatMessage(id, content, isUser, timestamp)
    companion object {
        fun fromDomain(model: ChatMessage) = ChatMessageEntity(model.id, model.content, model.isUser, model.timestamp)
    }
}

@Entity(tableName = "health_reports")
data class HealthReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val statusText: String,
    val keyIssuesJson: String,
    val recommendationsJson: String,
    val timestamp: Long
) {
    fun toDomain() = HealthReport(
        id = id,
        score = score,
        statusText = statusText,
        keyIssues = keyIssuesJson.split("||").filter { it.isNotBlank() },
        recommendations = recommendationsJson.split("||").filter { it.isNotBlank() },
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(model: HealthReport) = HealthReportEntity(
            id = model.id,
            score = model.score,
            statusText = model.statusText,
            keyIssuesJson = model.keyIssues.joinToString("||"),
            recommendationsJson = model.recommendations.joinToString("||"),
            timestamp = model.timestamp
        )
    }
}

@Entity(tableName = "optimization_logs")
data class OptimizationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionName: String,
    val reclaimedMemoryMb: Int,
    val healthScoreImpact: Int,
    val timestamp: Long
) {
    fun toDomain() = OptimizationLog(id, actionName, reclaimedMemoryMb, healthScoreImpact, timestamp)
    companion object {
        fun fromDomain(model: OptimizationLog) = OptimizationLogEntity(
            id = model.id,
            actionName = model.actionName,
            reclaimedMemoryMb = model.reclaimedMemoryMb,
            healthScoreImpact = model.healthScoreImpact,
            timestamp = model.timestamp
        )
    }
}
