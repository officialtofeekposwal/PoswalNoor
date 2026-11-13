package com.poswal.noor.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Detail : Screen("detail/{contentId}") {
        fun createRoute(contentId: Long) = "detail/$contentId"
    }
    data object Settings : Screen("settings")
}
