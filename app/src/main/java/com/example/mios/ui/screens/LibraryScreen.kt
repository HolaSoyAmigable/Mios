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

        val currentSong by viewModel.currentSong.collectAsState()
        if (currentSong != null) {
            PlayerControls(
                viewModel = viewModel,
                onExpand = { navController.navigate(Screen.Player.route) }
            )
        }
    }
}

@Composable
fun PlayerControls(viewModel: LibraryViewModel, onExpand: () -> Unit) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
            .padding(16.dp)
    ) {
        Slider(
            value = if (duration > 0) position.toFloat() / duration.toFloat() else 0f,
            onValueChange = { fraction ->
                viewModel.seekTo((fraction * duration).toLong())
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Text(if (isPlaying) "Pausar" else "Reproducir")
            }
        }
    }
}
