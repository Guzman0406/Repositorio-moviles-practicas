package com.example.examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.examen.navigation.AppScreen
import com.example.examen.ui.theme.ExamenTheme
import com.example.examen.viewmodels.SharedViewModel
import com.example.examen.views.DetailScreen
import com.example.examen.views.FavoritesScreen
import com.example.examen.views.LoadingScreen
import com.example.examen.views.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreen.LoadingScreen.route) {
        composable(AppScreen.LoadingScreen.route) {
            LoadingScreen {
                navController.navigate(AppScreen.MainScreen.route) {
                    popUpTo(AppScreen.LoadingScreen.route) { inclusive = true }
                }
            }
        }
        composable(AppScreen.MainScreen.route) {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()
    val bottomNavItems = listOf(AppScreen.SearchScreen, AppScreen.FavoritesScreen)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
    val showTopBar = showBottomBar || currentDestination?.route == AppScreen.DetailScreen.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        val title = when (currentDestination?.route) {
                            AppScreen.SearchScreen.route -> "Buscar Obras"
                            AppScreen.FavoritesScreen.route -> "Mis Favoritas"
                            AppScreen.DetailScreen.route -> "Detalles"
                            else -> ""
                        }
                        Text(title)
                    },
                    navigationIcon = {
                        if (currentDestination?.route == AppScreen.DetailScreen.route) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.SearchScreen.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(AppScreen.SearchScreen.route) { SearchScreen(navController, sharedViewModel) }
            composable(AppScreen.FavoritesScreen.route) { FavoritesScreen(navController, sharedViewModel) }
            composable(
                route = AppScreen.DetailScreen.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
            ) {
                val selectedArtwork by sharedViewModel.selectedArtwork.collectAsState()
                selectedArtwork?.let { DetailScreen(it) }
            }
        }
    }
}
