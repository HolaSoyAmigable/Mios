package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun PlaylistDetailScreen(
    navController: NavHostController,
    playlistId: Long,
    libraryViewModel: LibraryViewModel,
    viewModel: PlaylistDetailViewModel = viewModel()
) {
    val songsFlow = remember(playlistId) { viewModel.getSongs(playlistId) }
    val songs by songsFlow.collectAsState()
    val isRepeatAll by libraryViewModel.isRepeatAllEnabled.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text("Playlist", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { libraryViewModel.toggleRepeatAll() }) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repetir playlist",
                    tint = if (isRepeatAll) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }

        if (songs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text("Esta playlist no tiene canciones todavía", modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(songs) { index, song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = { Text(song.genre) },
                        trailingContent = {
                            Row {
                                IconButton(
                                    onClick = { viewModel.moveSong(playlistId, song.id, -1) },
                                    enabled = index > 0
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir")
                                }
                                IconButton(
                                    onClick = { viewModel.moveSong(playlistId, song.id, 1) },
                                    enabled = index < songs.size - 1
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Bajar")
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            libraryViewModel.playPlaylist(songs, index, playlistId)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
