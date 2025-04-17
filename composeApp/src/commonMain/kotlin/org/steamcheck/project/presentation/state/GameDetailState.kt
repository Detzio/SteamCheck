package org.steamcheck.project.presentation.state

data class GameDetailState(
    val gameDetails: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
