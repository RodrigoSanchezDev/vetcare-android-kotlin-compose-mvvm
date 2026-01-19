package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import java.time.LocalDateTime

/**
 * Entidad Room para Appointment (tabla appointments)
 * Representa una cita agendada con relaciones a mascota y veterinario
 */
@Entity(
    tableName = "appointments",
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
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val vetId: String,
    val dateTime: LocalDateTime,
    val reason: String,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String? = null
)
