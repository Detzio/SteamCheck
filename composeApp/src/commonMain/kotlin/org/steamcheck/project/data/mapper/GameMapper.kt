package org.steamcheck.project.data.mapper

import org.steamcheck.project.data.remote.dto.GameDto
import org.steamcheck.project.data.remote.dto.UserGameDto
import org.steamcheck.project.domain.model.Game
import org.steamcheck.project.domain.model.GameDetail
import org.steamcheck.project.domain.model.UserGame

fun GameDto.toDomain(): Game {
    return Game(
        id = this.id,
        name = this.name,
        imageUrl = this.headerImage,
        price = this.finalPrice,
        discountPercent = this.discountPercent,
        releaseDate = this.releaseDate,
        developer = this.developer,
        platforms = this.platforms,
        genres = this.genres,
        background = this.background
    )
}

fun GameDto.toGameDetail(): GameDetail {
    return GameDetail(
        id = this.id,
        name = this.name,
        imageUrl = this.headerImage,
        description = "", // L'API actuelle ne fournit pas la description
        releaseDate = this.releaseDate,
        developer = this.developer,
        publisher = this.developer.firstOrNull() ?: "",
        price = this.finalPrice,
        discountPercent = this.discountPercent,
        platforms = this.platforms,
        genres = this.genres,
        background = this.background
    )
}

fun UserGameDto.toDomain(): UserGame {
    return UserGame(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        totalSuccess = this.totalSuccess,
        ownedSuccess = this.ownedSuccess,
        totalPlaytime = this.totalPlaytime
    )
}
