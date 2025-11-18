package com.example.student.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student.data.Estudiante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EstudiantesViewModel : ViewModel() {

    private val _estudiantes = MutableStateFlow<List<Estudiante>>(emptyList())
    val estudiantes: StateFlow<List<Estudiante>> = _estudiantes.asStateFlow()

    private var currentId = 1

    fun agregarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            val nuevaLista = _estudiantes.value.toMutableList()
            val estudianteConId = if (estudiante.id == 0) {
                estudiante.copy(id = currentId++)
            } else {
                estudiante
            }
            nuevaLista.add(estudianteConId)
            _estudiantes.value = nuevaLista
        }
    }

    fun actualizarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            val nuevaLista = _estudiantes.value.toMutableList()
            val index = nuevaLista.indexOfFirst { it.id == estudiante.id }
            if (index != -1) {
                nuevaLista[index] = estudiante
                _estudiantes.value = nuevaLista
            }
        }
    }

    fun eliminarEstudiante(id: Int) {
        viewModelScope.launch {
            val nuevaLista = _estudiantes.value.toMutableList()
            nuevaLista.removeAll { it.id == id }
            _estudiantes.value = nuevaLista
        }
    }

    fun obtenerEstudiantePorId(id: Int): Estudiante? {
        return _estudiantes.value.find { it.id == id }
    }

    // Cálculos
    fun calcularPromedioGeneral(): Double {
        val estudiantes = _estudiantes.value
        if (estudiantes.isEmpty()) return 0.0
        return estudiantes.map { it.puntuaje }.average()
    }

    fun obtenerEstudianteConMayorPuntuaje(): Estudiante? {
        return _estudiantes.value.maxByOrNull { it.puntuaje }
    }

    fun obtenerTop3PorGrupo(grupo: String): List<Estudiante> {
        return _estudiantes.value
            .filter { it.grupo == grupo }
            .sortedByDescending { it.puntuaje }
            .take(3)
    }

    fun obtenerGrupos(): List<String> {
        return _estudiantes.value.map { it.grupo }.distinct()
    }

    fun calcularPromedioPorGrupo(grupo: String): Double {
        val estudiantesGrupo = _estudiantes.value.filter { it.grupo == grupo }
        if (estudiantesGrupo.isEmpty()) return 0.0
        return estudiantesGrupo.map { it.puntuaje }.average()
    }
}