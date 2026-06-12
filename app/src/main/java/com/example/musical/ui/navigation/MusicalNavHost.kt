package com.example.musical.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musical.data.model.Song
import com.example.musical.ui.home.HomeScreen
import com.example.musical.ui.search.SearchScreen
import com.example.musical.ui.library.LibraryScreen
import com.example.musical.ui.player.PlayerScreen
import com.example.musical.ui.profile.ProfileScreen

@Composable
fun MusicalNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
        composable(Screen.Library.route) {
            LibraryScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            // Passing a placeholder song; in a real app, you'd fetch this from a ViewModel using songId
            val placeholderSong = Song(
                id = songId ?: "",
                title = "Unknown Song",
                artist = "Unknown Artist",
                album = "Unknown Album",
                artworkUrl = "",
                durationMs = 210000 // 3:30
            )
            PlayerScreen(song = placeholderSong)
        }
    }
}
