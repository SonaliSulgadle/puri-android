package com.puri.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.analytics.LocalAnalytics
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.feature.launch.LaunchViewModel
import com.puri.app.navigation.PuriNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: Analytics

    private val launchViewModel: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            launchViewModel.startDestination.value == null
        }

        if (savedInstanceState == null) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val iconView = splashScreenView.iconView
                if (iconView == null) {
                    splashScreenView.remove()
                    return@setOnExitAnimationListener
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 1.3f)
                    val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 1.3f)
                    val iconAlpha = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)
                    val bgAlpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                    listOf(scaleX, scaleY, iconAlpha, bgAlpha).forEach { it.duration = 400L }
                    scaleX.interpolator = OvershootInterpolator()
                    scaleY.interpolator = OvershootInterpolator()
                    bgAlpha.doOnEnd { splashScreenView.remove() }
                    AnimatorSet().apply {
                        playTogether(
                            scaleX,
                            scaleY,
                            iconAlpha,
                            bgAlpha
                        ); start()
                    }
                } else {
                    ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f).apply {
                        duration = 300L
                        doOnEnd { splashScreenView.remove() }
                        start()
                    }
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
                    CompositionLocalProvider(LocalAnalytics provides analytics) {
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
}