package com.puri.app.core.ui.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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

    // Used SideEffect and not DisposableEffect — runs on every recomposition
    // ensuring correct state even after back navigation
    SideEffect {
        val window = (view.context as Activity).window
        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = darkIcons
    }
}