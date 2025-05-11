package org.steamcheck.project.domain.usecase

import org.steamcheck.project.data.remote.StoreApi
import org.steamcheck.project.data.mapper.toDomain
import org.steamcheck.project.domain.model.Game

class SearchGamesUseCase(
    private val api: StoreApi
) {
    suspend fun execute(query: String): List<Game> {
        if (query.isBlank()) return emptyList()
        
        val searchResults = api.searchGames(query)
        
        // Si aucun résultat, retourner une liste vide
        if (searchResults.isEmpty()) {
            return emptyList()
        }
        
        // Vérifier si les résultats de recherche ont des informations complètes
        val needsDetailedInfo = searchResults.any { game -> 
            game.finalPrice <= 0 || game.developer.isEmpty() || game.platforms.isEmpty() || game.genres.isEmpty() 
        }
        
        return if (needsDetailedInfo) {
            try {
                // Récupérer les IDs des jeux trouvés
                val gameIds = searchResults.map { game -> game.id }
                
                // Récupérer les détails complets pour chaque jeu trouvé
                val detailedGames = api.getGameDetails(gameIds)
                
                if (detailedGames.isNotEmpty()) {
                    // Créer une map des jeux détaillés pour faciliter la recherche
                    val detailedGamesMap = detailedGames.associateBy { game -> game.id }
                    
                    // Pour chaque résultat de recherche, utiliser les détails complets si disponibles
                    searchResults.map { searchResult ->
                        detailedGamesMap[searchResult.id]?.toDomain() ?: searchResult.toDomain()
                    }
                } else {
                    // Si aucun détail n'est disponible, utiliser les résultats de recherche
                    searchResults.map { game -> game.toDomain() }
                }
            } catch (e: Exception) {
                // En cas d'erreur lors de la récupération des détails, utiliser les résultats de recherche
                println("Erreur lors de la récupération des détails des jeux: ${e.message}")
                searchResults.map { game -> game.toDomain() }
            }
        } else {
            // Si les résultats contiennent déjà toutes les informations nécessaires
            searchResults.map { game -> game.toDomain() }
        }
    }
}
