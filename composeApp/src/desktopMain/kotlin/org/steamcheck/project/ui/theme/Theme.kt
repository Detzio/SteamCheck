package org.steamcheck.project.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF1A9FFF),
    primaryVariant = Color(0xFF0B76CC),
    secondary = Color(0xFF03DAC5)
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF1A9FFF),
    primaryVariant = Color(0xFF0B76CC),
    secondary = Color(0xFF03DAC5)
)

@Composable
fun SteamCheckTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
