package org.steamcheck.project.data.mapper

import org.steamcheck.project.data.remote.dto.GameDto
import org.steamcheck.project.data.remote.dto.UserGameDto
import org.steamcheck.project.domain.model.Game
import org.steamcheck.project.domain.model.UserGame

fun GameDto.toDomain(): Game {
    return Game(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        price = this.price,
        discountPercent = this.discountPercent
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
