package com.puri.app.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

/**
 * To Call this at the top of every screen composable to track screen views.
 *
 * Usage:
 * @Composable
 * fun SavedScreen(...) {
 *     TrackScreen("saved_guides")
 * }
 */
@Composable
fun TrackScreen(
    screenName: String,
    analytics: Analytics
) {
    DisposableEffect(screenName) {
        analytics.setScreen(screenName)
        onDispose { }
    }
}