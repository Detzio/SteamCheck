package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.steamcheck.project.presentation.state.UserStatsState

class UserStatsViewModel {
    private val _state = MutableStateFlow(UserStatsState())
    val state: StateFlow<UserStatsState> get() = _state

    fun loadUserStats(userId: String) {
        // Implémentation pour charger les statistiques utilisateur
    }
}


@Composable
fun UserStatsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You need to be logged in")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /*TODO*/ }) {
            Text("Login")
        }
    }
}


