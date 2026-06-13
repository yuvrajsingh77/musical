package com.example.musical.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musical.data.model.Song
import com.example.musical.ui.auth.AuthViewModel
import com.example.musical.ui.auth.LoginScreen
import com.example.musical.ui.home.HomeScreen
import com.example.musical.ui.home.HomeViewModel
import com.example.musical.ui.search.SearchScreen
import com.example.musical.ui.search.SearchViewModel
import com.example.musical.ui.library.LibraryScreen
import com.example.musical.ui.library.LibraryViewModel
import com.example.musical.ui.library.LikedSongsScreen
import com.example.musical.ui.playlist.PlaylistScreen
import com.example.musical.ui.player.PlayerScreen
import com.example.musical.ui.player.PlayerViewModel
import com.example.musical.ui.profile.ProfileScreen

@Composable
fun MusicalNavHost(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route
    val libraryViewModel: LibraryViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(Screen.Login.route) {
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            LoginScreen(authViewModel = authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                playerViewModel = playerViewModel
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController,
                searchViewModel = searchViewModel,
                playerViewModel = playerViewModel
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                viewModel = libraryViewModel,
                navController = navController
            )
        }
        composable(Screen.LikedSongs.route) {
            LikedSongsScreen(
                viewModel = libraryViewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.Playlist.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistScreen(
                playlistId = playlistId,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("songId") { type = NavType.StringType }),
            enterTransition = {
                slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            }
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            val queue by playerViewModel.queue.collectAsState()
            val currentSong by playerViewModel.song.collectAsState()

            val song = currentSong?.takeIf { it.id == songId }
                ?: queue.find { it.id == songId }
                ?: Song(
                    id = songId ?: "",
                    title = "Loading...",
                    artist = "", album = "",
                    artworkUrl = "", durationMs = 0
                )

            PlayerScreen(
                song = song,
                viewModel = playerViewModel,
                navController = navController
            )
        }
    }
}
