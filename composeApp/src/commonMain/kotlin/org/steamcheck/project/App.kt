package org.steamcheck.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.steamcheck.project.data.remote.SteamApiImpl
import org.steamcheck.project.data.remote.StoreApiImpl
import org.steamcheck.project.domain.usecase.GetGamesUseCase
import org.steamcheck.project.domain.usecase.GetUserStatsUseCase
import org.steamcheck.project.presentation.Navbar
import org.steamcheck.project.presentation.ui.SteamCheckTheme
import org.steamcheck.project.presentation.viewmodel.GamesListViewModel
import org.steamcheck.project.presentation.viewmodel.UserStatsViewModel

@Composable
fun App() {
    // Configuration du client HTTP avec le plugin de négociation de contenu JSON
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
    
    // Initialisation de l'architecture
    val api = remember { StoreApiImpl(client) }
    val getGamesUseCase = remember { GetGamesUseCase(api) }
    val gamesListViewModel = remember { GamesListViewModel(getGamesUseCase) }

    // Initialisation de l'utilisateur
    val userApi = remember { SteamApiImpl(client) }
    val userStatsUseCase = remember { GetUserStatsUseCase(userApi) }
    val userStatsViewModel = remember { UserStatsViewModel(userStatsUseCase) }
    
    // Application du thème à toute l'application
    SteamCheckTheme {
        // Affichage de la navbar avec les dépendances déjà initialisées
        Navbar(gamesListViewModel, userStatsViewModel)
    }
}
