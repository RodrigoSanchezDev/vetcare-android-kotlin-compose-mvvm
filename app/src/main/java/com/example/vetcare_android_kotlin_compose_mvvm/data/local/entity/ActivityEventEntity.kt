package com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidad Room para ActivityEvent (tabla activity_events)
 * Representa un evento de actividad del usuario (log)
 * Nota: metadata se almacena como String JSON serializado
 */
@Entity(tableName = "activity_events")
data class ActivityEventEntity(
    @PrimaryKey
    val id: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val userId: String,
    val screen: String,
    val action: String,
    val metadataJson: String? = null  // JSON serializado de Map<String, String>
)
