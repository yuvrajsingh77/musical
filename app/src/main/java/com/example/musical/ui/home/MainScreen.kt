package com.example.musical.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.musical.ui.components.MiniPlayer
import com.example.musical.ui.components.MusicalBottomNavigation
import com.example.musical.ui.components.NoInternetBanner
import com.example.musical.ui.navigation.Screen
import com.example.musical.ui.player.PlayerViewModel
import com.example.musical.ui.util.NetworkMonitor

@Composable
fun MainScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val currentSong by playerViewModel.song.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentPositionMs by playerViewModel.currentPositionMs.collectAsState()

    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

    Scaffold(
        bottomBar = {
            Column {
                MiniPlayer(
                    song = currentSong,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    onPlayPause = {
                        if (isPlaying) playerViewModel.pause() else playerViewModel.play()
                    },
                    onTap = {
                        currentSong?.let { song ->
                            navController.navigate(Screen.Player.createRoute(song.id))
                        }
                    }
                )
                MusicalBottomNavigation(navController)
            }
        }
    ) { padding ->
        Column {
            AnimatedVisibility(visible = !isOnline) {
                NoInternetBanner()
            }
            content(padding)
        }
    }
}
