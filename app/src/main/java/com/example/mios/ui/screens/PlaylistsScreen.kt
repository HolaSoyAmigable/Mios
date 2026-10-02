package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mios.navigation.Screen

@Composable
fun PlaylistsScreen(navController: NavHostController, viewModel: PlaylistsViewModel = viewModel()) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Playlists", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { showCreateDialog = true }) {
                Text("+ Nueva")
            }
        }

        if (playlists.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text("No tienes playlists todavía", modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(playlists) { playlist ->
                    ListItem(
                        headlineContent = { Text(playlist.name) },
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nueva playlist") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nombre") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createPlaylist(newName)
                    newName = ""
                    showCreateDialog = false
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }
}