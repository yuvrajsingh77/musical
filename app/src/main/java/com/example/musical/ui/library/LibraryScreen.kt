package com.example.musical.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    color = Color(0xFF1DB954)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
        
        val libraryItems = listOf(
            LibraryItem("Liked Songs", "Playlist • 120 songs", Icons.Default.Favorite),
            LibraryItem("Recently Played", "History", Icons.Default.History)
        )
        
        LazyColumn {
            items(libraryItems) { item ->
                LibraryListItem(item)
            }
        }
    }
}

@Composable
fun LibraryListItem(item: LibraryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(item.icon, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class LibraryItem(val title: String, val subtitle: String, val icon: ImageVector)
