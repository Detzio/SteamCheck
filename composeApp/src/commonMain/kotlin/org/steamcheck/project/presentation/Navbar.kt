package org.steamcheck.project.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.steamcheck.project.getPlatform
import org.steamcheck.project.presentation.viewmodel.GamesListView
import org.steamcheck.project.presentation.viewmodel.GamesListViewModel
import org.steamcheck.project.presentation.viewmodel.UserStatsView
import org.steamcheck.project.presentation.viewmodel.UserStatsViewModel

@Composable
fun FooterNavBar(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = { onSelect(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Bibliothèque") },
            label = { Text("Bibliothèque") }
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = { onSelect(1) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderNavBar(selected: Int, onSelect: (Int) -> Unit, onLogout: () -> Unit, hasSteamID: Boolean) {
    TopAppBar(
        title = {},
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    TextButton(onClick = { onSelect(0) }) {
                        Icon(Icons.Default.Home, contentDescription = "Bibliothèque")
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Bibliothèque", color = if (selected == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onSelect(1) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Profil", color = if (selected == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                }
                if (hasSteamID) {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Se déconnecter")
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}


@Composable
fun Navbar() {
    var selectedPage by remember { mutableStateOf(0) }
    val platform = getPlatform()
    val userStatsViewModel = remember { UserStatsViewModel() }

    val onLogout = { userStatsViewModel.updateSteamID("") }
    val hasSteamID by userStatsViewModel.steamID

    when (platform.platform) {
        "Mobile" -> {
            Scaffold(
                bottomBar = {
                    FooterNavBar(selected = selectedPage, onSelect = { selectedPage = it })
                }
            ) {
                if (selectedPage == 0) {
                    GamesListView()
                } else {
                    UserStatsView(viewModel = userStatsViewModel)
                }
            }
        }
        "Desktop" -> {
            Scaffold(
                topBar = {
                    HeaderNavBar(
                        selected = selectedPage,
                        onSelect = { selectedPage = it },
                        onLogout = onLogout,
                        hasSteamID = hasSteamID.isNotBlank()
                    )
                }
            ) {
                if (selectedPage == 0) {
                    GamesListView()
                } else {
                    UserStatsView(viewModel = userStatsViewModel)
                }
            }
        }
    }
}