package com.example.examen.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.examen.ArtworkApplication
import com.example.examen.models.Artwork
import com.example.examen.repositories.ArtworkRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, private val artwork: Artwork) : AndroidViewModel(application) {

    private val repository: ArtworkRepository = (application as ArtworkApplication).artworkRepository
    private val englishSpanishTranslator: Translator

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _translatedDescription = MutableStateFlow("Traduciendo...")
    val translatedDescription: StateFlow<String> = _translatedDescription.asStateFlow()

    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        englishSpanishTranslator = Translation.getClient(options)

        checkIfFavorite()
        translateDescription()
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
            repository.isFavorite(artwork.id).collectLatest {
                _isFavorite.value = it
            }
        }
    }

    private fun translateDescription() {
        val conditions = DownloadConditions.Builder().requireWifi().build()
        englishSpanishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                val originalText = artwork.thumbnail?.altText ?: "Sin descripción."
                if (originalText != "Sin descripción.") {
                    englishSpanishTranslator.translate(originalText)
                        .addOnSuccessListener { translatedText ->
                            _translatedDescription.value = translatedText
                        }
                        .addOnFailureListener { 
                            _translatedDescription.value = originalText
                        }
                } else {
                    _translatedDescription.value = originalText
                }
            }
            .addOnFailureListener { 
                _translatedDescription.value = artwork.thumbnail?.altText ?: "Sin descripción."
            }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(artwork.id)
            } else {
                repository.addFavorite(artwork)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        englishSpanishTranslator.close()
    }
}

class DetailViewModelFactory(private val application: Application, private val artwork: Artwork) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(application, artwork) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
