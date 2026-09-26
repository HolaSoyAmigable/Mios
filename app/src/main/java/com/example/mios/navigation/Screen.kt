package com.example.mios.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Import : Screen("import")
    object Genres : Screen("genres")
    object GenreDetail : Screen("genre_detail/{genreName}") {
        fun createRoute(genreName: String) = "genre_detail/$genreName"
    }
    object Playlists : Screen("playlists")
    object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
    }
    object Player : Screen("player")
    object Settings : Screen("settings")
    object Customize : Screen("customize")
    object PasswordSetup : Screen("password_setup")
}