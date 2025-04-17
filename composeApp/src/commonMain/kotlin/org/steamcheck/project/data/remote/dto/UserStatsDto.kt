package org.steamcheck.project.data.remote.dto

data class UserStatsDto(
    val userId: String,
    val totalGames: Int,
    val totalPlaytime: Int, // en minutes
    val favoriteGame: String
)
