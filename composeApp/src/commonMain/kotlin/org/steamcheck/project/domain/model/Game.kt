package org.steamcheck.project.domain.model

data class Game(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val discountPercent: Int,
    val releaseDate: String = "",
    val developer: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val background: String = ""
)
