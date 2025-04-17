package org.steamcheck.project.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.steamcheck.project.presentation.state.GamesListState

class GamesListViewModel {
    private val _state = MutableStateFlow(GamesListState())
    val state: StateFlow<GamesListState> get() = _state

    fun loadGames() {
        // Implémentation pour charger la liste des jeux
    }
}
