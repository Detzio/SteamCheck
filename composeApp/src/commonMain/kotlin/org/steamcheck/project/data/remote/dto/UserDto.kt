package org.steamcheck.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("steam_id") val steamID: String
)
