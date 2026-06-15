package com.example.musical.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.musical.data.model.Song
import com.example.musical.ui.auth.AuthViewModel
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
    playerViewModel: PlayerViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()
    val dailyMix by homeViewModel.dailyMix.collectAsState()
    val trendingNow by homeViewModel.trendingNow.collectAsState()
    val recentlyPlayed by homeViewModel.recentlyPlayed.collectAsState()
    val newReleases by homeViewModel.newReleases.collectAsState()
    val topCharts by homeViewModel.topCharts.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { HomeHeader(userName = currentUser?.displayName) }
                
                item {
                    SectionTitle("Recently Played")
                    ShimmerRow()
                }
                
                item {
                    SectionTitle("Arijit Singh Mix")
                    ShimmerRow()
                }
                
                item {
                    SectionTitle("Bollywood Trending")
                    ShimmerRow()
                }

                item {
                    SectionTitle("New Hindi Releases")
                    ShimmerRow()
                }

                item {
                    SectionTitle("Punjabi Hits")
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
                item { HomeHeader(userName = currentUser?.displayName) }
                
                item {
                    FeaturedBanner(songs = dailyMix, navController = navController, playerViewModel = playerViewModel)
                }

                item {
                    SectionTitle("Recently Played")
                    if (recentlyPlayed.isEmpty()) {
                        Text(
                            text = "Songs you play will appear here",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    } else {
                        SongSectionRow(
                            songs = recentlyPlayed,
                            navController = navController,
                            playerViewModel = playerViewModel
                        )
                    }
                }
                
                item {
                    SectionTitle("Arijit Singh Mix")
                    SongSectionRow(
                        songs = dailyMix,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }
                
                item {
                    SectionTitle("Bollywood Trending")
                    SongSectionRow(
                        songs = trendingNow,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }

                item {
                    SectionTitle("New Hindi Releases")
                    SongSectionRow(
                        songs = newReleases,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }

                item {
                    SectionTitle("Punjabi Hits")
                    SongSectionRow(
                        songs = topCharts,
                        navController = navController,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(userName: String?) {
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
            text = if (userName != null) "$greeting, ${userName.split(" ").first()}!" else "$greeting!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FeaturedBanner(songs: List<Song>, navController: NavController, playerViewModel: PlayerViewModel) {
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1C1C1E))
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1DB954),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }
    val featured = songs.first()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                playerViewModel.setQueue(songs, 0)
                navController.navigate(Screen.Player.createRoute(featured.id))
            }
    ) {
        AsyncImage(
            model = featured.artworkUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "FEATURED",
                color = Color(0xFF1DB954),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = featured.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = featured.artist,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
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
