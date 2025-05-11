package org.steamcheck.project.data.mapper

import org.steamcheck.project.data.remote.dto.UserDto
import org.steamcheck.project.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl,
        steamID = this.steamID,
        isPrivate = this.isPrivate,
        games = this.games.map { it.toDomain() }
    )
}


