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

// Cette fonction permet une utilisation plus directe dans toutes les plateformes
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    SteamCheckTheme(content = content)
}
