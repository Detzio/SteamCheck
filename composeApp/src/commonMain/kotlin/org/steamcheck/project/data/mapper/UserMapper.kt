package org.steamcheck.project.data.mapper

import org.steamcheck.project.data.remote.dto.UserDto
import org.steamcheck.project.data.remote.dto.UserStatsDto
import org.steamcheck.project.domain.model.User
import org.steamcheck.project.domain.model.UserStats

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl,
        steamID = this.steamID
    )
}

fun UserStatsDto.toDomain(): UserStats {
    return UserStats(
        userId = this.userId,
        totalGames = this.totalGames,
        totalPlaytime = this.totalPlaytime,
        favoriteGame = this.favoriteGame
    )
}
