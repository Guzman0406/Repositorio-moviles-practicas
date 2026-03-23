package com.example.examen.repositories

import com.example.examen.models.Artwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FavoritesRepository {

    private val _favoriteArtworks = MutableStateFlow<List<Artwork>>(emptyList())
    val favoriteArtworks = _favoriteArtworks.asStateFlow()

    fun addFavorite(artwork: Artwork) {
        if (!_favoriteArtworks.value.any { it.id == artwork.id }) {
            _favoriteArtworks.value = _favoriteArtworks.value + artwork
        }
    }

    fun removeFavorite(artworkId: Int) {
        _favoriteArtworks.value = _favoriteArtworks.value.filterNot { it.id == artworkId }
    }

    fun isFavorite(artworkId: Int): Boolean {
        return _favoriteArtworks.value.any { it.id == artworkId }
    }
}
