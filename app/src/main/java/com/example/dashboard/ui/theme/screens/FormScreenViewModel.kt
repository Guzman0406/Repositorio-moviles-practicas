package com.example.dashboard.ui.theme.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashboard.data.AppDatabase
import com.example.dashboard.data.FormData
import com.example.dashboard.data.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FormRepository

    init {
        val formDao = AppDatabase.getInstance(application).formDao()
        repository = FormRepository(formDao)
    }

    private val _formData = MutableStateFlow(
        FormData(
            nombre = "",
            email = "",
            telefono = "",
            direccion = "",
            fechaNacimiento = "",
            genero = "",
            notas = ""
        )
    )
    val formData: StateFlow<FormData> = _formData.asStateFlow()

    private val _savedForms = MutableStateFlow<List<FormData>>(emptyList())
    val savedForms: StateFlow<List<FormData>> = _savedForms.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    init {
        loadAllForms()
    }

    fun updateFormField(field: String, value: String) {
        val current = _formData.value
        _formData.value = when (field) {
            "nombre" -> current.copy(nombre = value)
            "email" -> current.copy(email = value)
            "telefono" -> current.copy(telefono = value)
            "direccion" -> current.copy(direccion = value)
            "fechaNacimiento" -> current.copy(fechaNacimiento = value)
            "genero" -> current.copy(genero = value)
            "notas" -> current.copy(notas = value)
            else -> current
        }
    }

    fun saveForm() {
        viewModelScope.launch {
            try {
                if (_isEditing.value) {
                    repository.updateFormData(_formData.value)
                } else {
                    repository.insertFormData(_formData.value)
                }
                _saveSuccess.value = true
                resetForm()
                loadAllForms()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadFormForEdit(formData: FormData) {
        _formData.value = formData
        _isEditing.value = true
    }

    fun deleteForm(formData: FormData) {
        viewModelScope.launch {
            repository.deleteFormData(formData)
            loadAllForms()
        }
    }

    private fun loadAllForms() {
        viewModelScope.launch {
            repository.allFormData.collect { forms ->
                _savedForms.value = forms
            }
        }
    }

    fun resetForm() {
        _formData.value = FormData(
            nombre = "",
            email = "",
            telefono = "",
            direccion = "",
            fechaNacimiento = "",
            genero = "",
            notas = ""
        )
        _isEditing.value = false
        _saveSuccess.value = false
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}