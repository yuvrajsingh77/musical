package com.example.musical.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.musical.ui.components.MusicalBottomNavigation

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
