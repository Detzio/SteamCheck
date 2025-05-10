package org.steamcheck.project.domain.usecase

import org.steamcheck.project.data.remote.SteamApi
import org.steamcheck.project.data.mapper.toDomain
import org.steamcheck.project.domain.model.User
import org.steamcheck.project.domain.model.UserStats

class GetUserStatsUseCase(
    private val api: SteamApi
) {
    suspend fun execute(steamId: String): User {
        return api.getUser(steamId).toDomain()
    }
}