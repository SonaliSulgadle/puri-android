package com.puri.app.core.ui.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

/**
 * This method controls ONLY the status bar icon color for a specific screen.
 * Does NOT add any padding — Scaffold handles that separately.
 *
 * @param darkIcons true = dark/black icons (used on LIGHT backgrounds)
 *                  false = light/white icons (used on DARK backgrounds)
 */
@Composable
fun StatusBarIconColor(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    DisposableEffect(darkIcons) {
        val window = (view.context as Activity).window
        val controller = WindowInsetsControllerCompat(window, view)
        val previous = controller.isAppearanceLightStatusBars

        controller.isAppearanceLightStatusBars = darkIcons

        onDispose {
            controller.isAppearanceLightStatusBars = previous
        }
    }
}