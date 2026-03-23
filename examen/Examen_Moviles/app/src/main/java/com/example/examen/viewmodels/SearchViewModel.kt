package com.example.examen.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen.ArtworkApplication
import com.example.examen.models.Artwork
import com.example.examen.repositories.ArtworkRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArtworkRepository = (application as ArtworkApplication).artworkRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _searchQuery
            .debounce(500)
            .onEach { query ->
                if (query.isNotBlank()) {
                    searchArtworks(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun searchArtworks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.searchArtworks(query)
                val infiniteList = mutableListOf<Artwork>()
                repeat(20) {
                    infiniteList.addAll(results)
                }
                _artworks.value = infiniteList
            } catch (e: Exception) {
                _artworks.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}
