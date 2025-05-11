package org.steamcheck.project.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ImageLoader(
    url: String,
    contentDescription: String?,
    modifier: Modifier
)
