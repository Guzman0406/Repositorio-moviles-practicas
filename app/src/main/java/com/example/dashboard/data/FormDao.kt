package com.example.dashboard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FormDao {

    @Query("SELECT * FROM form_data ORDER BY fechaCreacion DESC")
    fun getAllFormData(): Flow<List<FormData>>

    @Query("SELECT * FROM form_data WHERE id = :id")
    suspend fun getFormDataById(id: Long): FormData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormData(formData: FormData): Long

    @Update
    suspend fun updateFormData(formData: FormData)

    @Delete
    suspend fun deleteFormData(formData: FormData)

    @Query("DELETE FROM form_data WHERE id = :id")
    suspend fun deleteFormDataById(id: Long)
}