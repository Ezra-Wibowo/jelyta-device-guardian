package com.jelyta.deviceguardian.domain.usecase

import com.jelyta.deviceguardian.domain.model.AppSecurityInfo
import com.jelyta.deviceguardian.domain.repository.SecurityRepository

class AnalyzePermissionUseCase(private val securityRepository: SecurityRepository) {
    suspend operator fun invoke(): List<AppSecurityInfo> {
        return securityRepository.scanInstalledApps()
    }
}
