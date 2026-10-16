package com.example.mios.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.mios.ui.screens.PlaylistDetailScreen
import com.example.mios.ui.screens.PlaylistsScreen
import com.example.mios.ui.screens.SettingsScreen
import com.example.mios.ui.screens.SettingsViewModel

@Composable
fun AppNavigation(
    settingsViewModel: SettingsViewModel,
    navController: NavHostController = rememberNavController()
) {
    val sharedViewModel: LibraryViewModel = viewModel()
    val currentSong by sharedViewModel.currentSong.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

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
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(Screen.Library.route) {
                LibraryScreen(
                    navController = navController,
                    viewModel = sharedViewModel,
                    username = settings?.username ?: ""
                )
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
                PlaylistsScreen(navController = navController)
            }
            composable(
                route = Screen.PlaylistDetail.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistDetailScreen(
                    navController = navController,
                    playlistId = playlistId,
                    libraryViewModel = sharedViewModel
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController, viewModel = settingsViewModel)
            }
        }
    }
}