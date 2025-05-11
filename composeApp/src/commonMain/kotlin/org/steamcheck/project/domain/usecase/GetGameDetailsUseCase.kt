package org.steamcheck.project.domain.usecase

import org.steamcheck.project.data.mapper.toGameDetail
import org.steamcheck.project.data.remote.StoreApi
import org.steamcheck.project.domain.model.GameDetail

class GetGameDetailsUseCase(
    private val api: StoreApi
) {
    suspend fun execute(gameId: String): GameDetail? {
        val gameDto = api.getGameDetails(gameId) ?: return null
        return gameDto.toGameDetail()
    }
}
