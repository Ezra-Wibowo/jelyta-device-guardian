package com.jelyta.deviceguardian.presentation.optimizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
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
                title = { Text("AI Optimizer & Self-Heal", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instant System Actions", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.runTurboBoost() },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("opt_turbo_boost_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            enabled = !state.isOptimizing
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Turbo Boost", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.runCacheClean() },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("opt_cache_clean_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                            enabled = !state.isOptimizing
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = DarkBackground)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Purge Cache", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }

                    state.lastMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(msg, color = SecondaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = PrimaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Optimization Log History", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            }

            if (state.logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No optimization history yet. Run Turbo Boost or Cache Clean above.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                                    Text(log.actionName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                    val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                                    Text("Executed at $dateStr", color = TextSecondary, fontSize = 12.sp)
                                }
                                Surface(
                                    color = SecondaryGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "+${log.reclaimedMemoryMb} MB",
                                        color = SecondaryGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
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
}
