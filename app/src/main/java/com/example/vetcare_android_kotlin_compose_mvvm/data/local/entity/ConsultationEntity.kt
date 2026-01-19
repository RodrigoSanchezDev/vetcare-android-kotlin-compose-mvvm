package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidad Room para Consultation (tabla consultations)
 * Representa una consulta médica realizada
 */
@Entity(
    tableName = "consultations",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VeterinarianEntity::class,
            parentColumns = ["id"],
            childColumns = ["vetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["petId"]),
        Index(value = ["vetId"])
    ]
)
data class ConsultationEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val vetId: String,
    val dateTime: LocalDateTime,
    val diagnosis: String,
    val treatment: String,
    val notes: String? = null
)
