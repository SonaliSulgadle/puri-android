package com.puri.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.puri.app.feature.history.HistoryScreen
import com.puri.app.feature.onboarding.OnboardingScreen
import com.puri.app.feature.profile.ProfileScreen
import com.puri.app.feature.saved.SavedScreen
import com.puri.app.feature.saved.detail.SavedGuideDetailScreen
import com.puri.app.feature.solve.SolveScreen

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
            SolveScreen(
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Saved.route) {
            SavedScreen(
                onNavigateToSolve = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToDetail = { guideId ->
                    navController.navigate(Screen.SavedDetail.createRoute(guideId))
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Profile.route) { ProfileScreen() }

        composable(
            route = Screen.SavedDetail.route,
            arguments = listOf(
                navArgument(Screen.SavedDetail.ARG) { type = NavType.LongType }
            )
        ) {
            SavedGuideDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}