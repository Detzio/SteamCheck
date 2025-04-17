package org.steamcheck.project.presentation.state

data class GamesListState(
    val games: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
