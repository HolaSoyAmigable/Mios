package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mios.navigation.Screen

@Composable
fun GenresScreen(navController: NavHostController, viewModel: GenresViewModel = viewModel()) {
    val genres by viewModel.genres.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Géneros",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(genres) { genre ->
                ListItem(
                    headlineContent = { Text(genre.name) },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.GenreDetail.createRoute(genre.name))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun GenreDetailScreen(
    navController: NavHostController,
    genreName: String,
    libraryViewModel: LibraryViewModel,
    viewModel: GenresViewModel = viewModel()
) {
    val songsFlow = remember(genreName) { viewModel.getSongsForGenre(genreName) }
    val songs by songsFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            genreName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (songs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text("No hay canciones en este género", modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(songs) { index, song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = { Text(song.artist) },
                        modifier = Modifier.clickable { libraryViewModel.playList(songs, index) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}