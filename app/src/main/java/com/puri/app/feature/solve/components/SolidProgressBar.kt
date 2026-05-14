package com.puri.app.feature.solve.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import com.puri.app.core.ui.theme.CeladonPrimary

@Composable
fun SolidProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = CeladonPrimary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    Canvas(modifier = modifier) {
        // Track (background)
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = CornerRadius(size.height / 2)
        )
        // Progress fill
        val progressWidth = size.width * progress.coerceIn(0f, 1f)
        if (progressWidth > 0f) {
            drawRoundRect(
                color = progressColor,
                size = androidx.compose.ui.geometry.Size(progressWidth, size.height),
                cornerRadius = CornerRadius(size.height / 2)
            )
        }
    }
}
