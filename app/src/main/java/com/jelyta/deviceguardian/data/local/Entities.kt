package com.jelyta.deviceguardian.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jelyta.deviceguardian.domain.model.*

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

@Entity(tableName = "cyber_incidents")
data class SecurityIncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val severity: String,
    val description: String,
    val timestamp: Long
) {
    fun toDomain() = CyberIncident(id, title, severity, description, timestamp)
    companion object {
        fun fromDomain(m: CyberIncident) = SecurityIncidentEntity(m.id, m.title, m.severity, m.description, m.timestamp)
    }
}

@Entity(tableName = "digital_evidences")
data class DigitalEvidenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val artifactName: String,
    val artifactType: String,
    val hashSha256: String,
    val timestamp: Long
) {
    fun toDomain() = DigitalEvidence(id, artifactName, artifactType, hashSha256, timestamp)
    companion object {
        fun fromDomain(m: DigitalEvidence) = DigitalEvidenceEntity(m.id, m.artifactName, m.artifactType, m.hashSha256, m.timestamp)
    }
}

@Entity(tableName = "ioc_items")
data class IocItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val indicatorType: String,
    val indicatorValue: String,
    val threatCategory: String,
    val timestamp: Long
) {
    fun toDomain() = IocItem(id, indicatorType, indicatorValue, threatCategory, timestamp)
    companion object {
        fun fromDomain(m: IocItem) = IocItemEntity(m.id, m.indicatorType, m.indicatorValue, m.threatCategory, m.timestamp)
    }
}

@Entity(tableName = "asset_items")
data class AssetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetName: String,
    val assetType: String,
    val status: String,
    val timestamp: Long
) {
    fun toDomain() = AssetItem(id, assetName, assetType, status, timestamp)
    companion object {
        fun fromDomain(m: AssetItem) = AssetItemEntity(m.id, m.assetName, m.assetType, m.status, m.timestamp)
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val auditTitle: String,
    val outcome: String,
    val details: String,
    val timestamp: Long
) {
    fun toDomain() = AuditLogItem(id, auditTitle, outcome, details, timestamp)
    companion object {
        fun fromDomain(m: AuditLogItem) = AuditLogItemEntity(m.id, m.auditTitle, m.outcome, m.details, m.timestamp)
    }
}

@Entity(tableName = "threat_items")
data class ThreatItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threatName: String,
    val riskLevel: String,
    val status: String,
    val timestamp: Long
) {
    fun toDomain() = ThreatItem(id, threatName, riskLevel, status, timestamp)
    companion object {
        fun fromDomain(m: ThreatItem) = ThreatItemEntity(m.id, m.threatName, m.riskLevel, m.status, m.timestamp)
    }
}
