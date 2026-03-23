package com.example.examen.views

import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examen.ArtworkApplication
import com.example.examen.components.ArtworkListItem
import com.example.examen.models.Artwork
import com.example.examen.models.Thumbnail
import com.example.examen.navigation.AppScreen
import com.example.examen.viewmodels.FavoritesViewModel
import com.example.examen.viewmodels.SharedViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.FavoritesViewModelFactory(application))

    val favoriteArtworks by viewModel.favoriteArtworks.collectAsState(initial = emptyList())

    if (favoriteArtworks.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No tienes obras de arte favoritas.")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favoriteArtworks, key = { it.id }) { artworkEntity ->
                Box(modifier = Modifier.animateItemPlacement(tween(durationMillis = 300))) {
                    val artwork = Artwork(
                        id = artworkEntity.id,
                        title = artworkEntity.title,
                        imageId = artworkEntity.imageId,
                        artistDisplay = artworkEntity.artistDisplay,
                        thumbnail = Thumbnail(altText = artworkEntity.thumbnailAltText)
                    )
                    ArtworkListItem(artwork = artwork) {
                        sharedViewModel.selectArtwork(artwork)
                        navController.navigate(AppScreen.DetailScreen.route)
                    }
                    IconButton(
                        onClick = { viewModel.removeFavorite(artworkEntity.id) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar de favoritos")
                    }
                }
            }
        }
    }
}
