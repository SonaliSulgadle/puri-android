package com.puri.app.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.puri.app.core.ui.util.StatusBarIconColor
import com.puri.app.feature.address.AddressConverterScreen
import com.puri.app.feature.address.AddressResultScreen
import com.puri.app.feature.address.AddressViewModel
import com.puri.app.feature.history.HistoryScreen
import com.puri.app.feature.history.detail.HistoryDetailScreen
import com.puri.app.feature.onboarding.OnboardingScreen
import com.puri.app.feature.profile.PrivacyPolicyScreen
import com.puri.app.feature.profile.ProfileScreen
import com.puri.app.feature.saved.SavedScreen
import com.puri.app.feature.saved.detail.SavedGuideDetailScreen
import com.puri.app.feature.solve.SolveScreen

@Composable
fun PuriNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding — shows only on first launch
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Main app with bottom navigation
        composable(route = Screen.Home.route) {
            MainScaffold(navController = navController)
        }

        // Detail screens
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

        composable(route = Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HistoryDetail.route,
            arguments = listOf(
                navArgument(Screen.HistoryDetail.ARG) { type = NavType.LongType }
            )
        ) {
            HistoryDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddressConverter.route) {
            val viewModel: AddressViewModel = hiltViewModel()
            AddressConverterScreen(
                onBack = { navController.popBackStack() },
                onResultReady = {
                    navController.navigate(Screen.AddressResult.route) {
                        launchSingleTop = true
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.AddressResult.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.AddressConverter.route)
            }
            val viewModel: AddressViewModel = hiltViewModel(parentEntry)
            AddressResultScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

    }
}

// MainScaffold — the bottom nav container
@Composable
fun MainScaffold(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val currentDestination by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val isDark = isSystemInDarkTheme()
    StatusBarIconColor(darkIcons = !isDark)

    Scaffold(
        bottomBar = {
            PuriBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                SolveScreen(
                    onNavigateToSaved = {
                        bottomNavController.navigate(Screen.Saved.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHistory = {
                        bottomNavController.navigate(Screen.History.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenAddressConverter = {
                        navController.navigate(
                            Screen.AddressConverter.route
                        )
                    },
                    onNavigateToHistoryDetail = { historyItemId ->
                        navController.navigate(Screen.HistoryDetail.createRoute(historyItemId))
                    }
                )
            }

            composable(Screen.Saved.route) {
                SavedScreen(
                    onNavigateToSolve = {
                        bottomNavController.navigate(Screen.Home.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDetail = { guideId ->
                        navController.navigate(Screen.SavedDetail.createRoute(guideId))
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToDetail = { historyItemId ->
                        navController.navigate(
                            Screen.HistoryDetail.createRoute(historyItemId)
                        )
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    }
                )
            }
        }
    }
}