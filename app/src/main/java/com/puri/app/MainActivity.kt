package com.puri.app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.ManageHistory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.OnSurfaceVariant
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.core.ui.theme.SurfaceContainerLowest
import com.puri.app.navigation.PuriNavGraph
import com.puri.app.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val fullScreenRoutes = setOf(Screen.Onboarding.route)

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            delay(600)
            isReady = true
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Scale down and fade out — feels like the icon "launches" into the app
            val scaleX = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_X, 1f, 0f)
            val scaleY = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_Y, 1f, 0f)
            val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

            scaleX.duration = 350
            scaleY.duration = 350
            alpha.duration = 350

            scaleX.interpolator = AccelerateInterpolator()
            scaleY.interpolator = AccelerateInterpolator()

            scaleX.start()
            scaleY.start()
            alpha.start()

            alpha.doOnEnd { splashScreenView.remove() }
        }

        setContent {
            PuriTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isFirstLaunch = remember { mutableStateOf(true) }
                val startDestination = if (isFirstLaunch.value) {
                    Screen.Home.route // TODO - To be replaced with Onboarding
                } else {
                    Screen.Home.route
                }

                val showBottomBar = currentRoute !in fullScreenRoutes

                val bottomNavItems = listOf(
                    BottomNavItem(
                        Screen.Home,
                        "Home",
                        Icons.Filled.Explore,
                        Icons.Outlined.Explore
                    ),
                    BottomNavItem(
                        Screen.Saved,
                        "Saved",
                        Icons.Filled.Bookmark,
                        Icons.Outlined.BookmarkBorder
                    ),
                    BottomNavItem(
                        Screen.History,
                        "History",
                        Icons.Filled.ManageHistory,
                        Icons.Outlined.ManageHistory
                    ),
                    BottomNavItem(
                        Screen.Profile,
                        "Profile",
                        Icons.Filled.AccountCircle,
                        Icons.Outlined.AccountCircle
                    ),
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SurfaceContainerLowest,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = SurfaceContainerLowest,
                                tonalElevation = 0.dp
                            ) {
                                bottomNavItems.forEach { item ->
                                    NavigationBarItem(
                                        selected = currentRoute == item.screen.route,
                                        onClick = {
                                            navController.navigate(item.screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = item.selectedIcon,
                                                contentDescription = item.label
                                            )
                                        },
                                        label = { Text(item.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = IndigoPrimary,
                                            selectedTextColor = IndigoPrimary,
                                            unselectedIconColor = OnSurfaceVariant,
                                            unselectedTextColor = OnSurfaceVariant,
                                            indicatorColor = IndigoPrimary.copy(alpha = 0.12f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    PuriNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}