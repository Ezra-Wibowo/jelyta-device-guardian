package com.jelyta.deviceguardian.core.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Hardware : Screen("hardware", "Hardware Engine")
    object Optimizer : Screen("optimizer", "Optimizer")
    object Security : Screen("security", "Security Audit")
    object Assistant : Screen("assistant", "AI Assistant")
    object CloudSync : Screen("cloud_sync", "Cloud Sync")
}
