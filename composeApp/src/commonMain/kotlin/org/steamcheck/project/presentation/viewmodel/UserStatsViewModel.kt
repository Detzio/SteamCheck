package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.steamcheck.project.domain.model.UserGame
import org.steamcheck.project.domain.usecase.GetUserDataUseCase
import org.steamcheck.project.presentation.state.UserStatsState
import org.steamcheck.project.presentation.ui.ImageLoader
import org.steamcheck.project.presentation.ui.SteamDarkColorScheme

class UserStatsViewModel(
    private val getUserStatsUseCase: GetUserDataUseCase
) {
    private val _state = MutableStateFlow(UserStatsState())
    val state: StateFlow<UserStatsState> get() = _state

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var steamID = mutableStateOf("")
        private set

    fun updateSteamID(newSteamID: String) {
        steamID.value = newSteamID
    }

    fun clear() {
        steamID.value = ""
        _state.value = UserStatsState()
    }

    fun loadUserStats(steamID: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val stats = getUserStatsUseCase.execute(steamID)
                if (stats.isPrivate) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Votre compte est privé. Veuillez le rendre public et réessayer."
                    )
                } else {
                    _state.value = _state.value.copy(
                        stats = stats,
                        games = stats.games,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "SteamID invalide"
                )
            }
        }
    }
}


@Composable
fun UserStatsView(
    viewModel: UserStatsViewModel,
    platform: String
) {
    val state by viewModel.state.collectAsState()
    val steamID by viewModel.steamID
    var localError by remember { mutableStateOf<String?>(null) }

    val hasValidStats = state.stats?.username?.isNotBlank() == true && state.error == null
    if (hasValidStats) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = { viewModel.clear() },
                modifier = Modifier.align(Alignment.TopEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Se déconnecter")
            }
            UserDataView(
                stats = state,
                platform = platform,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    } else {

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        val errorMessage = state.error ?: localError
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (errorMessage == "Votre compte est privé. Veuillez le rendre public et réessayer.") {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = { viewModel.clear() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Revenir")
                }
            } else {
                SteamIDInputField(
                    steamID = steamID,
                    onSteamIDChange = {
                        viewModel.updateSteamID(it)
                        localError = null
                    },
                    onSubmit = { input ->
                        if (input.isBlank()) {
                            localError = "Le champ Steam ID ne peut pas être vide."
                        } else {
                            viewModel.updateSteamID(input)
                            viewModel.loadUserStats(input)
                        }
                    },
                    errorMessage = errorMessage
                )
            }
        }
    }
}


@Composable
fun SteamIDInputField(
    steamID: String,
    onSteamIDChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    errorMessage: String?
) {
    var inputSteamID by remember { mutableStateOf(steamID) }

    Text(
        text = "Renseignez votre Steam ID pour accéder à ce contenu :",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .widthIn(max = 400.dp)
            .padding(horizontal = 16.dp)
    ) {
        TextField(
            value = inputSteamID,
            onValueChange = { inputSteamID = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Entrez votre Steam ID",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            maxLines = 1,
            isError = errorMessage != null
        )
        Button(
            onClick = { onSubmit(inputSteamID) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("✔")
        }
    }
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Récupérez votre Steam ID sur votre page profil Steam.",
        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
}


@Composable
fun UserDataView(
    stats: UserStatsState,
    platform: String,
    modifier: Modifier = Modifier
) {
    val sortedGames = stats.games.sortedByDescending { game ->
        if (game.totalSuccess > 0) (game.ownedSuccess * 100) / game.totalSuccess else 0
    }


    Column(
        modifier = modifier
            .padding(top = 48.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ImageLoader(
                url = stats.stats?.avatarUrl.orEmpty(),
                contentDescription = "Avatar utilisateur",
                modifier = Modifier.size(64.dp)
            )
            Column {
                Text(
                    text = stats.stats?.username.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "SteamID : ${stats.stats?.steamID}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 1) Nombre total de jeux
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = "Total jeux",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text  = stats.games.size.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = SteamDarkColorScheme.primary
                )
            }

            // 2) Temps de jeu total
            val totalPlaytime = stats.games.sumOf { it.totalPlaytime }
            val hours = totalPlaytime / 60
            val minutes = totalPlaytime % 60
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = "Temps total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text  = buildString {
                        if (hours > 0) append("${hours}h ")
                        append("${minutes}m")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = SteamDarkColorScheme.primary
                )
            }

            // 3) Nombre de jeux à 100 %
            val fullCount = stats.games.count { it.ownedSuccess == it.totalSuccess && it.totalSuccess > 0 }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = "Jeux 100 %",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text  = fullCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = SteamDarkColorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Statistiques de jeux",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(sortedGames) { game ->
                GameCard(
                    game = game,
                    modifier = Modifier
                )
            }
        }
    }
}


@Composable
fun GameCard(
    game: UserGame,
    modifier: Modifier = Modifier
) {
    val hours = game.totalPlaytime / 60
    val minutes = game.totalPlaytime % 60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            ImageLoader(
                url = game.imageUrl,
                contentDescription = "Image du jeu",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Succès : ${game.ownedSuccess}/${game.totalSuccess}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Text(
                text = buildString {
                    if (hours > 0) append("${hours}h ")
                    append("${minutes}m")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
