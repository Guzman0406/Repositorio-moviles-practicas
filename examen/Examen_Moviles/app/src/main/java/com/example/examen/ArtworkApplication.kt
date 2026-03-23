package com.example.examen

import android.app.Application
import com.example.examen.repositories.ArtworkRepository

class ArtworkApplication : Application() {
    val artworkRepository: ArtworkRepository by lazy { ArtworkRepository(this) }
}
