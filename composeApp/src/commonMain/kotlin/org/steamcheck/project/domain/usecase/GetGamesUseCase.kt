package org.steamcheck.project.domain.usecase

import org.steamcheck.project.data.remote.StoreApi
import org.steamcheck.project.data.mapper.toDomain
import org.steamcheck.project.domain.model.Game

class GetGamesUseCase(
    private val api: StoreApi
) {
    suspend fun execute(page: Int = 1, limit: Int = 20): List<Game> {
        return api.getGames(page, limit).map { it.toDomain() }
    }

    // Méthode utilitaire pour charger plusieurs pages
    suspend fun loadPages(fromPage: Int, toPage: Int, limit: Int = 20): List<Game> {
        val results = mutableListOf<Game>()
        for (page in fromPage..toPage) {
            results.addAll(execute(page, limit))
        }
        return results
    }
    
    // Méthode pour charger tous les jeux disponibles
    suspend fun loadAllGames(maxPages: Int = 50): List<Game> {
        return api.getAllGames(maxPages).map { it.toDomain() }
    }
}
