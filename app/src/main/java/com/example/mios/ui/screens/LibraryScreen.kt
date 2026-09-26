package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mios.navigation.Screen

@Composable
fun LibraryScreen(navController: NavHostController, viewModel: LibraryViewModel) {
    val songs by viewModel.songs.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate(Screen.Import.route) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Importar Nueva Canción")
        }

        Button(
            onClick = { navController.navigate(Screen.Genres.route) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text("Ver Géneros")
        }

        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay canciones catalogadas todavía")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(songs) { song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = { Text(song.artist) },
                        modifier = Modifier.clickable { viewModel.playSong(song) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}