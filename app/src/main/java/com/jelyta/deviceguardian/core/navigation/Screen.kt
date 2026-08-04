package com.jelyta.deviceguardian.core.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object PatrolLog : Screen("patrol_log", "AI Patrol Log")
    object Hardware : Screen("hardware", "Hardware")
    object Optimizer : Screen("optimizer", "Optimizer")
    object Security : Screen("security", "Security")
    object Assistant : Screen("assistant", "Assistant")
    object CloudSync : Screen("cloud_sync", "Cloud")
}
