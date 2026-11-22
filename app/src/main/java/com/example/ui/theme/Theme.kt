package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KidLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = SkyBlueDark,
    secondary = KiwiGreen,
    onSecondary = Color.White,
    secondaryContainer = KiwiGreenContainer,
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = OrangeFun,
    onTertiary = Color.White,
    tertiaryContainer = OrangeContainer,
    onTertiaryContainer = Color(0xFFE65100),
    background = SoftCloudBackground,
    onBackground = TextDarkNavy,
    surface = CardBackgroundWhite,
    onSurface = TextDarkNavy,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSubtitleGrey
)

private val KidDarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    onPrimary = Color(0xFF003258),
    primaryContainer = SkyBlueDark,
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = KiwiGreenLight,
    onSecondary = Color(0xFF00390A),
    secondaryContainer = Color(0xFF005313),
    onSecondaryContainer = Color(0xFFA6F5A3),
    tertiary = SunnyYellowLight,
    onTertiary = Color(0xFF422B00),
    tertiaryContainer = Color(0xFF5F3F00),
    onTertiaryContainer = Color(0xFFFFDEA3),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent playful branding across all devices
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KidDarkColorScheme else KidLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
