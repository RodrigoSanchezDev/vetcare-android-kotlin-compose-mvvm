package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.ConsultationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de Consultations
 * Proporciona acceso a la tabla consultations en SQLite
 */
@Dao
interface ConsultationDao {

    @Query("SELECT * FROM consultations ORDER BY dateTime DESC")
    fun getAllConsultations(): Flow<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations ORDER BY dateTime DESC")
    suspend fun getAllConsultationsList(): List<ConsultationEntity>

    @Query("SELECT * FROM consultations WHERE id = :id")
    suspend fun getConsultationById(id: String): ConsultationEntity?

    @Query("SELECT * FROM consultations WHERE petId = :petId ORDER BY dateTime DESC")
    fun getConsultationsByPet(petId: String): Flow<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations WHERE petId = :petId ORDER BY dateTime DESC")
    suspend fun getConsultationsByPetList(petId: String): List<ConsultationEntity>

    @Query("SELECT * FROM consultations WHERE vetId = :vetId ORDER BY dateTime DESC")
    fun getConsultationsByVet(vetId: String): Flow<List<ConsultationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: ConsultationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllConsultations(consultations: List<ConsultationEntity>)

    @Update
    suspend fun updateConsultation(consultation: ConsultationEntity)

    @Delete
    suspend fun deleteConsultation(consultation: ConsultationEntity)

    @Query("DELETE FROM consultations WHERE id = :id")
    suspend fun deleteConsultationById(id: String): Int

    @Query("DELETE FROM consultations")
    suspend fun deleteAllConsultations()

    @Query("SELECT COUNT(*) FROM consultations")
    suspend fun getConsultationsCount(): Int
}
