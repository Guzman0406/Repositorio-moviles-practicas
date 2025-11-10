package com.example.dashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "form_data")
data class FormData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val fechaNacimiento: String,
    val genero: String,
    val notas: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)