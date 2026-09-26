package com.example.mios.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ImportScreen(navController: NavHostController, viewModel: ImportViewModel = viewModel()) {
    var hasPermission by remember { mutableStateOf(false) }
    var showGenreDialog by remember { mutableStateOf(false) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.loadUnimportedSongs()
    }

    LaunchedEffect(Unit) {
        launcher.launch(permission)
    }

    val unimportedSongs by viewModel.unimportedSongs.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Importar Nueva Canción", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        if (!hasPermission) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Se necesita permiso para leer tu música")
            }
            return@Column
        }

        if (unimportedSongs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay canciones nuevas por importar")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(unimportedSongs) { song ->
                    val isSelected = song.mediaStoreId in selectedIds
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = { Text(song.artist) },
                        leadingContent = {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { viewModel.toggleSelection(song.mediaStoreId) }
                            )
                        },
                        modifier = Modifier.clickable { viewModel.toggleSelection(song.mediaStoreId) }
                    )
                    HorizontalDivider()
                }
            }

            Button(
                onClick = { showGenreDialog = true },
                enabled = selectedIds.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Asignar género (${selectedIds.size} seleccionada${if (selectedIds.size != 1) "s" else ""})")
            }
        }
    }

    if (showGenreDialog) {
        GenreSelectionDialog(
            viewModel = viewModel,
            onDismiss = { showGenreDialog = false },
            onGenreConfirmed = { genreName ->
                viewModel.confirmGenreForSelection(genreName)
                showGenreDialog = false
            }
        )
    }
}

@Composable
fun GenreSelectionDialog(
    viewModel: ImportViewModel,
    onDismiss: () -> Unit,
    onGenreConfirmed: (String) -> Unit
) {
    val genres by viewModel.genres.collectAsState()
    var newGenreText by remember { mutableStateOf("") }
    var showAddGenre by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elige un género") },
        text = {
            Column {
                genres.forEach { genre ->
                    Text(
                        text = genre.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGenreConfirmed(genre.name) }
                            .padding(vertical = 12.dp)
                    )
                }

                HorizontalDivider()

                if (showAddGenre) {
                    OutlinedTextField(
                        value = newGenreText,
                        onValueChange = { newGenreText = it },
                        label = { Text("Nuevo género") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    Button(
                        onClick = {
                            if (newGenreText.isNotBlank()) {
                                viewModel.addNewGenre(newGenreText)
                                onGenreConfirmed(newGenreText)
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Crear y asignar")
                    }
                } else {
                    Text(
                        text = "+ Agregar nuevo género",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddGenre = true }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}