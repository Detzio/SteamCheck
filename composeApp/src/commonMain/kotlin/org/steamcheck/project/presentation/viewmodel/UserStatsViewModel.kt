package org.steamcheck.project.presentation.viewmodel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class UserStatsViewModel {
    // Pour l'instant vide, à compléter avec la logique appropriée
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    fun clear() {
        // Nettoyage des ressources
    }
}

@Composable
fun UserStatsView(viewModel: UserStatsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Profil Utilisateur",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Cette section est en cours de développement",
            modifier = Modifier.padding(16.dp)
        )
    }
}
