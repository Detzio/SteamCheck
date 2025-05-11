package org.steamcheck.project.data.remote


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.steamcheck.project.data.remote.dto.UserDto
import org.steamcheck.project.data.remote.dto.UserGameDto
import org.steamcheck.project.utils.Constants

interface SteamApi {
    suspend fun getUser(steamId: String): UserDto
}

class SteamApiImpl(private val client: HttpClient = ApiClient.client) : SteamApi {
    override suspend fun getUser(steamId: String): UserDto {
        val response = client.get("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/") {
            parameter("key", Constants.STEAM_API_KEY)
            parameter("steamids", steamId)
        }.body<JsonObject>()

        val userData = response["response"]?.jsonObject?.get("players")?.jsonArray?.firstOrNull()?.jsonObject

        return userData?.let {
            val isPrivate = it["communityvisibilitystate"]?.jsonPrimitive?.int != 3
            val games = if (!isPrivate) {
                fetchOwnedGames(steamId) // Récupérer les jeux si le compte est public
            } else {
                emptyList()
            }

            UserDto(
                id = it["steamid"]?.jsonPrimitive?.content ?: "",
                username = it["personaname"]?.jsonPrimitive?.content ?: "",
                avatarUrl = it["avatarfull"]?.jsonPrimitive?.content ?: "",
                steamID = it["steamid"]?.jsonPrimitive?.content ?: "",
                isPrivate = isPrivate,
                games = games
            )
        } ?: UserDto("", "", "", "", false, emptyList())
    }

    private suspend fun fetchOwnedGames(steamId: String): List<UserGameDto> {
        val gameListResponse = client.get("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/") {
            parameter("key", Constants.STEAM_API_KEY)
            parameter("steamid", steamId)
            parameter("include_appinfo", true)
            parameter("include_played_free_games", true)
            parameter("format", "json")
        }.body<JsonObject>()

        val gamesArray = gameListResponse["response"]?.jsonObject?.get("games")?.jsonArray

        return gamesArray?.map { game ->
            val gameObj = game.jsonObject
            val appId = gameObj["appid"]?.jsonPrimitive?.content ?: ""
            val imageHash = gameObj["img_icon_url"]?.jsonPrimitive?.content ?: ""
            val imageUrl = if (imageHash.isNotEmpty()) {
                "https://media.steampowered.com/steamcommunity/public/images/apps/$appId/$imageHash.jpg"
            } else {
                ""
            }

            val achievementResponse = client.get("https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/") {
                parameter("key", Constants.STEAM_API_KEY)
                parameter("steamid", steamId)
                parameter("appid", appId.toIntOrNull() ?: 0)
            }.body<JsonObject>()

            val achievementArray = achievementResponse["playerstats"]
                ?.jsonObject
                ?.get("achievements")
                ?.jsonArray

            val totalSuccess = achievementArray?.size ?: 0
            val ownedSuccess = achievementArray
                ?.count { it.jsonObject["achieved"]?.jsonPrimitive?.int == 1 }
                ?: 0


            UserGameDto(
                id = appId,
                name = gameObj["name"]?.jsonPrimitive?.content ?: "",
                imageUrl = imageUrl,
                totalSuccess = totalSuccess,
                ownedSuccess = ownedSuccess,
                totalPlaytime = gameObj["playtime_forever"]?.jsonPrimitive?.int ?: 0,
            )
        } ?: emptyList()
    }
}