package org.steamcheck.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("header_image") val imageUrl: String,
    @SerialName("final_price") val price: String,
    @SerialName("discount_percent") val discountPercent: Int
)
