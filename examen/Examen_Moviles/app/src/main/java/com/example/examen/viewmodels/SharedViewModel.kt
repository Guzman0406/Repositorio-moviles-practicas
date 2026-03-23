package com.example.examen.viewmodels

import androidx.lifecycle.ViewModel
import com.example.examen.models.Artwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedArtwork = MutableStateFlow<Artwork?>(null)
    val selectedArtwork: StateFlow<Artwork?> = _selectedArtwork

    fun selectArtwork(artwork: Artwork) {
        _selectedArtwork.value = artwork
    }
}
