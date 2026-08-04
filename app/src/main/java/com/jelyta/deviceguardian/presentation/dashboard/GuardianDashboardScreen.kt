package com.jelyta.deviceguardian.presentation.dashboard

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.domain.model.AuditLogItem
import com.jelyta.deviceguardian.domain.model.PerformanceMode
import com.jelyta.deviceguardian.domain.model.SocMetrics
import com.jelyta.deviceguardian.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianDashboardScreen(
    viewModel: DashboardViewModel
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    val metrics = state.metrics
    val report = state.healthReport

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "SOC Shield",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Jelyta Cyber Guardian",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Enterprise SOC & AI Device Defense",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val reportSummary = """
                                🛡️ JELYTA DEVICE GUARDIAN - DIAGNOSTIC REPORT
                                --------------------------------------------
                                • Health Score: ${report?.score ?: 100}/100 (${report?.statusText ?: "Optimal"})
                                • RAM Usage: ${metrics?.ramPercent ?: 0}% (${metrics?.ramUsedMb ?: 0}/${metrics?.ramTotalMb ?: 0} MB)
                                • Storage: ${metrics?.storagePercent ?: 0}% Used (${String.format("%.1f", metrics?.storageFreeGb ?: 0.0)} GB Free)
                                • Battery: ${metrics?.batteryPercent ?: 0}% (${metrics?.batteryTempCelsius ?: 0.0}°C, ${if (metrics?.isCharging == true) "Charging" else "Discharging"})
                                • Performance Mode: ${metrics?.performanceMode?.name ?: "BALANCED"}
                                • Active Issues Count: ${report?.keyIssues?.size ?: 0}
                                --------------------------------------------
                                Report Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}
                            """.trimIndent()

                            val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Device Health Report", reportSummary)
                            clipboardManager?.setPrimaryClip(clip)
                            Toast.makeText(context, "📋 Laporan Diagnostik Disalin ke Papan Klip!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("export_report_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Report",
                            tint = PrimaryCyan
                        )
                    }

                    Surface(
                        color = SecondaryGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SecondaryGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "AI Active",
                                color = SecondaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section -1: Patent Certification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PrimaryCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Patent Badge",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hak Paten & Hak Cipta Terdaftar",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SecondaryGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "RESMI",
                                        color = SecondaryGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pemegang Paten: Samuel David Stefano Basary",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "No. Paten: IDP000082736-DG • AI Device Defense Engine",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Section 0: AI Standby Patrol Engine Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceDark
                    )
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
                                    contentDescription = "Patrol AI",
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "AI Standby Patrol Active",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Penjaga Keamanan & Kinerja HP Otomatis",
                                        color = SecondaryGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Switch(
                                checked = state.isAiPatrolActive,
                                onCheckedChange = { viewModel.toggleAiPatrol(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBackground,
                                    checkedTrackColor = SecondaryGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Patroli Hari Ini", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.patrolStats.patrolsTodayCount}x Selesai", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column {
                                Text("Auto Junk Cleared", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.patrolStats.junkAutoClearedMb} MB", color = SecondaryGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column {
                                Text("Ancaman Dicegah", color = TextSecondary, fontSize = 10.sp)
                                Text("${state.patrolStats.threatsBlockedCount} Blocked", color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.runStandbyPatrolNow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("run_standby_patrol_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                            enabled = !state.isHealing
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Jalankan Patroli AI & Clean Junk Sekarang",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section 1: Enterprise SOC Metrics Cards
            item {
                Text(
                    text = "Security Operations Center (SOC)",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                SocMetricsGrid(socMetrics = state.socMetrics)
            }

            // Section 2: Quick Operations Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Automated AI Operations",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.runCyberAudit() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("run_cyber_audit_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !state.isHealing
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cyber Audit", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.runSelfHealing() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("ai_auto_repair_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !state.isHealing
                                ) {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Auto-Repair", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.runTurboBoost() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("turbo_boost_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !state.isHealing
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Turbo Boost", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.captureDigitalEvidence() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("capture_evidence_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !state.isHealing
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Evidence SHA", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Device Health Score
            item {
                report?.let {
                    HealthGaugeCard(score = it.score, status = it.statusText)
                }
            }

            // Section 4: Performance Mode Selector
            item {
                metrics?.let {
                    PerformanceModeSelector(
                        currentMode = it.performanceMode,
                        onModeSelected = { mode -> viewModel.setPerformanceMode(mode) }
                    )
                }
            }

            // Section 5: Hardware Metrics Grid
            item {
                metrics?.let {
                    MetricsGrid(metrics = it)
                }
            }

            // Section 6: Audit & Event History Feed (Empty state when database is empty)
            item {
                Text(
                    text = "Audit & Forensic Log History",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }

            if (state.auditHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = "Empty Log",
                                tint = TextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Database Initialized • Standby Active",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No prior audit or evidence records. All SOC counters are initialized at 0. Run an operation above to execute initial inspection.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            } else {
                items(state.auditHistory) { log ->
                    AuditHistoryCard(log = log)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SocMetricsGrid(socMetrics: SocMetrics) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SocCard(
                title = "Incidents",
                count = socMetrics.incidentsCount,
                icon = Icons.Default.Warning,
                accentColor = if (socMetrics.incidentsCount > 0) DangerRed else SecondaryGreen,
                modifier = Modifier.weight(1f)
            )
            SocCard(
                title = "Evidence",
                count = socMetrics.evidencesCount,
                icon = Icons.Default.Fingerprint,
                accentColor = WarningOrange,
                modifier = Modifier.weight(1f)
            )
            SocCard(
                title = "IOC",
                count = socMetrics.iocCount,
                icon = Icons.Default.BugReport,
                accentColor = PrimaryCyan,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SocCard(
                title = "Assets",
                count = socMetrics.assetsCount,
                icon = Icons.Default.Devices,
                accentColor = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
            SocCard(
                title = "Audits",
                count = socMetrics.auditCount,
                icon = Icons.Default.FindInPage,
                accentColor = SecondaryGreen,
                modifier = Modifier.weight(1f)
            )
            SocCard(
                title = "Threats",
                count = socMetrics.threatsCount,
                icon = Icons.Default.GppBad,
                accentColor = DangerRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SocCard(
    title: String,
    count: Int,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(18.dp))
                Surface(
                    color = accentColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$count",
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 12.sp
            )
            Text(
                text = if (count == 0) "0 Active" else "$count Recorded",
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AuditHistoryCard(log: AuditLogItem) {
    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("HH:mm:ss - dd MMM yyyy", Locale.getDefault()).format(Date(log.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (log.outcome) {
                    "REPAIRED", "SUCCESS", "PASSED" -> Icons.Default.CheckCircle
                    "ATTENTION REQUIRED" -> Icons.Default.Warning
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (log.outcome) {
                    "REPAIRED", "SUCCESS", "PASSED" -> SecondaryGreen
                    "ATTENTION REQUIRED" -> WarningOrange
                    else -> PrimaryCyan
                },
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.auditTitle,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = when (log.outcome) {
                            "REPAIRED", "SUCCESS", "PASSED", "CLEAN" -> SecondaryGreen.copy(alpha = 0.15f)
                            "ATTENTION REQUIRED", "HIGH THREAT MATCH" -> WarningOrange.copy(alpha = 0.15f)
                            else -> PrimaryCyan.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = log.outcome,
                            fontWeight = FontWeight.Bold,
                            color = when (log.outcome) {
                                "REPAIRED", "SUCCESS", "PASSED", "CLEAN" -> SecondaryGreen
                                "ATTENTION REQUIRED", "HIGH THREAT MATCH" -> WarningOrange
                                else -> PrimaryCyan
                            },
                            fontSize = 10.sp,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.details,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun HealthGaugeCard(score: Int, status: String) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "gaugeAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    drawArc(
                        color = Color(0xFF2C354A),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    val sweepAngle = (animatedScore / 100f) * 270f
                    val gaugeColor = when {
                        animatedScore >= 80 -> SecondaryGreen
                        animatedScore >= 60 -> WarningOrange
                        else -> DangerRed
                    }

                    drawArc(
                        color = gaugeColor,
                        startAngle = 135f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedScore.toInt()}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "HEALTH SCORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = when {
                    score >= 80 -> SecondaryGreen.copy(alpha = 0.2f)
                    score >= 60 -> WarningOrange.copy(alpha = 0.2f)
                    else -> DangerRed.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = status.uppercase(),
                    color = when {
                        score >= 80 -> SecondaryGreen
                        score >= 60 -> WarningOrange
                        else -> DangerRed
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PerformanceModeSelector(
    currentMode: PerformanceMode,
    onModeSelected: (PerformanceMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Performance Mode",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PerformanceMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onModeSelected(mode) },
                        label = {
                            Text(
                                mode.name.replace("_", " "),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = DarkBackground,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(metrics: com.jelyta.deviceguardian.domain.model.DeviceMetrics) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "RAM Usage",
                value = "${metrics.ramPercent}%",
                subtitle = "${metrics.ramUsedMb} / ${metrics.ramTotalMb} MB",
                icon = Icons.Default.Memory,
                progress = metrics.ramPercent / 100f,
                accentColor = PrimaryCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Storage",
                value = "${metrics.storagePercent}%",
                subtitle = "${String.format("%.1f", metrics.storageFreeGb)} GB Free",
                icon = Icons.Default.Storage,
                progress = metrics.storagePercent / 100f,
                accentColor = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Battery",
                value = "${metrics.batteryPercent}%",
                subtitle = if (metrics.isCharging) "Charging (${metrics.batteryTempCelsius}°C)" else "${metrics.batteryTempCelsius}°C",
                icon = Icons.Default.BatteryChargingFull,
                progress = metrics.batteryPercent / 100f,
                accentColor = SecondaryGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "CPU Load",
                value = "${metrics.estimatedCpuUsagePercent}%",
                subtitle = "Active Estimator",
                icon = Icons.Default.Speed,
                progress = metrics.estimatedCpuUsagePercent / 100f,
                accentColor = WarningOrange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                Text(text = value, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = TextSecondary, fontSize = 11.sp)
            Text(text = subtitle, color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = SurfaceDark
            )
        }
    }
}
