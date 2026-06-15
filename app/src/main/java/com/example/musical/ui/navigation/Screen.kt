package com.example.musical.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object LikedSongs : Screen("liked_songs")
    object Playlist : Screen("playlist/{playlistId}") {
        fun createRoute(id: Long) = "playlist/$id"
    }
    object Profile : Screen("profile")
    object Player : Screen("player/{songId}") {
        fun createRoute(songId: String) = "player/$songId"
    }
}
