package com.battuk.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = BattukGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = BattukGreenSurfaceLight,
    onPrimaryContainer = BattukGreenDark,
    secondary = BattukOrange,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = BattukOrangeLight,
    background = androidx.compose.ui.graphics.Color(0xFFF7FBF4),
    surface = androidx.compose.ui.graphics.Color.White,
    error = ExpenseRed
)

private val DarkColors = darkColorScheme(
    primary = BattukGreenLight,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = BattukGreenDark,
    onPrimaryContainer = BattukGreenSurfaceLight,
    secondary = BattukOrange,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    background = DarkBackground,
    surface = DarkSurface,
    error = ExpenseRed
)

// Soft, rounded, Duolingo-style corners throughout the app
private val BattukShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

enum class ThemeSetting { LIGHT, DARK, SYSTEM }

fun com.battuk.app.util.AppThemeMode.toThemeSetting(): ThemeSetting = when (this) {
    com.battuk.app.util.AppThemeMode.LIGHT -> ThemeSetting.LIGHT
    com.battuk.app.util.AppThemeMode.DARK -> ThemeSetting.DARK
    com.battuk.app.util.AppThemeMode.SYSTEM -> ThemeSetting.SYSTEM
}

@Composable
fun BattukTheme(
    themeSetting: ThemeSetting = ThemeSetting.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val colors = if (useDark) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = BattukTypography,
        shapes = BattukShapes,
        content = content
    )
}
