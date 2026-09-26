package com.example.mios.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mios.ui.screens.GenreDetailScreen
import com.example.mios.ui.screens.GenresScreen
import com.example.mios.ui.screens.ImportScreen
import com.example.mios.ui.screens.LibraryScreen
import com.example.mios.ui.screens.LibraryViewModel
import com.example.mios.ui.screens.PlayerControls
import com.example.mios.ui.screens.PlayerScreen
import androidx.compose.foundation.layout.padding

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val sharedViewModel: LibraryViewModel = viewModel()
    val currentSong by sharedViewModel.currentSong.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentSong != null) {
                PlayerControls(
                    viewModel = sharedViewModel,
                    onExpand = { navController.navigate(Screen.Player.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController, viewModel = sharedViewModel)
            }
            composable(Screen.Import.route) {
                ImportScreen(navController = navController)
            }
            composable(Screen.Player.route) {
                PlayerScreen(navController = navController, viewModel = sharedViewModel)
            }
            composable(Screen.Genres.route) {
                GenresScreen(navController = navController)
            }
            composable(
                route = Screen.GenreDetail.route,
                arguments = listOf(navArgument("genreName") { type = NavType.StringType })
            ) { backStackEntry ->
                val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
                GenreDetailScreen(
                    navController = navController,
                    genreName = genreName,
                    libraryViewModel = sharedViewModel
                )
            }
            composable(Screen.Playlists.route) {
                Text("Playlists — pendiente")
            }
            composable(Screen.Settings.route) {
                Text("Configuración — pendiente")
            }
        }
    }
}