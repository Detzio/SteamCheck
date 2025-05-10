package org.steamcheck.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*
import org.steamcheck.project.data.remote.dto.GameDto

interface StoreApi {
    suspend fun getGames(): List<GameDto>
}

class StoreApiImpl(private val client: HttpClient = ApiClient.client) : StoreApi {
    override suspend fun getGames(): List<GameDto> {
        // Simple requête à l'API pour récupérer les jeux
        val response = client.get("https://store.steampowered.com/api/featuredcategories").body<JsonObject>()
        
        // Extraction des jeux depuis la réponse JSON
        val gamesList = mutableListOf<GameDto>()
        
        // On récupère la catégorie "specials" qui contient les jeux en promotion
        val specials = response["specials"]?.jsonObject
        val items = specials?.get("items")?.jsonArray
        
        items?.forEach { item ->
            val jsonObj = item.jsonObject
            val id = jsonObj["id"]?.jsonPrimitive?.content ?: ""
            val name = jsonObj["name"]?.jsonPrimitive?.content ?: ""
            val headerImage = jsonObj["header_image"]?.jsonPrimitive?.content ?: ""
            val finalPrice = jsonObj["final_price"]?.jsonPrimitive?.content ?: "0"
            val discountPercent = jsonObj["discount_percent"]?.jsonPrimitive?.int ?: 0
            
            gamesList.add(GameDto(id, name, headerImage, finalPrice, discountPercent))
        }
        
        return gamesList
    }
}
