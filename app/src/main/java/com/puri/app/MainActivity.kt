package com.puri.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.feature.launch.LaunchViewModel
import com.puri.app.navigation.PuriNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val launchViewModel: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            launchViewModel.startDestination.value == null
        }

        // Splash exit animation
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scaleX = ObjectAnimator.ofFloat(
                    splashScreenView.iconView, View.SCALE_X, 1f, 1.3f
                )
                val scaleY = ObjectAnimator.ofFloat(
                    splashScreenView.iconView, View.SCALE_Y, 1f, 1.3f
                )
                val iconAlpha = ObjectAnimator.ofFloat(
                    splashScreenView.iconView, View.ALPHA, 1f, 0f
                )
                val bgAlpha = ObjectAnimator.ofFloat(
                    splashScreenView.view, View.ALPHA, 1f, 0f
                )

                listOf(scaleX, scaleY, iconAlpha, bgAlpha).forEach { it.duration = 400L }
                scaleX.interpolator = OvershootInterpolator()
                scaleY.interpolator = OvershootInterpolator()
                bgAlpha.interpolator = AccelerateInterpolator()

                bgAlpha.doOnEnd { splashScreenView.remove() }

                AnimatorSet().apply {
                    playTogether(scaleX, scaleY, iconAlpha, bgAlpha)
                    start()
                }
            } else {
                // Simple fade for pre-API 31
                ObjectAnimator
                    .ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                    .apply {
                        duration = 300L
                        doOnEnd { splashScreenView.remove() }
                        start()
                    }
            }
        }

        setContent {
            val startDestination by launchViewModel.startDestination
                .collectAsStateWithLifecycle()

            PuriTheme {
                AnimatedVisibility(
                    visible = startDestination != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    startDestination?.let { destination ->
                        val navController = rememberNavController()
                        PuriNavGraph(
                            navController = navController,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}