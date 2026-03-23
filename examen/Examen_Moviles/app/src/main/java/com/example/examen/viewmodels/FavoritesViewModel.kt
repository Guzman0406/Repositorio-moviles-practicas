package com.example.examen.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.examen.ArtworkApplication
import com.example.examen.models.ArtworkEntity
import com.example.examen.repositories.ArtworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArtworkRepository = (application as ArtworkApplication).artworkRepository

    val favoriteArtworks: Flow<List<ArtworkEntity>> = repository.favoriteArtworks

    fun removeFavorite(artworkId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(artworkId)
        }
    }

    class FavoritesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
