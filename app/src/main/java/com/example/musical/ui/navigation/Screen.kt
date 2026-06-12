package com.example.musical.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object Player : Screen("player/{songId}") {
        fun createRoute(songId: String) = "player/$songId"
    }
}
