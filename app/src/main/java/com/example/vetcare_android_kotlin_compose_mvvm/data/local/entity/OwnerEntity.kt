package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para Owner (tabla owners)
 * Representa un dueño de mascota registrado
 */
@Entity(tableName = "owners")
data class OwnerEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val avatarRes: Int? = null
)
