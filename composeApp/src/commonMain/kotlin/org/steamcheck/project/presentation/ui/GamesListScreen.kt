package org.steamcheck.project.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.steamcheck.project.domain.model.Game
import org.steamcheck.project.presentation.state.GamesListState
import org.steamcheck.project.presentation.viewmodel.GamesListViewModel

@Composable
fun GamesListScreen(
    viewModel: GamesListViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()

    // Effet pour détecter quand on atteint la fin de la liste et charger plus de jeux
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            if (lazyListState.layoutInfo.totalItemsCount == 0) return@snapshotFlow false

            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastIndex = lazyListState.layoutInfo.totalItemsCount - 1

            lastVisibleItem != null && lastVisibleItem.index >= lastIndex - 2
        }.collect { isAtBottom ->
            if (isAtBottom && !state.isLoadingMore && state.hasMorePages) {
                viewModel.loadNextPage()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadGames()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bibliothèque des jeux",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        // Champ de recherche
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                state = state,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClearSearch = { viewModel.clearSearch() },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.loadAllGames() },
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Charger tous les jeux"
                )
            }
        }

        if (state.isLoading && state.games.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        state.error?.let { error ->
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(onClick = { viewModel.retryLoadingGames() }) {
                    Text("Réessayer")
                }
            }
        }

        // Afficher les jeux en vedette dans le carousel (seulement si pas en mode recherche)
        if (state.searchQuery.isBlank() && state.featuredGames.isNotEmpty()) {
            Text(
                "Jeux en vedette",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.Start)
            )

            GameCarousel(games = state.featuredGames)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Tous les jeux",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.Start)
            )
        }

        // Afficher les résultats de recherche si une recherche est active, sinon afficher tous les jeux
        val gamesDisplay = if (state.searchQuery.isNotBlank()) state.searchResults else state.games

        // Utilisation du lazy column pour pouvoir paginer
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(gamesDisplay) { game ->
                GameCard(
                    game = game,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Afficher un indicateur de chargement en bas si on charge plus de jeux
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameCarousel(games: List<Game>) {
    if (games.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucun jeu trouvé",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { games.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Carousel principal
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(250.dp)
        ) { page ->
            GameCard(
                game = games[page],
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Indicateurs de page
        Row(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(games.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            color = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Carousel inférieur (miniatures)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(games.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(80.dp)
                        .height(45.dp)
                        .background(
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    ImageLoader(
                        url = games[index].imageUrl,
                        contentDescription = "Miniature de ${games[index].name}",
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    modifier: Modifier = Modifier
) {
    val discountColor = Color(0xFFFFC107) // Jaune pour les réductions

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image du jeu
            if (game.imageUrl.isNotEmpty()) {
                ImageLoader(
                    url = game.imageUrl,
                    contentDescription = "Image du jeu ${game.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Titre du jeu
            Text(
                text = game.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Informations sur le prix et les réductions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (game.price > 0) {
                    // Prix avec ou sans réduction
                    Text(
                        text = "${game.price}€",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    if (game.discountPercent > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = discountColor,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "-${game.discountPercent}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Jeu gratuit
                    Text(
                        text = "Gratuit",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4CAF50), // Vert pour les jeux gratuits
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Date de sortie
                if (game.releaseDate.isNotEmpty()) {
                    Text(
                        text = "Sortie: ${game.releaseDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Développeurs
            if (game.developer.isNotEmpty()) {
                Text(
                    text = "Développeur: ${game.developer.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Genres
            if (game.genres.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    game.genres.take(3).forEach { genre ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (game.genres.size > 3) {
                        Text(
                            text = "+${game.genres.size - 3}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SearchBar(
    state: GamesListState,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = state.searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text("Rechercher un jeu...") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Recherche") },
        trailingIcon = {
            if (state.searchQuery.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    )

    if (state.isSearching) {
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

