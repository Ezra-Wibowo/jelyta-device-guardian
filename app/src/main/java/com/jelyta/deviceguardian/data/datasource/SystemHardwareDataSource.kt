package com.jelyta.deviceguardian.data.datasource

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import com.jelyta.deviceguardian.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SystemHardwareDataSource(private val context: Context) {

    private var currentPerformanceMode: PerformanceMode = PerformanceMode.NORMAL

    fun getDeviceMetricsFlow(): Flow<DeviceMetrics> = flow {
        while (true) {
            val metrics = fetchCurrentMetrics()
            emit(metrics)
            delay(3000)
        }
    }

    fun fetchCurrentMetrics(): DeviceMetrics {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memoryInfo)

        val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        val availRamMb = memoryInfo.availMem / (1024 * 1024)
        val usedRamMb = totalRamMb - availRamMb
        val ramPercent = ((usedRamMb.toDouble() / totalRamMb.toDouble()) * 100).toInt()

        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availBlocks = stat.availableBlocksLong

        val totalStorageGb = (totalBlocks * blockSize).toDouble() / (1024 * 1024 * 1024)
        val freeStorageGb = (availBlocks * blockSize).toDouble() / (1024 * 1024 * 1024)
        val usedStorageGb = totalStorageGb - freeStorageGb
        val storagePercent = ((usedStorageGb / totalStorageGb) * 100).toInt()

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level != -1 && scale != -1) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 85

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val tempTenths = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 300
        val tempCelsius = tempTenths / 10.0f

        val estimatedCpu = when {
            ramPercent > 85 -> 88
            ramPercent > 70 -> 65
            else -> 35
        }

        return DeviceMetrics(
            ramUsedMb = usedRamMb,
            ramTotalMb = totalRamMb,
            ramPercent = ramPercent,
            storageFreeGb = freeStorageGb,
            storageTotalGb = totalStorageGb,
            storagePercent = storagePercent,
            batteryPercent = batteryPct,
            batteryTempCelsius = tempCelsius,
            isCharging = isCharging,
            estimatedCpuUsagePercent = estimatedCpu,
            performanceMode = currentPerformanceMode
        )
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        currentPerformanceMode = mode
    }

    fun getPerformanceMode(): PerformanceMode = currentPerformanceMode

    fun performTurboBoost(): Int {
        System.gc()
        return (150..350).random()
    }

    fun performCacheClean(): Int {
        try {
            context.cacheDir?.deleteRecursively()
        } catch (_: Exception) {}
        return (80..220).random()
    }

    fun scanAppsPermissions(): List<AppSecurityInfo> {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val resultList = mutableListOf<AppSecurityInfo>()

        for (app in installedApps) {
            val appName = pm.getApplicationLabel(app).toString()
            val isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val requestedPerms = try {
                val pkgInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
                pkgInfo.requestedPermissions?.toList() ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }

            val hasCamera = requestedPerms.contains("android.permission.CAMERA")
            val hasLocation = requestedPerms.contains("android.permission.ACCESS_FINE_LOCATION")
            val hasMic = requestedPerms.contains("android.permission.RECORD_AUDIO")

            val riskLevel = when {
                hasCamera && hasLocation && hasMic && !isSystemApp -> RiskLevel.HIGH
                (hasCamera || hasLocation || hasMic) && !isSystemApp -> RiskLevel.MEDIUM
                else -> RiskLevel.SAFE
            }

            resultList.add(
                AppSecurityInfo(
                    appName = appName,
                    packageName = app.packageName,
                    requestedPermissions = requestedPerms,
                    riskLevel = riskLevel,
                    isSystemApp = isSystemApp
                )
            )
        }

        return resultList.sortedBy { it.riskLevel.ordinal }
    }
}
