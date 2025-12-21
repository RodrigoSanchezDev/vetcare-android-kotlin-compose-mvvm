package com.example.vetcare_android_kotlin_compose_mvvm.data.model

/**
 * Veterinario del staff
 * @property id Identificador único
 * @property name Nombre completo
 * @property specialty Especialidad (opcional)
 * @property phone Teléfono de contacto (opcional)
 * @property avatarRes Recurso drawable del avatar (opcional)
 */
data class Veterinarian(
    val id: String,
    val name: String,
    val specialty: String? = null,
    val phone: String? = null,
    val avatarRes: Int? = null
)

