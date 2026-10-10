package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLavender,
    onPrimary = MidnightNavyDark,
    primaryContainer = RichVioletDark,
    onPrimaryContainer = ElectricLavenderLight,
    secondary = NeonCyan,
    onSecondary = MidnightNavyDark,
    secondaryContainer = NeonCyanSubtle,
    onSecondaryContainer = NeonCyanGlow,
    tertiary = AmberWarning,
    onTertiary = MidnightNavyDark,
    background = MidnightNavyDark,
    onBackground = TextPrimaryDark,
    surface = MidnightNavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = MidnightNavyBorder,
    error = CoralDisruption,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RichViolet,
    onPrimary = Color.White,
    primaryContainer = ElectricLavenderLight,
    onPrimaryContainer = RichVioletDark,
    secondary = NeonCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF006064),
    tertiary = AmberWarning,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    error = CoralDisruption,
    onError = Color.White
)

@Composable
fun VaygoTheme(
    darkTheme: Boolean = true, // Default to sleek midnight navy dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
