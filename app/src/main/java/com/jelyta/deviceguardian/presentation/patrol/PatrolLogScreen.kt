package com.jelyta.deviceguardian.presentation.patrol

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.domain.model.AuditLogItem
import com.jelyta.deviceguardian.presentation.theme.*
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatrolLogScreen(viewModel: PatrolLogViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Patrol Activity Log",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Laporan Aktivitas Real-time Pemindaian & Keamanan AI",
                            color = SecondaryGreen,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val allSummary = buildString {
                                appendLine("🛡️ JELYTA CYBER GUARDIAN - FULL AI PATROL LOG")
                                appendLine("Total Patrols: ${state.totalPatrolsCount} | Auto Cleared Junk: ${state.totalJunkClearedMb} MB | Threats Blocked: ${state.totalThreatsBlocked}")
                                appendLine("--------------------------------------------")
                                state.filteredLogs.take(10).forEach { item ->
                                    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))
                                    appendLine("• [$time] [${item.outcome}] ${item.auditTitle}")
                                    appendLine("  Details: ${item.details}")
                                }
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("AI Patrol Log Summary", allSummary)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "📋 Laporan Log Patroli AI Disalin!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("export_patrol_logs_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Patrol Logs",
                            tint = PrimaryCyan
                        )
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Stats Banner
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
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Patroli Keamanan AI Active",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "Laporan Pemindaian Berkala Terbuka",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                color = SecondaryGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .padding(end = 0.dp)
                                    )
                                    Text(
                                        "Standby Active",
                                        color = SecondaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Patroli Hari Ini", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.totalPatrolsCount} Scans", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Auto Junk Cleared", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.totalJunkClearedMb} MB", color = SecondaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Ancaman Ditolak", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.totalThreatsBlocked} Blocked", color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.runManualPatrolScan() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("run_manual_patrol_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                            enabled = !state.isScanning,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (state.isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DarkBackground,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Memindai Sistem & Junk...", color = DarkBackground, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Jalankan Pemindaian Patroli AI Sekarang", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Cari log aktivitas patroli...", color = TextSecondary, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("patrol_log_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = CardSurface,
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = CardSurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    val categories = listOf(
                        "ALL" to "Semua Log",
                        "SECURITY" to "🛡️ Keamanan",
                        "JUNK_CLEAN" to "🧹 Pembersihan Junk",
                        "OPTIMIZE" to "⚡ Boost RAM",
                        "SPAM_CALL" to "📞 Truecaller/Spam"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { (code, label) ->
                            val isSelected = state.selectedCategory == code
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(code) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryCyan,
                                    selectedLabelColor = DarkBackground,
                                    containerColor = CardSurface,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Patrol Log Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Laporan Aktivitas (${state.filteredLogs.size})",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Urutan Terbaru",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            if (state.filteredLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tidak Ada Log Patroli Ditemukan", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Coba sesuaikan kata kunci pencarian atau kategori filter", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                items(state.filteredLogs) { log ->
                    PatrolLogItemCard(log = log, context = context)
                }
            }
        }
    }
}

@Composable
fun PatrolLogItemCard(log: AuditLogItem, context: Context) {
    var expanded by remember { mutableStateOf(false) }

    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("HH:mm:ss - dd MMM yyyy", Locale.getDefault()).format(Date(log.timestamp))
    }

    val hashArtifact = remember(log.id, log.timestamp, log.auditTitle) {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest("${log.id}-${log.timestamp}-${log.auditTitle}".toByteArray())
            bytes.joinToString("") { "%02x".format(it) }.take(24)
        } catch (_: Exception) {
            "a8f93e71c900234bd8921"
        }
    }

    val badgeColor = when (log.outcome) {
        "REPAIRED", "SUCCESS", "PASSED", "CLEAN" -> SecondaryGreen
        "ATTENTION REQUIRED", "HIGH THREAT MATCH" -> WarningOrange
        else -> PrimaryCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (log.outcome) {
                        "REPAIRED", "SUCCESS", "PASSED", "CLEAN" -> Icons.Default.CheckCircle
                        "ATTENTION REQUIRED", "HIGH THREAT MATCH" -> Icons.Default.Warning
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.auditTitle,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateStr,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = log.outcome,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.details,
                color = TextPrimary,
                fontSize = 12.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(color = SurfaceDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Digital Audit Integrity Hash (SHA-256):", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 10.sp)
                    Text(hashArtifact, color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val logText = "🛡️ AI PATROL LOG ENTRY\nTitle: ${log.auditTitle}\nStatus: ${log.outcome}\nTime: $dateStr\nDetails: ${log.details}\nHash: $hashArtifact"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("AI Patrol Log", logText)
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, "Log disalin ke papan klip", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin rincian log", color = PrimaryCyan, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
