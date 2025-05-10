package org.steamcheck.project.domain.model

data class User(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val steamID: Any
)
