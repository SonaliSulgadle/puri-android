package com.puri.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.puri.app.feature.history.HistoryScreen
import com.puri.app.feature.home.HomeScreen
import com.puri.app.feature.onboarding.OnboardingScreen
import com.puri.app.feature.profile.ProfileScreen
import com.puri.app.feature.saved.SavedScreen

@Composable
fun PuriNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSolveResult = { id ->
                    navController.navigate("solve_result/$id")
                }
            )
        }
        composable(Screen.Saved.route) { SavedScreen() }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToSolveResult = { id ->
                    navController.navigate("solve_result/$id")
                }
            )
        }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}