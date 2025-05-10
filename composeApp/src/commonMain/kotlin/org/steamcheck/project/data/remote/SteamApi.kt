package org.steamcheck.project.data.remote


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.steamcheck.project.data.remote.dto.UserDto
import org.steamcheck.project.utils.Constants

interface SteamApi {
    suspend fun getUser(steamId: String): UserDto
    suspend fun getUserStats(steamId: String): UserDto
    suspend fun getUserGames(steamId: String): List<UserDto>
    suspend fun getGameDetails(appId: String): UserDto
    suspend fun getGameNews(appId: String): UserDto
    suspend fun getGameAchievements(appId: String): UserDto
}

class SteamApiImpl(private val client: HttpClient = ApiClient.client) : SteamApi {
    override suspend fun getUser(steamId: String): UserDto {
        val response = client.get("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/") {
            parameter("key", Constants.STEAM_API_KEY)
            parameter("steamids", steamId)
        }.body<JsonObject>()

        val userData = response["response"]?.jsonObject?.get("players")?.jsonArray?.firstOrNull()?.jsonObject

        return userData?.let {
            UserDto(
                id = it["steamid"]?.jsonPrimitive?.content ?: "",
                username = it["personaname"]?.jsonPrimitive?.content ?: "",
                avatarUrl = it["avatarfull"]?.jsonPrimitive?.content ?: "",
                steamID = it["steamid"]?.jsonPrimitive?.content ?: ""

            )
        } ?: UserDto("", "", "", "")
    }

    override suspend fun getUserStats(steamId: String): UserDto {
        val response = client.get("https://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v2/") {
            parameter("key", Constants.STEAM_API_KEY)
            parameter("steamid", steamId)
        }.body<JsonObject>()

        val statsData = response["playerstats"]?.jsonObject

        return statsData?.let {
            UserDto(
                id = steamId,
                username = it["gameName"]?.jsonPrimitive?.content ?: "",
                avatarUrl = "",
                steamID = steamId
            )
        } ?: UserDto("", "", "", "")
    }

    override suspend fun getUserGames(steamId: String): List<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getGameDetails(appId: String): UserDto {
        TODO("Not yet implemented")
    }

    override suspend fun getGameNews(appId: String): UserDto {
        TODO("Not yet implemented")
    }

    override suspend fun getGameAchievements(appId: String): UserDto {
        TODO("Not yet implemented")
    }
}