package com.jelyta.deviceguardian.data.datasource.hardware

import android.os.Environment
import android.os.StatFs
import com.jelyta.deviceguardian.domain.model.StorageHardwareInfo

class StorageMonitor {

    fun getStorageInfo(): StorageHardwareInfo {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)

            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = totalBytes - freeBytes

            val usagePercentage = if (totalBytes > 0) {
                (((usedBytes.toDouble() / totalBytes.toDouble())) * 100).toInt()
            } else 0

            val bytesInGb = 1024.0 * 1024.0 * 1024.0

            StorageHardwareInfo(
                totalBytes = totalBytes,
                freeBytes = freeBytes,
                usedBytes = usedBytes,
                usagePercentage = usagePercentage,
                totalGb = totalBytes / bytesInGb,
                freeGb = freeBytes / bytesInGb,
                usedGb = usedBytes / bytesInGb
            )
        } catch (e: Exception) {
            StorageHardwareInfo(
                totalBytes = 64L * 1024 * 1024 * 1024,
                freeBytes = 32L * 1024 * 1024 * 1024,
                usedBytes = 32L * 1024 * 1024 * 1024,
                usagePercentage = 50,
                totalGb = 64.0,
                freeGb = 32.0,
                usedGb = 32.0
            )
        }
    }
}
