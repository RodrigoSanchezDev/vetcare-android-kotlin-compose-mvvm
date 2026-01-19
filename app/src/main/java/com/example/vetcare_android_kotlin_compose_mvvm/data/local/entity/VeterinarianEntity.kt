package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para Veterinarian (tabla veterinarians)
 * Representa un veterinario del staff
 */
@Entity(tableName = "veterinarians")
data class VeterinarianEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val specialty: String? = null,
    val phone: String? = null,
    val avatarRes: Int? = null
)
