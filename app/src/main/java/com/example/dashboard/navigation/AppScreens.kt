package com.example.dashboard.navigation


sealed class AppScreens(val route: String) {
    object Dashboard : AppScreens("dashboard_screen")
    object Section1 : AppScreens("section1_screen")
    object Section2 : AppScreens("section2_screen")

    object Settings : AppScreens("settings_screen")
    object Form : AppScreens("form_screen")


}