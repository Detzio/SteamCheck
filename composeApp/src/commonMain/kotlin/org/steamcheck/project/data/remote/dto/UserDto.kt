package org.steamcheck.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("steam_id") val steamID: String,
    @SerialName("is_private") val isPrivate: Boolean,
    @SerialName("games") val games: List<UserGameDto>,
)

@Serializable
data class UserGameDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("header_image") val imageUrl: String,
    @SerialName("total_success") val totalSuccess: Int,
    @SerialName("owned_success") val ownedSuccess: Int,
    @SerialName("total_playtime")  val totalPlaytime: Int,
)
