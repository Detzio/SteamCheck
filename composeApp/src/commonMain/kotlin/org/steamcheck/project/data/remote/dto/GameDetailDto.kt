package org.steamcheck.project.data.remote.dto

data class GameDetailDto(
    val id: String,
    val name: String,
    val description: String,
    val releaseDate: String,
    val developer: String,
    val publisher: String,
    val imageUrl: String
)
