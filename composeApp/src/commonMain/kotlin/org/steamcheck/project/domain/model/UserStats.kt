package org.steamcheck.project.domain.model

data class UserStats(
    val userId: String,
    val totalPlaytime: Int,
    val favoriteGame: String,
    val totalGames: Int
)
