package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OtoKlonDarkColorScheme = darkColorScheme(
    primary = LilacPrimary,
    onPrimary = LilacOnPrimary,
    primaryContainer = LilacPrimaryContainer,
    onPrimaryContainer = LilacOnPrimaryContainer,
    secondary = LilacSecondary,
    onSecondary = LilacOnSecondary,
    secondaryContainer = LilacSecondaryContainer,
    onSecondaryContainer = LilacOnSecondaryContainer,
    tertiary = LilacTertiary,
    onTertiary = LilacOnTertiary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = DarkSurfaceContainer,
    outline = Color(0xFF5A4870),
    outlineVariant = Color(0xFF3A2E4C)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Dark Theme as requested: "Lila mor, koyu tema"
    dynamicColor: Boolean = false, // Keep consistent custom lilac-mor branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OtoKlonDarkColorScheme,
        typography = Typography,
        content = content
    )
}
