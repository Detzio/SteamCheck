package org.steamcheck.project.presentation.state

import org.steamcheck.project.domain.model.Game

data class GamesListState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
