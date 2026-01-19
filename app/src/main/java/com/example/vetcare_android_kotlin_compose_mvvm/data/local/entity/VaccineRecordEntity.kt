package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad Room para VaccineRecord (tabla vaccine_records)
 * Representa un registro de vacuna aplicada a una mascota
 */
@Entity(
    tableName = "vaccine_records",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["petId"])]
)
data class VaccineRecordEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val vaccineName: String,
    val lastDate: LocalDate,
    val nextDueDate: LocalDate,
    val notes: String? = null
)
