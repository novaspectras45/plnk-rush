package com.Zihmaho6.Tekloc.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NeonScheme = darkColorScheme(
    primary = AccentMagenta,
    onPrimary = TextOnAccent,
    primaryContainer = AccentMagentaDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = BackgroundDeep,
    secondaryContainer = SurfaceWell,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentAmber,
    onTertiary = BackgroundDeep,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = SurfaceNight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextSecondary,
    outline = BorderIdle,
    outlineVariant = BorderIdle,
    error = AccentMagenta,
    onError = TextOnAccent
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeonScheme,
        typography = AppTypography,
        content = content
    )
}
