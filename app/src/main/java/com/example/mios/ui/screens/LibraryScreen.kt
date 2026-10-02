package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mios.data.database.Song
import com.example.mios.navigation.Screen

@Composable
fun LibraryScreen(navController: NavHostController, viewModel: LibraryViewModel) {
    val songs by viewModel.songs.collectAsState()
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate(Screen.Import.route) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Importar Nueva Canción")
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Button(
                onClick = { navController.navigate(Screen.Genres.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Géneros")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate(Screen.Playlists.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Playlists")
            }
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
                        trailingContent = {
                            IconButton(onClick = { songToAddToPlaylist = song }) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = "Añadir a playlist")
                            }
                        },
                        modifier = Modifier.clickable { viewModel.playSong(song) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    songToAddToPlaylist?.let { song ->
        AddToPlaylistDialog(
            song = song,
            onDismiss = { songToAddToPlaylist = null }
        )
    }
}

@Composable
fun AddToPlaylistDialog(
    song: Song,
    onDismiss: () -> Unit,
    viewModel: PlaylistsViewModel = viewModel()
) {
    val playlists by viewModel.playlists.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir a playlist") },
        text = {
            Column {
                if (playlists.isEmpty()) {
                    Text("No tienes playlists todavía. Crea una desde la sección Playlists.")
                } else {
                    playlists.forEach { playlist ->
                        Text(
                            text = playlist.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.addSongToPlaylist(playlist.id, song.id)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}