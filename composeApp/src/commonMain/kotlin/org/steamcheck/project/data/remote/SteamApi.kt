package org.steamcheck.project.data.remote

import org.steamcheck.project.data.remote.dto.GameDetailDto
import org.steamcheck.project.data.remote.dto.GameDto
import org.steamcheck.project.data.remote.dto.UserStatsDto

interface SteamApi {
    suspend fun getUserStats(userId: String): UserStatsDto
}
