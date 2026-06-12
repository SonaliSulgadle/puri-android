package com.puri.app.core.ui.theme

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

private val LightColorScheme = lightColorScheme(
    primary = CeladonPrimary,
    onPrimary = CeladonOnPrimary,
    primaryContainer = CeladonPrimaryContainer,
    onPrimaryContainer = CeladonOnPrimaryContainer,
    secondary = SageSecondary,
    onSecondary = SageOnSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = MossTertiary,
    onTertiary = MossOnTertiary,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerHigh = SurfaceContainerHigh,
    error = Color(0xFFB3261E),
    errorContainer = ErrorContainer,
    onError = Color(0xFFFFFFFF),
    onErrorContainer = OnError,
    outline = Color(0xFFCAC4D0),
    outlineVariant = Color(0xFFCAC4D0).copy(alpha = 0.15f),
)

private val DarkColorScheme = darkColorScheme(
    primary = CeladonPrimaryContainer,
    onPrimary = CeladonOnPrimaryContainer,
    primaryContainer = CeladonPrimary,
    onPrimaryContainer = CeladonOnPrimary,
    secondary = SageSecondaryContainer,
    onSecondary = SageOnSecondaryContainer,
    secondaryContainer = SageSecondary,
    onSecondaryContainer = SageOnSecondary,
    tertiary = Color(0xFF86D98A),
    onTertiary = Color(0xFF003910),
    background = SurfaceDark,
    onBackground = Color(0xFFE4E5F0),
    surface = SurfaceDark,
    onSurface = Color(0xFFE4E5F0),
    surfaceVariant = SurfaceContainerLowDark,
    onSurfaceVariant = Color(0xFFA0A4B8),
    surfaceContainer = SurfaceContainerLowDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    error = Color(0xFFFF6B6B),
    errorContainer = Color(0xFF4A1A1A),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFFFB4B4),
    outline = Color(0xFF4A4D6A),
    outlineVariant = Color(0xFF2A2D45),
    tertiaryContainer = Color(0xFF1A3A1F),
    onTertiaryContainer = Color(0xFF86D98A),
)

@Composable
fun PuriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    forceLightStatusBarIcons: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        DisposableEffect(darkTheme, forceLightStatusBarIcons) {
            val window = (view.context as ComponentActivity).window
            val controller = WindowInsetsControllerCompat(window, view)
            val useLightIcons = forceLightStatusBarIcons || darkTheme

            controller.isAppearanceLightStatusBars = !useLightIcons

            onDispose {
                val systemIsDark = view.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                controller.isAppearanceLightStatusBars = !systemIsDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PuriTypography,
        content = content
    )
}