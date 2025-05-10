package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.steamcheck.project.domain.usecase.GetGamesUseCase
import org.steamcheck.project.presentation.state.GamesListState

class GamesListViewModel(
    private val getGamesUseCase: GetGamesUseCase
) {
    private val _state = MutableStateFlow(GamesListState())
    val state: StateFlow<GamesListState> get() = _state
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Fonction simple pour charger les jeux
    fun loadGames() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val games = getGamesUseCase.execute()
                _state.value = _state.value.copy(
                    games = games,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clear() {
        viewModelScope.cancel()
    }
}

@Composable
fun GamesListView(viewModel: GamesListViewModel) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bibliothèque des jeux",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            items(state.games) { game ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = game.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (game.discountPercent > 0) {
                            Text(
                                text = "-${game.discountPercent}% : ${game.price}",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(text = game.price)
                        }
                    }
                }
            }
        }
    }
}
