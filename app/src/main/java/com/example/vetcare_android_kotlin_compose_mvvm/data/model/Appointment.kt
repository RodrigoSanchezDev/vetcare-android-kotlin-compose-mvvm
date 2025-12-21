package com.example.vetcare_android_kotlin_compose_mvvm.data.model

import java.time.LocalDateTime

/**
 * Estados posibles de una cita
 */
enum class AppointmentStatus(val displayName: String) {
    SCHEDULED("Programada"),
    CONFIRMED("Confirmada"),
    IN_PROGRESS("En progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    NO_SHOW("No asistió")
}

/**
 * Cita agendada en la veterinaria
 * @property id Identificador único
 * @property petId ID de la mascota
 * @property vetId ID del veterinario
 * @property dateTime Fecha y hora de la cita
 * @property reason Motivo de la cita
 * @property status Estado de la cita
 * @property notes Notas adicionales (opcional)
 */
data class Appointment(
    val id: String,
    val petId: String,
    val vetId: String,
    val dateTime: LocalDateTime,
    val reason: String,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String? = null
)

