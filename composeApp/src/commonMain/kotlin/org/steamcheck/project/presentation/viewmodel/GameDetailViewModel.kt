package org.steamcheck.project.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.steamcheck.project.presentation.state.GameDetailState

class GameDetailViewModel {
    private val _state = MutableStateFlow(GameDetailState())
    val state: StateFlow<GameDetailState> get() = _state

    fun loadGameDetails(gameId: String) {
        // Implémentation pour charger les détails d'un jeu
    }
}
