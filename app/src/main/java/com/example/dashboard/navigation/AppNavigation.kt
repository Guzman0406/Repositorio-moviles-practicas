package com.example.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dashboard.ui.theme.screens.DashboardScreen
import com.example.dashboard.ui.theme.screens.Section1Screen
import com.example.dashboard.ui.theme.screens.Section2Screen
import com.example.dashboard.ui.theme.settings.SettingsScreen
import com.example.dashboard.ui.theme.settings.SettingsScreen
import com.example.dashboard.ui.theme.screens.DashboardScreen
import com.example.dashboard.ui.theme.screens.Section1Screen
import com.example.dashboard.ui.theme.screens.Section2Screen
import com.example.dashboard.ui.theme.screens.FormScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Dashboard.route
    ) {
        composable(route = AppScreens.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(route = AppScreens.Section1.route) {
            Section1Screen(navController)
        }
        composable(route = AppScreens.Section2.route) {
            Section2Screen(navController)
        }

        composable(route = AppScreens.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(route = AppScreens.Form.route) {
            FormScreen(navController)
        }
    }
}