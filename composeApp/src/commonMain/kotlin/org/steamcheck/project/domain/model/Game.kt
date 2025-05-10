package org.steamcheck.project.domain.model

data class Game(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: String,
    val discountPercent: Int
)
