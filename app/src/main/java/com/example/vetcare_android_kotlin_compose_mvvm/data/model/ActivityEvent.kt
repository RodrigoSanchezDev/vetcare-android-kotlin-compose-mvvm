package com.example.vetcare_android_kotlin_compose_mvvm.data.model

import java.time.LocalDateTime

/**
 * Evento de actividad para logging
 * @property id Identificador único
 * @property timestamp Fecha y hora del evento
 * @property userId ID del usuario que realizó la acción
 * @property screen Pantalla donde ocurrió
 * @property action Acción realizada
 * @property metadata Datos adicionales en formato key-value (opcional)
 */
data class ActivityEvent(
    val id: String,
    val timestamp: LocalDateTime,
    val userId: String,
    val screen: String,
    val action: String,
    val metadata: Map<String, String>? = null
)

