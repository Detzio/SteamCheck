package org.steamcheck.project.domain.model

data class UserGame(
    val id: String,
    val name: String,
    val imageUrl: String,
    val totalSuccess: Int,
    val ownedSuccess: Int,
    val totalPlaytime: Int,
)
