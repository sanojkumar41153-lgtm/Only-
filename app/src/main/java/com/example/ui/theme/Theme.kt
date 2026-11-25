package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = GemCyan,
    onPrimary = VaultMidnight,
    primaryContainer = Color(0xFF0C3554),
    onPrimaryContainer = Color(0xFFC8ECFF),
    secondary = GemEmerald,
    onSecondary = VaultMidnight,
    secondaryContainer = Color(0xFF0A3A2E),
    onSecondaryContainer = Color(0xFFBAF8E0),
    tertiary = GemGold,
    onTertiary = VaultMidnight,
    tertiaryContainer = Color(0xFF3E2D08),
    onTertiaryContainer = GemGoldSoft,
    background = VaultMidnight,
    onBackground = TextPrimaryDark,
    surface = VaultSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = VaultSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerHigh = VaultSurfaceHigh,
    outline = OutlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = GemCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F0FF),
    onPrimaryContainer = Color(0xFF052E47),
    secondary = GemEmeraldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    background = FrostBackground,
    onBackground = TextPrimaryLight,
    surface = FrostSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = FrostSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerHigh = Color(0xFFEDF3FA),
    outline = OutlineLight,
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun GemRewardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
