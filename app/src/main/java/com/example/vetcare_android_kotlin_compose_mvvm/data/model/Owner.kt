package com.example.vetcare_android_kotlin_compose_mvvm.data.model

/**
 * Dueño de mascota registrado en la veterinaria
 * @property id Identificador único
 * @property fullName Nombre completo
 * @property email Email de contacto
 * @property phone Teléfono de contacto (opcional)
 * @property address Dirección (opcional)
 * @property avatarRes Recurso drawable del avatar (opcional)
 */
data class Owner(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val avatarRes: Int? = null
)

