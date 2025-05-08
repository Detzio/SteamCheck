package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.websocket.Frame
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

@Composable
fun GamesListView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bibliothèque des jeux")
        // Ajoute ici le reste de ta logique d'affichage de jeux
    }
}
