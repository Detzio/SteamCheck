package org.steamcheck.project.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SteamCheckTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = SteamDarkColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}