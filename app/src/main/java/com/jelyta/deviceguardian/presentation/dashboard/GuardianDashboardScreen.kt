package com.jelyta.deviceguardian.presentation.dashboard

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.domain.model.PerformanceMode
import com.jelyta.deviceguardian.presentation.theme.*

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
                            Icons.Default.Shield,
                            contentDescription = "App Logo",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jelyta AI Guardian",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                report?.let {
                    HealthGaugeCard(score = it.score, status = it.statusText)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.runTurboBoost() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("one_tap_boost_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isHealing
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = DarkBackground)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("One-Tap Boost", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.runSelfHealing() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("self_heal_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isHealing
                    ) {
                        Icon(Icons.Default.Healing, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Self-Heal", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                metrics?.let {
                    PerformanceModeSelector(
                        currentMode = it.performanceMode,
                        onModeSelected = { mode -> viewModel.setPerformanceMode(mode) }
                    )
                }
            }

            item {
                metrics?.let {
                    MetricsGrid(metrics = it)
                }
            }

            item {
                report?.let {
                    KeyRecommendationsCard(issues = it.keyIssues, recommendations = it.recommendations)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
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
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "HEALTH SCORE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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
                    fontSize = 13.sp,
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
            Spacer(modifier = Modifier.height(12.dp))
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
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                Text(text = value, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = TextSecondary, fontSize = 12.sp)
            Text(text = subtitle, color = TextSecondary.copy(alpha = 0.7f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = SurfaceDark
            )
        }
    }
}

@Composable
fun KeyRecommendationsCard(issues: List<String>, recommendations: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assistant, contentDescription = null, tint = PrimaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Recommendations", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            issues.forEach { issue ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(issue, color = TextPrimary, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            recommendations.forEach { rec ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(rec, color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}
