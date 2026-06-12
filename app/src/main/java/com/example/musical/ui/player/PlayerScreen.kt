package com.example.musical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musical.data.model.Song

@Composable
fun PlayerScreen(song: Song, viewModel: PlayerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPositionMs by viewModel.currentPositionMs.collectAsState()
    
    LaunchedEffect(song) {
        viewModel.setSong(song)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E3532), Color.Black)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        AsyncImage(
            model = song.artworkUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Slider(
            value = if (song.durationMs > 0) currentPositionMs.toFloat() / song.durationMs else 0f,
            onValueChange = { viewModel.seekTo((it * song.durationMs).toInt()) },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPositionMs),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatTime(song.durationMs),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Shuffle, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White)
            }
            IconButton(
                onClick = { if (isPlaying) viewModel.pause() else viewModel.play() },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Repeat, contentDescription = null, tint = Color.White)
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
