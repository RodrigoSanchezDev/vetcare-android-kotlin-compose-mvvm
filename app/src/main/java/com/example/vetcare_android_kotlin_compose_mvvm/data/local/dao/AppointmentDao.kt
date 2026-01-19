package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.AppointmentEntity
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para operaciones CRUD de Appointments
 * Proporciona acceso a la tabla appointments en SQLite
 */
@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments ORDER BY dateTime ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments ORDER BY dateTime ASC")
    suspend fun getAllAppointmentsList(): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: String): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE petId = :petId ORDER BY dateTime ASC")
    fun getAppointmentsByPet(petId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE petId = :petId ORDER BY dateTime ASC")
    suspend fun getAppointmentsByPetList(petId: String): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE vetId = :vetId ORDER BY dateTime ASC")
    fun getAppointmentsByVet(vetId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE vetId = :vetId ORDER BY dateTime ASC")
    suspend fun getAppointmentsByVetList(vetId: String): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE dateTime > :now AND status IN ('SCHEDULED', 'CONFIRMED') ORDER BY dateTime ASC")
    fun getUpcomingAppointments(now: LocalDateTime): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE dateTime > :now AND status IN ('SCHEDULED', 'CONFIRMED') ORDER BY dateTime ASC")
    suspend fun getUpcomingAppointmentsList(now: LocalDateTime): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: String, status: AppointmentStatus): Int

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointmentById(id: String): Int

    @Query("DELETE FROM appointments")
    suspend fun deleteAllAppointments()

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun getAppointmentsCount(): Int

    @Query("SELECT COUNT(*) FROM appointments WHERE dateTime > :now AND status IN ('SCHEDULED', 'CONFIRMED')")
    suspend fun getUpcomingAppointmentsCount(now: LocalDateTime): Int
}
