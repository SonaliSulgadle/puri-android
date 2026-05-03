package com.puri.app.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalytics = staticCompositionLocalOf<Analytics> {
    error("No Analytics provided — wrap with CompositionLocalProvider(LocalAnalytics provides analytics)")
}