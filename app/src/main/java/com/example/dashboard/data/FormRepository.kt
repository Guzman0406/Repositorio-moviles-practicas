package com.example.dashboard.data

import kotlinx.coroutines.flow.Flow

class FormRepository(private val formDao: FormDao) {

    val allFormData: Flow<List<FormData>> = formDao.getAllFormData()

    suspend fun getFormDataById(id: Long): FormData? {
        return formDao.getFormDataById(id)
    }

    suspend fun insertFormData(formData: FormData): Long {
        return formDao.insertFormData(formData)
    }

    suspend fun updateFormData(formData: FormData) {
        formDao.updateFormData(formData)
    }

    suspend fun deleteFormData(formData: FormData) {
        formDao.deleteFormData(formData)
    }
}