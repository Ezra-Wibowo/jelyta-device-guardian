package com.jelyta.deviceguardian.data.datasource.hardware

import android.app.ActivityManager
import android.content.Context
import com.jelyta.deviceguardian.domain.model.MemoryHardwareInfo

class MemoryMonitor(private val context: Context) {

    fun getMemoryInfo(): MemoryHardwareInfo {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)

            val total = memInfo.totalMem
            val avail = memInfo.availMem
            val used = total - avail
            val usagePct = if (total > 0) (((used.toDouble() / total.toDouble())) * 100).toInt() else 0

            val bytesInMb = 1024L * 1024L

            MemoryHardwareInfo(
                totalMemoryBytes = total,
                availMemoryBytes = avail,
                usedMemoryBytes = used,
                usagePercentage = usagePct,
                isLowMemory = memInfo.lowMemory,
                totalMb = total / bytesInMb,
                usedMb = used / bytesInMb,
                availMb = avail / bytesInMb
            )
        } catch (e: Exception) {
            MemoryHardwareInfo(
                totalMemoryBytes = 4096L * 1024 * 1024,
                availMemoryBytes = 2048L * 1024 * 1024,
                usedMemoryBytes = 2048L * 1024 * 1024,
                usagePercentage = 50,
                isLowMemory = false,
                totalMb = 4096,
                usedMb = 2048,
                availMb = 2048
            )
        }
    }
}
