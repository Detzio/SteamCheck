package org.steamcheck.project.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.steamcheck.project.presentation.state.UserStatsState

class UserStatsViewModel {
    private val _state = MutableStateFlow(UserStatsState())
    val state: StateFlow<UserStatsState> get() = _state

    fun loadUserStats(userId: String) {
        // Implémentation pour charger les statistiques utilisateur
    }
}
