package org.steamcheck.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*
import org.steamcheck.project.data.remote.dto.GameDto

interface StoreApi {
    suspend fun getGames(page: Int = 1, limit: Int = 20): List<GameDto>
    suspend fun getGameDetails(appId: String): GameDto?
    suspend fun getGameDetails(appIds: List<String>): List<GameDto>  // Nouvelle méthode pour gérer plusieurs IDs
    suspend fun searchGames(query: String): List<GameDto>
    suspend fun getAllGames(maxPages: Int = Int.MAX_VALUE): List<GameDto>
}

class StoreApiImpl(private val client: HttpClient = ApiClient.client) : StoreApi {
    // URL de base pour l'API Steam
    private val baseUrl = "https://store.steampowered.com/api"
    
    // Pour suivre les IDs déjà traités dans getAllGames
    private val processedIds = mutableSetOf<String>()
    
    // Fonction utilitaire pour normaliser les prix
    private fun normalizePrice(price: Double): Double {
        // Si le prix est supérieur à 1000€, on présume qu'il est en centimes
        return if (price > 1000.0 && price % 100.0 == 0.0) {
            price / 100.0
        } else {
            price
        }
    }

    override suspend fun getGames(page: Int, limit: Int): List<GameDto> {
        try {
            // Utilisation de l'API featured pour obtenir les jeux recommandés
            val response = client.get("$baseUrl/featuredcategories").body<JsonObject>()

            val gamesList = mutableListOf<GameDto>()
            val processedIds = mutableSetOf<String>() // Pour éviter les doublons dans cette requête

            // Extraction des jeux depuis "specials" qui contient les offres spéciales
            val specials = response["specials"]?.jsonObject?.get("items")?.jsonArray

            // Calculer l'index de début et de fin pour la pagination
            val startIndex = (page - 1) * limit
            val endIndex = startIndex + limit
            
            // Si nous n'avons plus de jeux à partir de l'index demandé, retourner une liste vide
            if (specials != null && startIndex < specials.size) {
                val pageItems = if (endIndex <= specials.size) {
                    specials.subList(startIndex, endIndex)
                } else {
                    specials.subList(startIndex, specials.size)
                }

                pageItems.forEach { item ->
                    try {
                        val gameJson = item.jsonObject
                        val id = gameJson["id"]?.jsonPrimitive?.content ?: ""
                        
                        // Vérifier que l'ID n'est pas déjà traité
                        if (id.isNotEmpty() && id !in processedIds) {
                            processedIds.add(id)
                            
                            val name = gameJson["name"]?.jsonPrimitive?.content ?: ""
                            val headerImage = gameJson["large_capsule_image"]?.jsonPrimitive?.content
                                ?: gameJson["small_capsule_image"]?.jsonPrimitive?.content ?: ""

                            // Gestion du prix et des réductions
                            val discountPercent = gameJson["discount_percent"]?.jsonPrimitive?.int ?: 0

                            // Récupérer le prix initial (non réduit)
                            val originalPrice = gameJson["original_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0

                            // Récupérer le prix final (avec réduction si applicable)
                            val finalPrice = gameJson["final_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: originalPrice
                            
                            // Normaliser le prix
                            val normalizedPrice = normalizePrice(finalPrice)

                            // Récupérer les détails du jeu pour obtenir plus d'informations
                            val gameDetails = getGameDetails(id)

                            val gameDto = if (gameDetails != null) {
                                // Si on a récupéré les détails, on utilise ces informations
                                gameDetails.copy(
                                    headerImage = headerImage.ifEmpty { gameDetails.headerImage },
                                    finalPrice = if (normalizedPrice > 0) normalizedPrice else normalizePrice(gameDetails.finalPrice),
                                    discountPercent = if (discountPercent > 0) discountPercent else gameDetails.discountPercent
                                )
                            } else {
                                // Sinon on crée un GameDto avec les informations limitées
                                GameDto(
                                    id = id,
                                    name = name,
                                    headerImage = headerImage,
                                    finalPrice = if (normalizedPrice > 0) normalizedPrice else normalizePrice(originalPrice),
                                    discountPercent = discountPercent
                                )
                            }

                            gamesList.add(gameDto)
                        }
                    } catch (e: Exception) {
                        // Ignorer les entrées non valides
                        println("Erreur lors du parsing d'un jeu: ${e.message}")
                    }
                }
            }

            // Compléter avec d'autres jeux populaires si nécessaire
            if (gamesList.size < limit) {
                try {
                    // On peut utiliser d'autres catégories pour compléter
                    val topSellers = response["top_sellers"]?.jsonObject?.get("items")?.jsonArray
                    
                    if (topSellers != null) {
                        val remainingItems = limit - gamesList.size
                        val topSellersList = topSellers.take(remainingItems)
                        
                        topSellersList.forEach { item ->
                            try {
                                val gameJson = item.jsonObject
                                val id = gameJson["id"]?.jsonPrimitive?.content ?: ""
                                
                                // Vérifier si ce jeu n'est pas déjà dans la liste
                                if (id.isNotEmpty() && id !in processedIds) {
                                    processedIds.add(id)
                                    
                                    val name = gameJson["name"]?.jsonPrimitive?.content ?: ""
                                    val headerImage = gameJson["large_capsule_image"]?.jsonPrimitive?.content
                                        ?: gameJson["small_capsule_image"]?.jsonPrimitive?.content ?: ""
                                    
                                    // Gestion des prix
                                    val discountPercent = gameJson["discount_percent"]?.jsonPrimitive?.int ?: 0
                                    val originalPrice = gameJson["original_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0
                                    val finalPrice = gameJson["final_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: originalPrice
                                    
                                    // Normaliser le prix
                                    val normalizedPrice = normalizePrice(finalPrice)
                                    
                                    val gameDetails = getGameDetails(id)
                                    
                                    val gameDto = if (gameDetails != null) {
                                        gameDetails.copy(
                                            headerImage = headerImage.ifEmpty { gameDetails.headerImage },
                                            finalPrice = if (normalizedPrice > 0) normalizedPrice else normalizePrice(gameDetails.finalPrice),
                                            discountPercent = if (discountPercent > 0) discountPercent else gameDetails.discountPercent
                                        )
                                    } else {
                                        GameDto(
                                            id = id,
                                            name = name,
                                            headerImage = headerImage,
                                            finalPrice = if (normalizedPrice > 0) normalizedPrice else normalizePrice(originalPrice),
                                            discountPercent = discountPercent
                                        )
                                    }
                                    
                                    gamesList.add(gameDto)
                                }
                            } catch (e: Exception) {
                                println("Erreur lors du parsing d'un jeu complémentaire: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Erreur lors de la récupération des jeux complémentaires: ${e.message}")
                }
            }

            return gamesList
        } catch (e: Exception) {
            println("Erreur lors de la récupération des jeux: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getGameDetails(appId: String): GameDto? {
        try {
            // Utilisation du bon endpoint pour les détails d'un jeu
            val response = client.get("$baseUrl/appdetails?appids=$appId").body<JsonObject>()

            // La réponse contient un objet avec l'ID de l'app comme clé
            val gameData = response[appId]?.jsonObject

            if (gameData?.get("success")?.jsonPrimitive?.boolean == true) {
                val data = gameData["data"]?.jsonObject ?: return null

                val id = appId
                val name = data["name"]?.jsonPrimitive?.content ?: ""
                val headerImage = data["header_image"]?.jsonPrimitive?.content ?: ""

                // Récupération du prix
                val priceData = data["price_overview"]?.jsonObject
                var finalPrice = 0.0
                var discountPercent = 0
                var originalPrice = 0.0

                if (priceData != null) {
                    // Récupération du prix original (non réduit) si disponible
                    originalPrice = priceData["initial"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0

                    // Récupération du prix final (avec réduction si applicable)
                    finalPrice = priceData["final"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0
                    discountPercent = priceData["discount_percent"]?.jsonPrimitive?.int ?: 0

                    // Si le prix final est 0 mais qu'il y a un prix original, utiliser celui-ci
                    if (finalPrice == 0.0 && originalPrice > 0.0) {
                        finalPrice = originalPrice
                    }
                    
                    // Normaliser le prix
                    finalPrice = normalizePrice(finalPrice)
                    originalPrice = normalizePrice(originalPrice)
                }

                // Autres informations
                val releaseDate = data["release_date"]?.jsonObject?.get("date")?.jsonPrimitive?.content ?: ""

                val developers = mutableListOf<String>()
                data["developers"]?.jsonArray?.forEach { dev ->
                    developers.add(dev.jsonPrimitive.content)
                }

                val platforms = mutableListOf<String>()
                data["platforms"]?.jsonObject?.entries?.forEach { (key, value) ->
                    if (value.jsonPrimitive.boolean) platforms.add(key)
                }

                val genres = mutableListOf<String>()
                data["genres"]?.jsonArray?.forEach { genre ->
                    genres.add(genre.jsonObject["description"]?.jsonPrimitive?.content ?: "")
                }

                val background = data["background"]?.jsonPrimitive?.content ?: ""

                return GameDto(
                    id = id,
                    name = name,
                    headerImage = headerImage,
                    finalPrice = finalPrice,
                    discountPercent = discountPercent,
                    releaseDate = releaseDate,
                    developer = developers,
                    platforms = platforms,
                    genres = genres,
                    background = background
                )
            }
            return null
        } catch (e: Exception) {
            println("Erreur lors de la récupération des détails du jeu: ${e.message}")
            return null
        }
    }

    override suspend fun getGameDetails(appIds: List<String>): List<GameDto> {
        val results = mutableListOf<GameDto>()
        
        for (appId in appIds) {
            getGameDetails(appId)?.let { gameDto ->
                results.add(gameDto)
            }
        }
        
        return results
    }

    override suspend fun searchGames(query: String): List<GameDto> {
        try {
            // Utilisation de l'API de recherche Steam
            val response = client.get("$baseUrl/storesearch/") {
                parameter("term", query)
                parameter("l", "french")
                parameter("cc", "FR")
            }.body<JsonObject>()

            val gamesList = mutableListOf<GameDto>()

            // Extraction des résultats de recherche
            val items = response["items"]?.jsonArray

            items?.forEach { item ->
                try {
                    val gameJson = item.jsonObject
                    val id = gameJson["id"]?.jsonPrimitive?.content
                        ?: gameJson["appid"]?.jsonPrimitive?.content ?: ""
                    val name = gameJson["name"]?.jsonPrimitive?.content ?: ""
                    val headerImage = gameJson["tiny_image"]?.jsonPrimitive?.content ?: ""

                    // Récupération des prix si disponibles
                    val discountPercent = gameJson["discount_percent"]?.jsonPrimitive?.int ?: 0

                    // Récupérer le prix original (non réduit)
                    val originalPrice = gameJson["original_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0

                    // Récupérer le prix final (avec réduction si applicable)
                    var finalPrice = gameJson["final_price"]?.jsonPrimitive?.int?.toDouble()?.div(100) ?: 0.0
                    
                    // Si le prix final est 0 mais qu'il y a un prix original, utiliser celui-ci
                    if (finalPrice == 0.0 && originalPrice > 0.0) {
                        finalPrice = originalPrice
                    }
                    
                    // Normaliser le prix pour éviter les erreurs d'affichage
                    finalPrice = normalizePrice(finalPrice)

                    // Vérification que c'est bien un jeu
                    if (id.isNotEmpty() && name.isNotEmpty()) {
                        gamesList.add(
                            GameDto(
                                id = id,
                                name = name,
                                headerImage = headerImage,
                                finalPrice = finalPrice,
                                discountPercent = discountPercent
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Ignorer les entrées non valides
                    println("Erreur lors du parsing d'un résultat de recherche: ${e.message}")
                }
            }

            return gamesList
        } catch (e: Exception) {
            println("Erreur lors de la recherche de jeux: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getAllGames(maxPages: Int): List<GameDto> {
        val allGames = mutableListOf<GameDto>()
        var currentPage = 1
        var hasMoreGames = true
        processedIds.clear() // Réinitialiser la liste des IDs traités

        while (hasMoreGames && currentPage <= maxPages) {
            val games = getGames(page = currentPage)
            if (games.isEmpty()) {
                hasMoreGames = false
            } else {
                // Filtrer pour n'ajouter que les jeux avec des IDs uniques
                val uniqueGames = games.filter { game -> 
                    if (game.id in processedIds) {
                        false
                    } else {
                        processedIds.add(game.id)
                        true
                    }
                }
                
                allGames.addAll(uniqueGames)
                currentPage++
                println("Chargé ${uniqueGames.size} jeux uniques de la page $currentPage. Total: ${allGames.size}")
                
                // Arrêter si on n'a pas trouvé de nouveaux jeux
                if (uniqueGames.isEmpty()) {
                    hasMoreGames = false
                }
            }
        }

        return allGames
    }
}
