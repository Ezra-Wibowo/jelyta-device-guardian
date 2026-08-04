package com.jelyta.deviceguardian.presentation.optimizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizerScreen(
    viewModel: OptimizerViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Optimizer & Anti-Lag Engine", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                        Text("Solusi Performa Maksimal & CPU Throttle Defense", color = SecondaryGreen, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 0: CPU Lag Solution & Throttle Guard
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Solusi CPU Lemot & HP Lambat", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                                    Text("Mode Akselerasi Prosesor & Penyeimbang Suhu", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            Surface(
                                color = PrimaryCyan.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Suhu CPU: ${state.cpuState.estimatedCpuTempCelsius}°C",
                                    color = PrimaryCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Pilih Mode Akselerasi Prosesor:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val modes = listOf(
                                "ULTRA_SPEED" to "🚀 Ultra Speed",
                                "BALANCED" to "⚖️ Balanced",
                                "BATTERY_SAVER" to "🔋 Hemat Baterai"
                            )

                            modes.forEach { (mode, label) ->
                                val isSelected = state.cpuState.cpuGovernorMode == mode
                                Button(
                                    onClick = { viewModel.setCpuMode(mode) },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) PrimaryCyan else SurfaceDark,
                                        contentColor = if (isSelected) DarkBackground else TextSecondary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = SurfaceDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        // App Freezer & Animation Tweak Switches
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pembeku App Background (App Freezer)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text("Membekukan ${state.cpuState.frozenAppsCount} aplikasi tersembunyi yang menguras CPU", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = state.cpuState.isAppFreezerActive,
                                onCheckedChange = { viewModel.toggleAppFreezer(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = SecondaryGreen)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Optimasi Skala Animasi System UI (0.5x)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text("Akselerasi kecepatan respon UI 2x lebih cepat di CPU spesifikasi rendah", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = state.cpuState.animationScaleTweakEnabled,
                                onCheckedChange = { viewModel.toggleAnimationScale(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = PrimaryCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.runCpuDefragAndFreeze() },
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("defrag_cpu_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            enabled = !state.isOptimizing,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Akselerasi CPU & Hentikan App Beban Berat", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section 1: Instant System Actions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tindakan Pengoptimalan Instan", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.runTurboBoost() },
                                modifier = Modifier.weight(1f).height(46.dp).testTag("opt_turbo_boost_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                enabled = !state.isOptimizing
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = DarkBackground)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Turbo Boost RAM", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.runCacheClean() },
                                modifier = Modifier.weight(1f).height(46.dp).testTag("opt_cache_clean_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                                enabled = !state.isOptimizing
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = DarkBackground)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bersihkan Cache", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        state.lastMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(msg, color = SecondaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Section 1.5: Nightly Automatic Cache Clean Schedule Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Pembersihan Malam Otomatis",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "WorkManager Background Auto-Clean saat HP diisi daya",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = state.nightlySchedule.isEnabled,
                                onCheckedChange = { viewModel.toggleNightlyClean(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBackground,
                                    checkedTrackColor = SecondaryGreen
                                ),
                                modifier = Modifier.testTag("nightly_clean_switch")
                            )
                        }

                        if (state.nightlySchedule.isEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = SurfaceDark)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Pilih Waktu Jam Pembersihan Malam Hari (WIB):",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val hours = listOf(1 to "01:00 AM", 2 to "02:00 AM", 3 to "03:00 AM", 4 to "04:00 AM")
                                hours.forEach { (hour, label) ->
                                    val isSelected = state.nightlySchedule.scheduledHour == hour
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setNightlyCleanHour(hour) },
                                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SecondaryGreen,
                                            selectedLabelColor = DarkBackground,
                                            containerColor = SurfaceDark,
                                            labelColor = TextSecondary
                                        ),
                                        modifier = Modifier.weight(1f).testTag("nightly_clean_hour_$hour")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = SecondaryGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = SecondaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Status: Terjadwal setiap pukul %02d:00 WIB. Membersihkan cache, file sampah & background RAM otomatis tanpa mengganggu penggunaan.".format(state.nightlySchedule.scheduledHour),
                                        color = SecondaryGreen,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Log History
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = PrimaryCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Riwayat Pengoptimalan Sistem", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                }
            }

            if (state.logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada riwayat pengoptimalan. Jalankan Turbo Boost di atas.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                items(state.logs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.actionName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                val dateStr = SimpleDateFormat("HH:mm:ss - dd MMM", Locale.getDefault()).format(Date(log.timestamp))
                                Text("Eksekusi pada $dateStr", color = TextSecondary, fontSize = 11.sp)
                            }
                            Surface(
                                color = SecondaryGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "+${log.reclaimedMemoryMb} MB",
                                    color = SecondaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

