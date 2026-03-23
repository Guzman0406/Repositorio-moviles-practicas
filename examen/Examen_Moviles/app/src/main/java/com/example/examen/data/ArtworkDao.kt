package com.example.examen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.examen.models.ArtworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtworkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: ArtworkEntity)

    @Query("DELETE FROM favorite_artworks WHERE id = :artworkId")
    suspend fun deleteArtworkById(artworkId: Int)

    @Query("SELECT * FROM favorite_artworks")
    fun getAllFavoriteArtworks(): Flow<List<ArtworkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_artworks WHERE id = :artworkId)")
    fun isFavorite(artworkId: Int): Flow<Boolean>
}
