package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.VaccineRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * DAO para operaciones CRUD de VaccineRecords
 * Proporciona acceso a la tabla vaccine_records en SQLite
 */
@Dao
interface VaccineRecordDao {

    @Query("SELECT * FROM vaccine_records ORDER BY nextDueDate ASC")
    fun getAllVaccineRecords(): Flow<List<VaccineRecordEntity>>

    @Query("SELECT * FROM vaccine_records ORDER BY nextDueDate ASC")
    suspend fun getAllVaccineRecordsList(): List<VaccineRecordEntity>

    @Query("SELECT * FROM vaccine_records WHERE id = :id")
    suspend fun getVaccineRecordById(id: String): VaccineRecordEntity?

    @Query("SELECT * FROM vaccine_records WHERE petId = :petId ORDER BY nextDueDate ASC")
    fun getVaccineRecordsByPet(petId: String): Flow<List<VaccineRecordEntity>>

    @Query("SELECT * FROM vaccine_records WHERE petId = :petId ORDER BY nextDueDate ASC")
    suspend fun getVaccineRecordsByPetList(petId: String): List<VaccineRecordEntity>

    @Query("SELECT * FROM vaccine_records WHERE nextDueDate <= :limitDate ORDER BY nextDueDate ASC")
    fun getUpcomingVaccines(limitDate: LocalDate): Flow<List<VaccineRecordEntity>>

    @Query("SELECT * FROM vaccine_records WHERE nextDueDate <= :limitDate ORDER BY nextDueDate ASC")
    suspend fun getUpcomingVaccinesList(limitDate: LocalDate): List<VaccineRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccineRecord(vaccineRecord: VaccineRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVaccineRecords(vaccineRecords: List<VaccineRecordEntity>)

    @Update
    suspend fun updateVaccineRecord(vaccineRecord: VaccineRecordEntity)

    @Delete
    suspend fun deleteVaccineRecord(vaccineRecord: VaccineRecordEntity)

    @Query("DELETE FROM vaccine_records WHERE id = :id")
    suspend fun deleteVaccineRecordById(id: String): Int

    @Query("DELETE FROM vaccine_records")
    suspend fun deleteAllVaccineRecords()

    @Query("SELECT COUNT(*) FROM vaccine_records")
    suspend fun getVaccineRecordsCount(): Int

    @Query("SELECT COUNT(*) FROM vaccine_records WHERE nextDueDate <= :limitDate")
    suspend fun getUpcomingVaccinesCount(limitDate: LocalDate): Int
}
