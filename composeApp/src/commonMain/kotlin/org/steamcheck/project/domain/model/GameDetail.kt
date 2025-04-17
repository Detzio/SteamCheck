package org.steamcheck.project.domain.model

data class GameDetail(
    val id: String,
    val name: String,
    val description: String,
    val releaseDate: String,
    val developer: String,
    val publisher: String
)
