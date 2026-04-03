package com.puri.app.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary                = IndigoPrimary,
    onPrimary              = IndigoOnPrimary,
    primaryContainer       = IndigoPrimaryContainer,
    onPrimaryContainer     = IndigoOnPrimaryContainer,
    secondary              = VioletSecondary,
    onSecondary            = VioletOnSecondary,
    secondaryContainer     = VioletSecondaryContainer,
    onSecondaryContainer   = VioletOnSecondaryContainer,
    tertiary               = MagentaTertiary,
    onTertiary             = MagentaOnTertiary,
    background             = Surface,
    onBackground           = OnSurface,
    surface                = Surface,
    onSurface              = OnSurface,
    surfaceVariant         = SurfaceContainerLow,
    onSurfaceVariant       = OnSurfaceVariant,
    surfaceContainer       = SurfaceContainer,
    surfaceContainerLow    = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerHigh   = SurfaceContainerHigh,
    error                  = Color(0xFFB3261E),
    errorContainer         = ErrorContainer,
    onError                = Color(0xFFFFFFFF),
    onErrorContainer       = OnError,
    outline                = Color(0xFFCAC4D0),
    outlineVariant         = Color(0xFFCAC4D0).copy(alpha = 0.15f),
)

private val DarkColorScheme = darkColorScheme(
    primary                = IndigoPrimaryContainer, // lighter in dark
    onPrimary              = IndigoOnPrimaryContainer,
    primaryContainer       = IndigoPrimary,
    onPrimaryContainer     = IndigoOnPrimary,
    secondary              = VioletSecondaryContainer,
    onSecondary            = VioletOnSecondaryContainer,
    secondaryContainer     = VioletSecondary,
    onSecondaryContainer   = VioletOnSecondary,
    tertiary               = Color(0xFFE06FAA),
    onTertiary             = Color(0xFF5A0035),
    background             = SurfaceDark,
    onBackground           = OnSurfaceDark,
    surface                = SurfaceDark,
    onSurface              = OnSurfaceDark,
    surfaceVariant         = SurfaceContainerLowDark,
    onSurfaceVariant       = OnSurfaceVariantDark,
    surfaceContainer       = SurfaceContainerLowDark,
    surfaceContainerLow    = SurfaceContainerLowDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerHigh   = SurfaceContainerHighDark,
)

@Composable
fun PuriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = PuriTypography,
        content     = content
    )
}