package com.jelyta.deviceguardian.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.jelyta.deviceguardian.presentation.security.SecurityScreen
import com.jelyta.deviceguardian.presentation.security.SecurityViewModel
import com.jelyta.deviceguardian.presentation.theme.DarkBackground
import com.jelyta.deviceguardian.presentation.theme.PrimaryCyan
import com.jelyta.deviceguardian.presentation.theme.TextSecondary

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val items = listOf(
        Screen.Dashboard to Icons.Default.Dashboard,
        Screen.Hardware to Icons.Default.Memory,
        Screen.Optimizer to Icons.Default.Speed,
        Screen.Security to Icons.Default.Security,
        Screen.Assistant to Icons.Default.Assistant,
        Screen.CloudSync to Icons.Default.CloudSync
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = DarkBackground) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
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
