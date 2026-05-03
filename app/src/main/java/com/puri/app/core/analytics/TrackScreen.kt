package com.puri.app.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun TrackScreen(screenName: String) {
    val analytics = LocalAnalytics.current ?: return
    DisposableEffect(screenName) {
        analytics.setScreen(screenName)
        onDispose { }
    }
}