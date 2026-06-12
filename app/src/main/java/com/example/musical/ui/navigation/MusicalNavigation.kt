package com.example.musical.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musical.ui.home.HomeScreen
import com.example.musical.ui.search.SearchScreen
import com.example.musical.ui.library.LibraryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Profile : Screen("profile")
}

@Composable
fun MusicalNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
        composable(Screen.Library.route) {
            LibraryScreen()
        }
        composable(Screen.Profile.route) {
            // ProfileScreen()
        }
    }
}
