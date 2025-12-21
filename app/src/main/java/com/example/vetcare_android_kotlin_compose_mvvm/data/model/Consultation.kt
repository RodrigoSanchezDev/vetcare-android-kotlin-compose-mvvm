package com.example.vetcare_android_kotlin_compose_mvvm.data.model

import java.time.LocalDateTime

/**
 * Consulta médica realizada
 * @property id Identificador único
 * @property petId ID de la mascota
 * @property vetId ID del veterinario
 * @property dateTime Fecha y hora de la consulta
 * @property diagnosis Diagnóstico
 * @property treatment Tratamiento indicado
 * @property notes Notas adicionales (opcional)
 */
data class Consultation(
    val id: String,
    val petId: String,
    val vetId: String,
    val dateTime: LocalDateTime,
    val diagnosis: String,
    val treatment: String,
    val notes: String? = null
)

