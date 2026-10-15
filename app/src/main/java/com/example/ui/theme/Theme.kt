package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DKartBlue,
    onPrimary = Color.White,
    primaryContainer = DKartLightBlue,
    onPrimaryContainer = DKartDarkBlue,
    secondary = DKartYellow,
    onSecondary = DKartDeepNavy,
    secondaryContainer = Color(0xFFFFF9C4),
    onSecondaryContainer = Color(0xFFF57F17),
    tertiary = DKartGreen,
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
    primary = DKartBlue,
    onPrimary = Color.White,
    primaryContainer = DKartDarkBlue,
    onPrimaryContainer = DKartLightBlue,
    secondary = DKartYellow,
    onSecondary = DKartDeepNavy,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF424242)
)

@Composable
fun DKartTheme(
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

// Backward compatibility aliases
@Composable
fun BharatBazaarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = DKartTheme(darkTheme = darkTheme, content = content)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = DKartTheme(darkTheme = darkTheme, content = content)
