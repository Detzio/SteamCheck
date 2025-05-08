package org.steamcheck.project.presentation

import androidx.compose.foundation.layout.Spacer
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.steamcheck.project.getPlatform
import org.steamcheck.project.presentation.viewmodel.GamesListView
import org.steamcheck.project.presentation.viewmodel.GamesListViewModel
import org.steamcheck.project.presentation.viewmodel.UserStatsView
import org.steamcheck.project.presentation.viewmodel.UserStatsViewModel

@Composable
fun FooterNavBar(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar {
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
fun HeaderNavBar(selected: Int, onSelect: (Int) -> Unit) {
    TopAppBar(
        title = {},
        actions = {
            TextButton(onClick = { onSelect(0) }) {
                Text("Bibliothèque", color = if (selected == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
            TextButton(onClick = { onSelect(1) }) {
                Text("Profil", color = if (selected == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}


@Composable
fun Navbar() {
    var selectedPage by remember { mutableStateOf(0) }
    val platform = getPlatform()

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
                    UserStatsView()
                }
            }
        }
        "Desktop" -> {
            Scaffold(
                topBar = {
                    HeaderNavBar(selected = selectedPage, onSelect = { selectedPage = it })
                }
            ) {
                if (selectedPage == 0) {
                    GamesListView()
                } else {
                    UserStatsView()
                }
            }
        }
    }
}