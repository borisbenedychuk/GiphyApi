package com.example.natifetesttask.presentation.theme.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.natifetesttask.presentation.utils.compose.isInLandScape

private val LightColorPalette = darkColors(
    primary = Color(0xFF000000),
    primaryVariant = Color(0xFF292828),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
)

private val DarkColorPalette = lightColors(
    primary = Color(0xFFA2A2A2),
    primaryVariant = Color(0xFF9B9B9B),
    background = Color(0xFF383838),
    surface = Color(0xFF242323),
)


@Composable
fun NatifeTestTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    val dimensions = if (isInLandScape()) LandscapeDimensions() else PortraitDimensions()
    CompositionLocalProvider(
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colors = colors.copy(error = Color(0xFFA30000)),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}