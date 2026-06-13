package com.example.musical.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musical.data.model.Song
import com.example.musical.ui.components.ErrorView
import com.example.musical.ui.components.ShimmerSongCard
import com.example.musical.ui.components.SongCard
import com.example.musical.ui.navigation.Screen
import com.example.musical.ui.player.PlayerViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    playerViewModel: PlayerViewModel
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val dailyMix by homeViewModel.dailyMix.collectAsState()
    val trendingNow by homeViewModel.trendingNow.collectAsState()
    val recentlyPlayed by homeViewModel.recentlyPlayed.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { HomeHeader() }
                
                item {
                    SectionTitle("Recently Played")
                    ShimmerRow()
                }
                
                item {
                    SectionTitle("Daily Mix")
                    ShimmerRow()
                }
                
                item {
                    SectionTitle("Trending Now")
                    ShimmerRow()
                }
            }
        }
        is HomeUiState.Error -> {
            ErrorView(
                message = state.message,
                onRetry = { homeViewModel.retry() }
            )
        }
        is HomeUiState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { HomeHeader() }
                
                if (recentlyPlayed.isNotEmpty()) {
                    item {
                        SectionTitle("Recently Played")
                        SongSectionRow(
                            songs = recentlyPlayed,
                            navController = navController,
                            playerViewModel = playerViewModel
                        )
                    }
                }
                
                item {
                    SectionTitle("Daily Mix")
                    SongSectionRow(
                        songs = dailyMix,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }
                
                item {
                    SectionTitle("Trending Now")
                    SongSectionRow(
                        songs = trendingNow,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader() {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ShimmerRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(5) {
            ShimmerSongCard()
        }
    }
}

@Composable
fun SongSectionRow(
    songs: List<Song>,
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(songs) { index, song ->
            SongCard(
                song = song,
                onClick = {
                    playerViewModel.setQueue(songs, index)
                    navController.navigate(Screen.Player.createRoute(song.id))
                }
            )
        }
    }
}
