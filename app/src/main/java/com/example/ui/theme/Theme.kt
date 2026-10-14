package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FlipkartBlue,
    onPrimary = Color.White,
    primaryContainer = FlipkartLightBlue,
    onPrimaryContainer = FlipkartDarkBlue,
    secondary = FlipkartOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = FlipkartGreen,
    onTertiary = Color.White,
    background = BackgroundNeutral,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = FlipkartBlue,
    onPrimary = Color.White,
    primaryContainer = FlipkartDarkBlue,
    onPrimaryContainer = FlipkartLightBlue,
    secondary = FlipkartOrange,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF424242)
)

@Composable
fun BharatBazaarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = BharatBazaarTheme(darkTheme = darkTheme, content = content)
