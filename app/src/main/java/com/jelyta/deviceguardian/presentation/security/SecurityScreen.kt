package com.jelyta.deviceguardian.presentation.security

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jelyta.deviceguardian.domain.model.RiskLevel
import com.jelyta.deviceguardian.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                        Text("Cyber Defense & Threat Audit", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                        Text("Enterprise SOC Inspection Module", color = TextSecondary, fontSize = 11.sp)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.runScan() },
                        modifier = Modifier.testTag("rescan_security_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rescan", tint = PrimaryCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = SecondaryGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Security Posture Score: ${state.securityScore}/100", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                        Text("Zero-Trust Architecture • Real-Time Protection Active", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // Tab Bar
            ScrollableTabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = DarkBackground,
                contentColor = PrimaryCyan,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("Apps Audit", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = PrimaryCyan,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Network & Wi-Fi", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = PrimaryCyan,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = state.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("Forensics Hash", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = PrimaryCyan,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = state.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    text = { Text("Threat Intel", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selectedContentColor = PrimaryCyan,
                    unselectedContentColor = TextSecondary
                )
            }

            // Tab Body Content
            when (state.selectedTab) {
                0 -> AppPermissionsTab(state = state)
                1 -> NetworkSecurityTab(state = state, onScanNetwork = { viewModel.scanNetworkSecurity() })
                2 -> ForensicsHashTab(
                    state = state,
                    onInputChanged = { viewModel.updateForensicInput(it) },
                    onGenerateHash = { viewModel.generateForensicHash() }
                )
                3 -> ThreatIntelTab(
                    state = state,
                    onQueryChanged = { viewModel.updateThreatQuery(it) },
                    onSearchThreat = { viewModel.executeThreatLookup() }
                )
            }
        }
    }
}

@Composable
fun AppPermissionsTab(state: SecurityUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Installed Application Audit (${state.apps.size} apps inspected)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)

        if (state.isScanning) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryCyan)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.apps) { app ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.appName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text(app.packageName, color = TextSecondary, fontSize = 11.sp)
                            }
                            Surface(
                                color = when (app.riskLevel) {
                                    RiskLevel.HIGH -> DangerRed.copy(alpha = 0.2f)
                                    RiskLevel.MEDIUM -> WarningOrange.copy(alpha = 0.2f)
                                    RiskLevel.SAFE -> SecondaryGreen.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    app.riskLevel.name,
                                    color = when (app.riskLevel) {
                                        RiskLevel.HIGH -> DangerRed
                                        RiskLevel.MEDIUM -> WarningOrange
                                        RiskLevel.SAFE -> SecondaryGreen
                                    },
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

@Composable
fun NetworkSecurityTab(state: SecurityUiState, onScanNetwork: () -> Unit) {
    val net = state.networkState
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Zero-Trust Wi-Fi & Gateway Analysis", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                NetworkInfoRow("Adapter Status", net.connectionType, SecondaryGreen)
                NetworkInfoRow("Local IP", net.ipAddress, TextPrimary)
                NetworkInfoRow("Gateway", net.gateway, TextPrimary)
                NetworkInfoRow("DNS Protocol", net.dnsSecurityStatus, SecondaryGreen)
                NetworkInfoRow("MitM Spoof Flag", if (net.isMitmDetected) "THREAT DETECTED" else "CLEAN (NO SPOOFING)", if (net.isMitmDetected) DangerRed else SecondaryGreen)

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onScanNetwork,
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("scan_network_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.WifiTethering, contentDescription = null, tint = DarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Perform Network Security Audit", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NetworkInfoRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor, fontSize = 12.sp)
    }
}

@Composable
fun ForensicsHashTab(
    state: SecurityUiState,
    onInputChanged: (String) -> Unit,
    onGenerateHash: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Digital Evidence SHA-256 Generator", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.forensicInput,
                    onValueChange = onInputChanged,
                    label = { Text("Artifact String / Identifier", color = TextSecondary) },
                    placeholder = { Text("e.g., SYSTEM_KERNEL_LOG_01", color = TextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth().testTag("forensic_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarningOrange,
                        unfocusedBorderColor = SurfaceDark,
                        focusedLabelColor = WarningOrange
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGenerateHash,
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("generate_hash_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = DarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Cryptographic SHA-256", color = DarkBackground, fontWeight = FontWeight.Bold)
                }

                if (state.forensicHashResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Generated Immutable Evidence SHA-256:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.forensicHashResult,
                            color = PrimaryCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThreatIntelTab(
    state: SecurityUiState,
    onQueryChanged: (String) -> Unit,
    onSearchThreat: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Threat Intelligence IOC Matcher", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.threatQuery,
                    onValueChange = onQueryChanged,
                    label = { Text("IP / Domain / Hash Indicator", color = TextSecondary) },
                    placeholder = { Text("e.g., 185.220.101.4 or malware_hash", color = TextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth().testTag("threat_query_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = SurfaceDark,
                        focusedLabelColor = PrimaryCyan
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSearchThreat,
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("search_threat_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = DarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Match Threat Indicator", color = DarkBackground, fontWeight = FontWeight.Bold)
                }

                state.threatResult?.let { res ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Threat Intelligence Search Result:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(res.query, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text(
                                    res.status,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.riskScore > 50) DangerRed else SecondaryGreen,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Category: ${res.threatCategory}", color = TextSecondary, fontSize = 11.sp)
                            Text("Risk Score: ${res.riskScore}/100", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Matched Rules:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 11.sp)
                            res.matchedRules.forEach { rule ->
                                Text("• $rule", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
