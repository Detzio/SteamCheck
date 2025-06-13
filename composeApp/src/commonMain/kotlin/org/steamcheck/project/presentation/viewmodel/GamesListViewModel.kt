package org.steamcheck.project.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.steamcheck.project.domain.model.Game
import org.steamcheck.project.domain.usecase.GetGamesUseCase
import org.steamcheck.project.domain.usecase.SearchGamesUseCase
import org.steamcheck.project.presentation.state.GamesListState
import org.steamcheck.project.presentation.ui.GamesListScreen

class GamesListViewModel(
    private val getGamesUseCase: GetGamesUseCase,
    private val searchGamesUseCase: SearchGamesUseCase
) {
    private val _state = MutableStateFlow(GamesListState())
    val state: StateFlow<GamesListState> get() = _state
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Nombre de jeux à charger par page
    private val pageSize = 25

    // Fonction pour charger les jeux et les trier
    fun loadGames() {
        // Éviter de charger les jeux si c'est déjà en cours
        if (_state.value.isLoading) return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                println("Chargement des jeux en cours...")
                val games = getGamesUseCase.execute(1, pageSize)
                println("Jeux chargés: ${games.size} jeux trouvés")
                
                // Tri des jeux par date (du plus récent au plus ancien)
                val sortedGames = sortGamesByReleaseDate(games)
                
                // Sélection des jeux en vedette (top 5 pour le carousel)
                val featuredGames = sortedGames.take(5)

                _state.value = _state.value.copy(
                    games = sortedGames,
                    featuredGames = featuredGames,
                    isLoading = false,
                    error = null,
                    currentPage = 1,
                    hasMorePages = games.size >= pageSize
                )
            } catch (e: Exception) {
                println("Erreur lors du chargement des jeux: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Erreur: ${e.message}. Veuillez réessayer."
                )
            }
        }
    }

    // Fonction pour charger tous les jeux disponibles
    fun loadAllGames() {
        // Éviter de charger les jeux si c'est déjà en cours
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                println("Chargement de tous les jeux en cours...")
                val games = getGamesUseCase.loadAllGames()
                println("Tous les jeux chargés: ${games.size} jeux trouvés")

                // Tri des jeux par date (du plus récent au plus ancien)
                val sortedGames = sortGamesByReleaseDate(games)

                // Sélection des jeux en vedette (top 5 pour le carousel)
                val featuredGames = sortedGames.take(5)

                _state.value = _state.value.copy(
                    games = sortedGames,
                    featuredGames = featuredGames,
                    isLoading = false,
                    error = null,
                    currentPage = 1,
                    hasMorePages = false  // Tous les jeux sont chargés
                )
            } catch (e: Exception) {
                println("Erreur lors du chargement de tous les jeux: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Erreur: ${e.message}. Veuillez réessayer."
                )
            }
        }
    }

    // Fonction pour charger la page suivante
    fun loadNextPage() {
        val currentState = _state.value

        // Vérifier si déjà en train de charger ou s'il n'y a plus de pages
        if (currentState.isLoadingMore || !currentState.hasMorePages) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            try {
                val nextPage = currentState.currentPage + 1
                println("Chargement de la page $nextPage")

                val newGames = getGamesUseCase.execute(nextPage, pageSize)

                // S'il n'y a plus de jeux, mettre à jour hasMorePages
                val hasMore = newGames.isNotEmpty() && newGames.size >= pageSize

                // Filtrer pour éviter les doublons
                val existingIds = currentState.games.map { it.id }.toSet()
                val uniqueNewGames = newGames.filter { it.id !in existingIds }

                // Trier et ajouter les nouveaux jeux
                val allGames = currentState.games + uniqueNewGames

                _state.value = _state.value.copy(
                    games = sortGamesByReleaseDate(allGames),
                    currentPage = nextPage,
                    isLoadingMore = false,
                    hasMorePages = hasMore
                )

                println("Page $nextPage chargée. ${uniqueNewGames.size} nouveaux jeux. Total: ${allGames.size} jeux.")
            } catch (e: Exception) {
                println("Erreur lors du chargement de la page suivante: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoadingMore = false,
                    error = "Erreur: ${e.message}. Veuillez réessayer."
                )
            }
        }
    }

    // Fonction pour trier les jeux par date de sortie (du plus récent au plus ancien)
    private fun sortGamesByReleaseDate(games: List<Game>): List<Game> {
        return games.sortedByDescending { game ->
            if (game.releaseDate.isBlank()) {
                // Si pas de date, placer à la fin
                "0000-00-00"
            } else {
                // Conserver la date telle quelle pour le tri (format supposé: YYYY-MM-DD ou similaire)
                game.releaseDate
            }
        }
    }

    // Fonction pour gérer la mise à jour de la requête de recherche
    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)

        if (query.isBlank()) {
            // Réinitialiser les résultats de recherche si la requête est vide
            _state.value = _state.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }

        // Déclencher la recherche avec un délai pour éviter trop de requêtes
        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)
            delay(500) // Délai pour éviter des recherches trop fréquentes

            if (_state.value.searchQuery == query) {
                try {
                    val results = searchGamesUseCase.execute(query)
                    // Tri des résultats par date également
                    val sortedResults = sortGamesByReleaseDate(results)

                    _state.value = _state.value.copy(
                        searchResults = sortedResults,
                        isSearching = false
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Erreur de recherche: ${e.message}",
                        isSearching = false
                    )
                }
            }
        }
    }

    // Fonction pour effacer la recherche
    fun clearSearch() {
        _state.value = _state.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false
        )
    }

    // Fonction pour recharger les jeux en cas d'erreur
    fun retryLoadingGames() {
        if (_state.value.error != null) {
            loadGames()
        }
    }
}
