package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FarmGreenPrimaryDark,
    onPrimary = FarmBackgroundDark,
    primaryContainer = FarmGreenContainerDark,
    onPrimaryContainer = FarmGreenPrimaryDark,
    secondary = BotanicalTealContainer,
    onSecondary = FarmGreenOnPrimary,
    background = FarmBackgroundDark,
    surface = FarmSurfaceDark,
    onSurface = FarmOnSurfaceDark,
    surfaceVariant = FarmSurfaceVariantDark,
    onSurfaceVariant = FarmOnSurfaceDark,
    outline = FarmGreenPrimaryDark.copy(alpha = 0.3f),
    error = ErrorRed,
    errorContainer = ErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = FarmGreenOnPrimary,
    primaryContainer = FarmGreenContainer,
    onPrimaryContainer = FarmGreenOnContainer,
    secondary = EarthBrown,
    onSecondary = FarmGreenOnPrimary,
    secondaryContainer = EarthBrownContainer,
    background = FarmBackgroundLight,
    surface = FarmSurfaceLight,
    onSurface = FarmOnSurfaceLight,
    surfaceVariant = FarmSurfaceVariant,
    onSurfaceVariant = FarmOnSurfaceVariant,
    outline = FarmOutline,
    outlineVariant = FarmOutlineVariant,
    error = ErrorRed,
    errorContainer = ErrorContainer
)

@Composable
fun KisanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand emerald for agriculture
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
