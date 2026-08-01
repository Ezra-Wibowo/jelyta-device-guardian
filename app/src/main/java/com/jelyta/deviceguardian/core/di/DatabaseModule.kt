package com.jelyta.deviceguardian.core.di

import android.content.Context
import com.jelyta.deviceguardian.data.local.GuardianDatabase

class DatabaseModule(private val context: Context) {
    val database: GuardianDatabase by lazy {
        GuardianDatabase.getDatabase(context)
    }

    val chatMessageDao by lazy { database.chatMessageDao() }
    val healthReportDao by lazy { database.healthReportDao() }
    val optimizationLogDao by lazy { database.optimizationLogDao() }
    val securityIncidentDao by lazy { database.securityIncidentDao() }
    val digitalEvidenceDao by lazy { database.digitalEvidenceDao() }
    val iocItemDao by lazy { database.iocItemDao() }
    val assetItemDao by lazy { database.assetItemDao() }
    val auditLogDao by lazy { database.auditLogDao() }
    val threatItemDao by lazy { database.threatItemDao() }
}
