package com.example.musical

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.musical.ui.home.MainScreen
import com.example.musical.ui.navigation.MusicalNavHost
import com.example.musical.ui.theme.MusicalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicalTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(navController = navController) { padding ->
                        Surface(modifier = Modifier.padding(padding)) {
                            MusicalNavHost(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
