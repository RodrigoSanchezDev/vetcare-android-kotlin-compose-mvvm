package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole

/**
 * Entidad Room para User (tabla users)
 * Representa un usuario del sistema para autenticación
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val ownerId: String? = null
)
