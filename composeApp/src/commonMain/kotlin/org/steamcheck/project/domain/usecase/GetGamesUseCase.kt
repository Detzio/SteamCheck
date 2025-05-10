package org.steamcheck.project.domain.usecase

import org.steamcheck.project.data.remote.StoreApi
import org.steamcheck.project.data.mapper.toDomain
import org.steamcheck.project.domain.model.Game

class GetGamesUseCase(
    private val api: StoreApi
) {
    suspend fun execute(): List<Game> {
        return api.getGames().map { it.toDomain() }
    }
}
