package org.steamcheck.project.presentation.state

import org.steamcheck.project.domain.model.Game

data class GamesListState(
    val games: List<Game> = emptyList(),
    val featuredGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Game> = emptyList(),
    val isSearching: Boolean = false,
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true
)
