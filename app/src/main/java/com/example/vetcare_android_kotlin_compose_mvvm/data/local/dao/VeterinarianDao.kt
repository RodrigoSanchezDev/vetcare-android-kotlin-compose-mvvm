package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.VeterinarianEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de Veterinarians
 * Proporciona acceso a la tabla veterinarians en SQLite
 */
@Dao
interface VeterinarianDao {

    @Query("SELECT * FROM veterinarians")
    fun getAllVeterinarians(): Flow<List<VeterinarianEntity>>

    @Query("SELECT * FROM veterinarians")
    suspend fun getAllVeterinariansList(): List<VeterinarianEntity>

    @Query("SELECT * FROM veterinarians WHERE id = :id")
    suspend fun getVeterinarianById(id: String): VeterinarianEntity?

    @Query("SELECT * FROM veterinarians WHERE specialty = :specialty")
    fun getVeterinariansBySpecialty(specialty: String): Flow<List<VeterinarianEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVeterinarian(veterinarian: VeterinarianEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVeterinarians(veterinarians: List<VeterinarianEntity>)

    @Update
    suspend fun updateVeterinarian(veterinarian: VeterinarianEntity)

    @Delete
    suspend fun deleteVeterinarian(veterinarian: VeterinarianEntity)

    @Query("DELETE FROM veterinarians WHERE id = :id")
    suspend fun deleteVeterinarianById(id: String): Int

    @Query("DELETE FROM veterinarians")
    suspend fun deleteAllVeterinarians()

    @Query("SELECT COUNT(*) FROM veterinarians")
    suspend fun getVeterinariansCount(): Int
}
