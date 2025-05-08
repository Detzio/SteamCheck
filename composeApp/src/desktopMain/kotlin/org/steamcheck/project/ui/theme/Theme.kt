package org.steamcheck.project.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF1A9FFF),
    secondary = Color(0xFF03DAC5)
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF1A9FFF),
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
        colorScheme = colors,
        content = content
    )
}
ommit -m