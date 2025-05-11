package org.steamcheck.project.domain.model

data class GameDetail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val releaseDate: String,
    val developer: List<String> = emptyList(),
    val publisher: String = "",
    val price: Double = 0.0,
    val discountPercent: Int = 0,
    val platforms: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val background: String = "",
    val positiveRatings: Int = 0,
    val negativeRatings: Int = 0
)
