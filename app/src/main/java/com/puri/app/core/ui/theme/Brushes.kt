package com.puri.app.core.ui.theme

import androidx.compose.ui.graphics.Brush

object PuriBrush {

    // The Snap & Solve button — violet → magenta
    val snapAndSolve = Brush.linearGradient(
        colors = listOf(GradientSnapStart, GradientSnapEnd)
    )

    // Hero card background — indigo → lighter indigo
    val heroCard = Brush.linearGradient(
        colors = listOf(GradientHeroStart, GradientHeroEnd)
    )

    // Subtle surface gradient to break flat look
    val surfaceSubtle = Brush.verticalGradient(
        colors = listOf(Surface, SurfaceContainerLow)
    )
}