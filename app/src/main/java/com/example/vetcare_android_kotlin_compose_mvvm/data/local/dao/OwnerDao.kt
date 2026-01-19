package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.OwnerEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de Owners
 * Proporciona acceso a la tabla owners en SQLite
 */
@Dao
interface OwnerDao {

    @Query("SELECT * FROM owners")
    fun getAllOwners(): Flow<List<OwnerEntity>>

    @Query("SELECT * FROM owners")
    suspend fun getAllOwnersList(): List<OwnerEntity>

    @Query("SELECT * FROM owners WHERE id = :id")
    suspend fun getOwnerById(id: String): OwnerEntity?

    @Query("SELECT * FROM owners WHERE email = :email LIMIT 1")
    suspend fun getOwnerByEmail(email: String): OwnerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner: OwnerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOwners(owners: List<OwnerEntity>)

    @Update
    suspend fun updateOwner(owner: OwnerEntity)

    @Delete
    suspend fun deleteOwner(owner: OwnerEntity)

    @Query("DELETE FROM owners WHERE id = :id")
    suspend fun deleteOwnerById(id: String)

    @Query("DELETE FROM owners")
    suspend fun deleteAllOwners()

    @Query("SELECT COUNT(*) FROM owners")
    suspend fun getOwnersCount(): Int
}
