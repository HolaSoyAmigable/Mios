package com.example.mios.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mios.ui.screens.ImportScreen
import com.example.mios.ui.screens.LibraryScreen
import com.example.mios.ui.screens.LibraryViewModel
import com.example.mios.ui.screens.PlayerScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val sharedViewModel: LibraryViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
        modifier = Modifier.fillMaxSize()
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
            Text("Géneros — pendiente")
        }
        composable(Screen.Playlists.route) {
            Text("Playlists — pendiente")
        }
        composable(Screen.Settings.route) {
            Text("Configuración — pendiente")
        }
    }
}