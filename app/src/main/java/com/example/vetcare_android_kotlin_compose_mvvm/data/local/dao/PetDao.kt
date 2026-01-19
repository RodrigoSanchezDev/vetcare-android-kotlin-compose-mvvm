package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de Pets
 * Proporciona acceso a la tabla pets en SQLite
 */
@Dao
interface PetDao {

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets")
    suspend fun getAllPetsList(): List<PetEntity>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: String): PetEntity?

    @Query("SELECT * FROM pets WHERE ownerId = :ownerId")
    fun getPetsByOwner(ownerId: String): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE ownerId = :ownerId")
    suspend fun getPetsByOwnerList(ownerId: String): List<PetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPets(pets: List<PetEntity>)

    @Update
    suspend fun updatePet(pet: PetEntity)

    @Delete
    suspend fun deletePet(pet: PetEntity)

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deletePetById(id: String): Int

    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()

    @Query("SELECT COUNT(*) FROM pets")
    suspend fun getPetsCount(): Int

    @Query("SELECT COUNT(*) FROM pets WHERE ownerId = :ownerId")
    suspend fun getPetsCountByOwner(ownerId: String): Int
}
