package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.steamcheck.project.presentation.state.UserStatsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class UserStatsViewModel {
    private val _state = MutableStateFlow(UserStatsState())    val state: StateFlow<UserStatsState> get() = _state

    var steamID = mutableStateOf("")
        private set

    fun updateSteamID(newSteamID: String) {
        steamID.value = newSteamID
    }

    fun hasSteamID(): Boolean {
        return steamID.value.isNotBlank()
    }
}

@Composable
fun UserStatsView(viewModel: UserStatsViewModel) {
    val steamID by viewModel.steamID
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (viewModel.hasSteamID()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                Button(
                    onClick = { viewModel.updateSteamID("") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Se déconnecter")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Affichage modifié pour l'utilisateur avec Steam ID : $steamID",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SteamIDInputField(
                steamID = steamID,
                onSteamIDChange = {}, // Pas utilisé ici
                onSubmit = { inputSteamID ->
                    if (inputSteamID.isBlank()) {
                        errorMessage = "Le champ Steam ID ne peut pas être vide."
                    } else {
                        errorMessage = null
                        viewModel.updateSteamID(inputSteamID)
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