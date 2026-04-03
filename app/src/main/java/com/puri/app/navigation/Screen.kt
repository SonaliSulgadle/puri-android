package com.puri.app.navigation

sealed class Screen(val route: String) {
    // Main tabs
    data object Home : Screen("home")
    data object Saved : Screen("saved")
    data object History : Screen("history")
    data object Profile : Screen("profile")

    // Sub-screens (no bottom nav)
    data object Onboarding : Screen("onboarding")
    data class SolveResult(val id: Long = -1L) : Screen("solve_result/{id}") {
        fun withId(id: Long) = "solve_result/$id"
    }
}