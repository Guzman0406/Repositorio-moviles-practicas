package com.example.examen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String, val title: String, val icon: ImageVector?) {
    object LoadingScreen : AppScreen("loading", "Loading", null)
    object SearchScreen : AppScreen("search", "Search", Icons.Default.Search)
    object DetailScreen : AppScreen("detail", "Detail", null)
    object FavoritesScreen : AppScreen("favorites", "Favorites", Icons.Default.Favorite)
    object MainScreen : AppScreen("main", "Main", null)
}
