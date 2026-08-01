package com.jelyta.deviceguardian.presentation.hardware

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.domain.model.HardwareStatus
import com.jelyta.deviceguardian.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareDashboardScreen(
    viewModel: HardwareDashboardViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snapshot = state.snapshot

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = PrimaryCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hardware Monitoring Engine", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshManually() },
                        modifier = Modifier.testTag("refresh_hardware_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Hardware Telemetry", tint = PrimaryCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        if (state.isLoading || snapshot == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryCyan)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. AI Health Engine Score Card
                item {
                    val health = snapshot.health
                    val statusColor = when (health.status) {
                        HardwareStatus.EXCELLENT -> SecondaryGreen
                        HardwareStatus.GOOD -> PrimaryCyan
                        HardwareStatus.WARNING -> WarningOrange
                        HardwareStatus.CRITICAL -> DangerRed
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("AI HARDWARE HEALTH ENGINE", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${health.score} / 100",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor,
                                        modifier = Modifier.testTag("hardware_score_text")
                                    )
                                }

                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        health.status.name,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(health.summaryText, color = TextPrimary, fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { health.score / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = statusColor,
                                trackColor = SurfaceDark
                            )

                            if (health.issues.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Key Hardware Findings:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                health.issues.forEach { issue ->
                                    Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(issue, color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Tab Navigation
                item {
                    TabRow(
                        selectedTabIndex = state.selectedCategoryIndex,
                        containerColor = CardSurface,
                        contentColor = PrimaryCyan,
                        indicator = { tabPositions ->
                            if (state.selectedCategoryIndex < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedCategoryIndex]),
                                    color = PrimaryCyan
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = state.selectedCategoryIndex == 0,
                            onClick = { viewModel.selectCategory(0) },
                            text = { Text("Battery & Thermal", fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = state.selectedCategoryIndex == 1,
                            onClick = { viewModel.selectCategory(1) },
                            text = { Text("Memory & Storage", fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = state.selectedCategoryIndex == 2,
                            onClick = { viewModel.selectCategory(2) },
                            text = { Text("CPU & System", fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                // 3. Tab Content
                item {
                    when (state.selectedCategoryIndex) {
                        0 -> BatteryThermalSection(snapshot)
                        1 -> MemoryStorageSection(snapshot)
                        else -> CpuSystemSection(snapshot)
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryThermalSection(snapshot: com.jelyta.deviceguardian.domain.model.HardwareSnapshot) {
    val b = snapshot.batteryInfo
    val t = snapshot.thermalInfo

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BATTERY TELEMETRY (BatteryManager)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                HardwareDetailRow("Battery Level", "${b.batteryPercent}% (${b.status})")
                HardwareDetailRow("Temperature", "${b.temperatureCelsius}°C", testTag = "battery_temp_text")
                HardwareDetailRow("Power Source", b.plugType)
                HardwareDetailRow("Battery Health", b.health)
                HardwareDetailRow("Voltage", "${b.voltageMv} mV")
                HardwareDetailRow("Technology", b.technology)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("THERMAL FRAMEWORK", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(t.statusDescription, color = TextPrimary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MemoryStorageSection(snapshot: com.jelyta.deviceguardian.domain.model.HardwareSnapshot) {
    val m = snapshot.memoryInfo
    val s = snapshot.storageInfo

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("RAM TELEMETRY (ActivityManager.MemoryInfo)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                HardwareDetailRow("RAM Used", "${m.usedMb} MB / ${m.totalMb} MB (${m.usagePercentage}%)", testTag = "ram_usage_text")
                HardwareDetailRow("Available RAM", "${m.availMb} MB")
                HardwareDetailRow("Low Memory State", if (m.isLowMemory) "ACTIVE WARNING" else "Normal")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("INTERNAL STORAGE TELEMETRY (StatFs)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                HardwareDetailRow("Free Storage", "${String.format("%.1f", s.freeGb)} GB / ${String.format("%.1f", s.totalGb)} GB", testTag = "storage_usage_text")
                HardwareDetailRow("Used Storage", "${String.format("%.1f", s.usedGb)} GB (${s.usagePercentage}%)")
            }
        }
    }
}

@Composable
private fun CpuSystemSection(snapshot: com.jelyta.deviceguardian.domain.model.HardwareSnapshot) {
    val c = snapshot.cpuInfo

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("CPU SUBSYSTEM TELEMETRY", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            HardwareDetailRow("CPU Cores", "${c.availableProcessors} Active Cores")
            HardwareDetailRow("Estimated CPU Load", "${c.estimatedCpuUsagePercent}%")
            Spacer(modifier = Modifier.height(8.dp))
            Text(c.statusDescription, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun HardwareDetailRow(label: String, value: String, testTag: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        val modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
        Text(value, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = modifier)
    }
}
