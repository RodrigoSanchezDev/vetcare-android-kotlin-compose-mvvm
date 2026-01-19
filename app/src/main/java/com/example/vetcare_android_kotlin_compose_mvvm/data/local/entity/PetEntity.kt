package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies

/**
 * Entidad Room para Pet (tabla pets)
 * Representa una mascota con relación a su dueño
 */
@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = OwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PetEntity(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val name: String,
    val species: PetSpecies,
    val breed: String? = null,
    val ageYears: Int,
    val weightKg: Double? = null,
    val photoRes: Int? = null,
    val notes: String? = null
)
