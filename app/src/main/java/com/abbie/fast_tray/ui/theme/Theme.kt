package com.abbie.fast_tray.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//DARK MODE
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    secondary = SlateDark,
    tertiary = OrangeDark,
    background = SlateMedium,
    surface = SlateDark,
    onPrimary = WhiteSurface,
    onSecondary = WhiteSurface,
    onTertiary = WhiteSurface,
    onBackground = CreamBackground,
    onSurface = CreamBackground
)

//LIGHT MODE
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = SlateDark,
    tertiary = OrangeLight,
    background = CreamBackground,
    surface = WhiteSurface,
    onPrimary = WhiteSurface,
    onSecondary = WhiteSurface,
    onTertiary = SlateMedium,
    onBackground = SlateMedium,
    onSurface = SlateMedium
)

@Composable
fun FasttrayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.secondary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}