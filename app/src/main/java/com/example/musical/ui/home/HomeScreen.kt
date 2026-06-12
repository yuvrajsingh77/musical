package com.example.musical.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musical.data.model.Song
import com.example.musical.ui.components.SongCard
import java.util.Calendar

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            HomeHeader()
        }
        
        item {
            SectionTitle("Recently Played")
            RecentlyPlayedSection()
        }
        
        item {
            SectionTitle("Daily Mix")
            RecentlyPlayedSection()
        }
        
        item {
            SectionTitle("Trending Now")
            RecentlyPlayedSection()
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
fun RecentlyPlayedSection() {
    val dummySongs = listOf(
        Song("1", "Song One", "Artist A", "Album X", "https://via.placeholder.com/150", 180000),
        Song("2", "Song Two", "Artist B", "Album Y", "https://via.placeholder.com/150", 200000),
        Song("3", "Song Three", "Artist C", "Album Z", "https://via.placeholder.com/150", 220000)
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(dummySongs) { song ->
            SongCard(song = song, onClick = { /* TODO */ })
        }
    }
}
