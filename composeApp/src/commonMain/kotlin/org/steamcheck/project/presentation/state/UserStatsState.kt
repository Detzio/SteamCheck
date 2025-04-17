package org.steamcheck.project.presentation.state

data class UserStatsState(
    val stats: Map<String, Any> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
