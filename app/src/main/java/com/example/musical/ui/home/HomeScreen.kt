package com.example.musical.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musical.data.model.Song
import com.example.musical.ui.components.SongCard
import com.example.musical.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        bottomBar = {
            MusicalBottomNavigation(navController)
        }
    ) { padding ->
        content(padding)
    }
}

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Good Evening",
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

@Composable
fun MusicalBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val items = listOf(
            NavigationItem("Home", Icons.Default.Home, Screen.Home.route),
            NavigationItem("Search", Icons.Default.Search, Screen.Search.route),
            NavigationItem("Library", Icons.Default.LibraryMusic, Screen.Library.route),
            NavigationItem("Profile", Icons.Default.Person, Screen.Profile.route)
        )
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector, val route: String)
