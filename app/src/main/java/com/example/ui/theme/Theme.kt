package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GymColorScheme = darkColorScheme(
    primary = GymCyan,
    onPrimary = DarkCyanOnPrimary,
    primaryContainer = GymCyanDim,
    onPrimaryContainer = TextPrimary,
    secondary = GymEmerald,
    onSecondary = DarkEmeraldOnSecondary,
    secondaryContainer = GymEmeraldDim,
    onSecondaryContainer = TextPrimary,
    tertiary = GymIndigo,
    background = GymDarkBg,
    onBackground = TextPrimary,
    surface = GymSurface,
    onSurface = TextPrimary,
    surfaceVariant = GymSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = GymRose,
    onError = TextPrimary
)

@Composable
fun IronPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Fitness app uses deep energetic dark theme for contrast and OLED performance
    val colorScheme = GymColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                if (android.os.Build.VERSION.SDK_INT < 35) {
                    @Suppress("DEPRECATION")
                    it.statusBarColor = GymDarkBg.toArgb()
                    @Suppress("DEPRECATION")
                    it.navigationBarColor = GymDarkBg.toArgb()
                }
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
