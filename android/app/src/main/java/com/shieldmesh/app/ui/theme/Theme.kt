package com.shieldmesh.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ShieldMeshColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = DarkBackground,
    primaryContainer = GreenAccent.copy(alpha = 0.12f),
    onPrimaryContainer = GreenAccent,
    secondary = CyanAccent,
    onSecondary = DarkBackground,
    secondaryContainer = CyanAccent.copy(alpha = 0.12f),
    onSecondaryContainer = CyanAccent,
    tertiary = PurpleAccent,
    onTertiary = DarkBackground,
    tertiaryContainer = PurpleAccent.copy(alpha = 0.12f),
    onTertiaryContainer = PurpleAccent,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = CardBorderSubtle,
    error = CriticalRed,
    onError = DarkBackground,
    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground
)

@Composable
fun ShieldMeshTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = ShieldMeshColorScheme,
        typography = ShieldMeshTypography,
        content = content
    )
}
