package com.jelyta.deviceguardian.data.datasource.hardware

import android.net.TrafficStats

data class NetworkTrafficHardwareInfo(
    val totalRxBytes: Long,
    val totalTxBytes: Long,
    val mobileRxBytes: Long,
    val mobileTxBytes: Long,
    val formattedTotalRx: String,
    val formattedTotalTx: String,
    val formattedMobileRx: String,
    val formattedMobileTx: String
)

class NetworkTrafficMonitor {

    fun getTrafficInfo(): NetworkTrafficHardwareInfo {
        val totalRx = TrafficStats.getTotalRxBytes().let { if (it < 0) 0L else it }
        val totalTx = TrafficStats.getTotalTxBytes().let { if (it < 0) 0L else it }
        val mobileRx = TrafficStats.getMobileRxBytes().let { if (it < 0) 0L else it }
        val mobileTx = TrafficStats.getMobileTxBytes().let { if (it < 0) 0L else it }

        return NetworkTrafficHardwareInfo(
            totalRxBytes = totalRx,
            totalTxBytes = totalTx,
            mobileRxBytes = mobileRx,
            mobileTxBytes = mobileTx,
            formattedTotalRx = formatBytes(totalRx),
            formattedTotalTx = formatBytes(totalTx),
            formattedMobileRx = formatBytes(mobileRx),
            formattedMobileTx = formatBytes(mobileTx)
        )
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.2f GB", gb)
            mb >= 1.0 -> String.format("%.2f MB", mb)
            else -> String.format("%.2f KB", kb)
        }
    }
}
