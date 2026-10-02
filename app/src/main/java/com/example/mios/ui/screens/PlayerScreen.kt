package com.example.mios.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mios.player.AlbumArtUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PlayerScreen(navController: NavHostController, viewModel: LibraryViewModel) {
    val context = LocalContext.current
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isRepeatOneEnabled by viewModel.isRepeatOneEnabled.collectAsState()
    val hasNext by viewModel.hasNext.collectAsState()
    val hasPrevious by viewModel.hasPrevious.collectAsState()
    val currentPlaylistId by viewModel.currentPlaylistId.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var albumArt by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentSong?.mediaStoreId) {
        currentSong?.let { song ->
            albumArt = withContext(Dispatchers.IO) {
                AlbumArtUtil.getEmbeddedArt(context, song.mediaStoreId)
            }
        }
    }

    val song = currentSong ?: run {
        navController.popBackStack()
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val art = albumArt
            if (art != null) {
                Image(
                    bitmap = art.asImageBitmap(),
                    contentDescription = "Portada",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = "Sin portada",
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(song.title, style = MaterialTheme.typography.titleLarge)
                Text(song.genre, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.playPrevious() }, enabled = hasPrevious) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior")
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Reproducir/Pausar"
                )
            }
            IconButton(onClick = { viewModel.playNext() }, enabled = hasNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.toggleRepeatOne() }) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repetir",
                    tint = if (isRepeatOneEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
            IconButton(onClick = { }, enabled = false) {
                Icon(Icons.Default.Shuffle, contentDescription = "Aleatorio")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Borrar canción") },
            text = { Text("¿Qué quieres hacer con esta canción?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSongFromLibrary(song)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Borrar de toda la app")
                }
            },
            dismissButton = {
                if (currentPlaylistId != null) {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Quitar solo de la playlist")
                    }
                } else {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}