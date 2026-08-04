package com.jelyta.deviceguardian.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.jelyta.deviceguardian.presentation.assistant.AssistantTranslatorScreen
import com.jelyta.deviceguardian.presentation.assistant.AssistantViewModel
import com.jelyta.deviceguardian.presentation.cloud.CloudSyncScreen
import com.jelyta.deviceguardian.presentation.cloud.CloudSyncViewModel
import com.jelyta.deviceguardian.presentation.dashboard.GuardianDashboardScreen
import com.jelyta.deviceguardian.presentation.dashboard.DashboardViewModel
import com.jelyta.deviceguardian.presentation.hardware.HardwareDashboardScreen
import com.jelyta.deviceguardian.presentation.hardware.HardwareDashboardViewModel
import com.jelyta.deviceguardian.presentation.optimizer.OptimizerScreen
import com.jelyta.deviceguardian.presentation.optimizer.OptimizerViewModel
import com.jelyta.deviceguardian.presentation.patrol.PatrolLogScreen
import com.jelyta.deviceguardian.presentation.patrol.PatrolLogViewModel
import com.jelyta.deviceguardian.presentation.security.SecurityScreen
import com.jelyta.deviceguardian.presentation.security.SecurityViewModel
import com.jelyta.deviceguardian.presentation.theme.*

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var showStartupPatentDialog by rememberSaveable { mutableStateOf(true) }

    val items = listOf(
        Screen.Dashboard to Icons.Default.Dashboard,
        Screen.PatrolLog to Icons.Default.Shield,
        Screen.Hardware to Icons.Default.Memory,
        Screen.Optimizer to Icons.Default.Speed,
        Screen.Security to Icons.Default.Security,
        Screen.Assistant to Icons.Default.Assistant,
        Screen.CloudSync to Icons.Default.CloudSync
    )

    // Startup Patent Dialog when app opens
    if (showStartupPatentDialog) {
        AlertDialog(
            onDismissRequest = { showStartupPatentDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Patent Verified",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Informasi Hak Paten & Hak Cipta",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "PEMEGANG HAK PATEN RESMI",
                                color = SecondaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Samuel David Stefano Basary",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("No. Paten: IDP000082736-DG", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SecondaryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lisensi: DJKI Intelektual Cyber Defense", color = TextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Engine: Jelyta AI Device Guardian v2.4", color = TextPrimary, fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "Seluruh sistem optimasi, pertahanan memori, dan keamanan kecerdasan buatan dilindungi oleh Hak Cipta & Hak Paten atas nama Samuel David Stefano Basary.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStartupPatentDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    modifier = Modifier.testTag("patent_dialog_dismiss_btn")
                ) {
                    Text("Verifikasi & Masuk Aplikasi", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.background(DarkBackground)) {
                // Persistent Patent Footer at bottom of screen
                Surface(
                    color = CardSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Patent Badge",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hak Paten Resmi: Samuel David Stefano Basary • IDP000082736-DG",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                NavigationBar(containerColor = DarkBackground) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = screen.title) },
                            label = {
                                Text(
                                    text = screen.title,
                                    maxLines = 1,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBackground,
                                selectedTextColor = PrimaryCyan,
                                indicatorColor = PrimaryCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(context))
                GuardianDashboardScreen(viewModel = vm)
            }
            composable(Screen.PatrolLog.route) {
                val vm: PatrolLogViewModel = viewModel(factory = PatrolLogViewModel.Factory(context))
                PatrolLogScreen(viewModel = vm)
            }
            composable(Screen.Hardware.route) {
                val vm: HardwareDashboardViewModel = viewModel(factory = HardwareDashboardViewModel.Factory(context))
                HardwareDashboardScreen(viewModel = vm)
            }
            composable(Screen.Optimizer.route) {
                val vm: OptimizerViewModel = viewModel(factory = OptimizerViewModel.Factory(context))
                OptimizerScreen(viewModel = vm)
            }
            composable(Screen.Security.route) {
                val vm: SecurityViewModel = viewModel(factory = SecurityViewModel.Factory(context))
                SecurityScreen(viewModel = vm)
            }
            composable(Screen.Assistant.route) {
                val vm: AssistantViewModel = viewModel(factory = AssistantViewModel.Factory(context))
                AssistantTranslatorScreen(viewModel = vm)
            }
            composable(Screen.CloudSync.route) {
                val vm: CloudSyncViewModel = viewModel(factory = CloudSyncViewModel.Factory(context))
                CloudSyncScreen(viewModel = vm)
            }
        }
    }
}
