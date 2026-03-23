package com.example.examen.repositories

import android.content.Context
import com.example.examen.data.AppDatabase
import com.example.examen.data.ArtworkDao
import com.example.examen.data.RetrofitClient
import com.example.examen.models.Artwork
import com.example.examen.models.ArtworkEntity
import kotlinx.coroutines.flow.Flow

class ArtworkRepository(context: Context) {

    private val artworkDao: ArtworkDao = AppDatabase.getDatabase(context).artworkDao()
    private val articApiService = RetrofitClient.instance

    val favoriteArtworks: Flow<List<ArtworkEntity>> = artworkDao.getAllFavoriteArtworks()

    suspend fun searchArtworks(query: String): List<Artwork> {
        val response = articApiService.searchArtworks(query)
        val iiifUrl = response.config.iiifUrl
        return response.data.map { artwork ->
            artwork.copy(imageId = "${iiifUrl}/${artwork.imageId}/full/843,/0/default.jpg")
        }
    }

    fun isFavorite(artworkId: Int): Flow<Boolean> {
        return artworkDao.isFavorite(artworkId)
    }

    suspend fun addFavorite(artwork: Artwork) {
        val artworkEntity = ArtworkEntity(
            id = artwork.id,
            title = artwork.title,
            imageId = artwork.imageId,
            artistDisplay = artwork.artistDisplay,
            thumbnailAltText = artwork.thumbnail?.altText
        )
        artworkDao.insertArtwork(artworkEntity)
    }

    suspend fun removeFavorite(artworkId: Int) {
        artworkDao.deleteArtworkById(artworkId)
    }
}
