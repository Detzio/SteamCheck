package org.steamcheck.project.presentation.state

import org.steamcheck.project.domain.model.User

data class UserStatsState(
    val stats: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
