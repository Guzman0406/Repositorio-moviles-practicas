package com.example.examen.models

import com.google.gson.annotations.SerializedName

data class ArtworksResponse(
    val data: List<Artwork>,
    val config: ApiConfig
)

data class Artwork(
    val id: Int,
    val title: String,
    @SerializedName("image_id") val imageId: String?,
    @SerializedName("artist_display") val artistDisplay: String?,
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    @SerializedName("alt_text") val altText: String?
)

data class ApiConfig(
    @SerializedName("iiif_url") val iiifUrl: String
)
