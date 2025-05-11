package org.steamcheck.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    @SerialName("appid") val id: String,
    @SerialName("name") val name: String,
    @SerialName("header_image") val headerImage: String,
    @SerialName("final_price") val finalPrice: Double,
    @SerialName("discount_percent") val discountPercent: Int,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("developer") val developer: List<String> = emptyList(),
    @SerialName("platforms") val platforms: List<String> = emptyList(),
    @SerialName("genres") val genres: List<String> = emptyList(),
    @SerialName("background") val background: String = ""
)
