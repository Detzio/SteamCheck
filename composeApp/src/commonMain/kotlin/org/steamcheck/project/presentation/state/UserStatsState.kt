package org.steamcheck.project.presentation.state

import org.steamcheck.project.domain.model.User
import org.steamcheck.project.domain.model.UserGame

data class UserStatsState(
    val stats: User? = null,
    val games: List<UserGame> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
