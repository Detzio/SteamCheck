package org.steamcheck.project.data.mapper

import org.steamcheck.project.data.remote.dto.GameDto
import org.steamcheck.project.domain.model.Game

fun GameDto.toDomain(): Game {
    return Game(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl
    )
}
