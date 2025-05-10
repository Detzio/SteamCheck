package org.steamcheck.project.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
actual fun ImageLoader(
    url: String,
    contentDescription: String?,
    modifier: Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier
    )
}