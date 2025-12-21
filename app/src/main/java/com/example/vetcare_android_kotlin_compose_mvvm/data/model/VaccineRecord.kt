package com.example.vetcare_android_kotlin_compose_mvvm.data.model

import java.time.LocalDate

/**
 * Registro de vacuna aplicada
 * @property id Identificador único
 * @property petId ID de la mascota
 * @property vaccineName Nombre de la vacuna
 * @property lastDate Fecha de última aplicación
 * @property nextDueDate Fecha de próxima aplicación
 * @property notes Notas adicionales (opcional)
 */
data class VaccineRecord(
    val id: String,
    val petId: String,
    val vaccineName: String,
    val lastDate: LocalDate,
    val nextDueDate: LocalDate,
    val notes: String? = null
)

