package com.example.examen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_artworks")
data class ArtworkEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageId: String?,
    val artistDisplay: String?,
    val thumbnailAltText: String?
)
