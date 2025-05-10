package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.steamcheck.project.domain.usecase.GetUserStatsUseCase
import org.steamcheck.project.presentation.state.UserStatsState

class UserStatsViewModel(
    private val getUserStatsUseCase: GetUserStatsUseCase
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
                _state.value = _state.value.copy(
                    stats = stats,
                    isLoading = false,
                    error = null
                )
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

    val hasValidStats = state.stats?.username?.isNotBlank() == true
    if (hasValidStats) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Bouton Déconnexion en haut à droite
            Button(
                onClick = { viewModel.clear() },
                modifier = Modifier
                    .align(Alignment.TopEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Se déconnecter")
            }
            // Contenu utilisateur centré avec avatar et infos
            UserDataView(
                stats = state,
                platform = platform,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    } else {
        val errorMessage = state.error ?: localError
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
            if(platform == "Mobile") {
                // Afficher l'avatar de l'utilisateur pour mobile
                AsyncImage(
                    model = stats.stats?.avatarUrl,
                    contentDescription = "Avatar utilisateur",
                    modifier = Modifier.size(64.dp)
                )
            } else {
                // Afficher l'avatar de l'utilisateur pour desktop
                Image(
                    painter = rememberAsyncImagePainter(model = stats.stats?.avatarUrl),
                    contentDescription = "Avatar utilisateur",
                    modifier = Modifier.size(64.dp)
                )
            }
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
    }
}
