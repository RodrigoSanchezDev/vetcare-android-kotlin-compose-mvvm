package com.example.vetcare_android_kotlin_compose_mvvm.data.model

/**
 * Roles de usuario en el sistema VetCare
 */
enum class UserRole {
    ADMIN,  // Personal de veterinaria con acceso completo
    OWNER   // Dueño de mascota con acceso limitado a sus datos
}

/**
 * Usuario del sistema (para autenticación)
 * @property id Identificador único
 * @property name Nombre para mostrar
 * @property email Email para login
 * @property passwordHash Password (simulado - en producción sería un hash)
 * @property role Rol del usuario
 * @property ownerId ID del Owner asociado (solo para role OWNER)
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val ownerId: String? = null
)

