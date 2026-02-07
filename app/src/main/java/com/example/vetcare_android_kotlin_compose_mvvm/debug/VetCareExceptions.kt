package com.example.vetcare_android_kotlin_compose_mvvm.debug

/**
 * Clases personalizadas de excepciones para VetCare
 *
 * Este módulo define excepciones específicas del dominio para:
 * - Mejor trazabilidad de errores
 * - Mensajes contextualizados
 * - Facilitar el debug y logging
 * - Manejo diferenciado según tipo de error
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */

/**
 * Excepción base para errores de VetCare
 * Todas las excepciones personalizadas heredan de esta
 */
open class VetCareException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: String = "VETCARE_ERROR",
    val context: Map<String, Any>? = null
) : Exception(message, cause) {

    /**
     * Genera un mensaje detallado para logging
     */
    fun toDetailedMessage(): String = buildString {
        append("[$errorCode] $message")
        context?.let { ctx ->
            append(" | Context: ")
            append(ctx.entries.joinToString(", ") { "${it.key}=${it.value}" })
        }
        cause?.let {
            append(" | Caused by: ${it.javaClass.simpleName}: ${it.message}")
        }
    }
}

// ============================================
// EXCEPCIONES DE BASE DE DATOS
// ============================================

/**
 * Error en operaciones de base de datos Room
 */
class DatabaseException(
    message: String,
    cause: Throwable? = null,
    val operation: String,
    val entity: String,
    val entityId: String? = null
) : VetCareException(
    message = message,
    cause = cause,
    errorCode = "DB_ERROR",
    context = mapOf(
        "operation" to operation,
        "entity" to entity,
        "entityId" to (entityId ?: "N/A")
    )
)

/**
 * Entidad no encontrada en la base de datos
 */
class EntityNotFoundException(
    entity: String,
    entityId: String,
    additionalContext: Map<String, Any>? = null
) : VetCareException(
    message = "$entity con ID '$entityId' no encontrado en la base de datos",
    errorCode = "ENTITY_NOT_FOUND",
    context = mapOf(
        "entity" to entity,
        "entityId" to entityId
    ).plus(additionalContext ?: emptyMap())
)

/**
 * Error de integridad de datos
 */
class DataIntegrityException(
    message: String,
    cause: Throwable? = null,
    entity: String,
    field: String? = null,
    value: Any? = null
) : VetCareException(
    message = message,
    cause = cause,
    errorCode = "DATA_INTEGRITY_ERROR",
    context = mapOf(
        "entity" to entity,
        "field" to (field ?: "N/A"),
        "invalidValue" to (value?.toString() ?: "N/A")
    )
)

// ============================================
// EXCEPCIONES DE RED Y CONECTIVIDAD
// ============================================

/**
 * Error de conexión de red (para futuras integraciones)
 */
class NetworkException(
    message: String,
    cause: Throwable? = null,
    val endpoint: String? = null,
    val statusCode: Int? = null
) : VetCareException(
    message = message,
    cause = cause,
    errorCode = "NETWORK_ERROR",
    context = mapOf(
        "endpoint" to (endpoint ?: "N/A"),
        "statusCode" to (statusCode?.toString() ?: "N/A")
    )
)

/**
 * Timeout en operación
 */
class TimeoutException(
    operation: String,
    timeoutMs: Long,
    cause: Throwable? = null
) : VetCareException(
    message = "La operación '$operation' excedió el tiempo límite de ${timeoutMs}ms",
    cause = cause,
    errorCode = "TIMEOUT_ERROR",
    context = mapOf(
        "operation" to operation,
        "timeoutMs" to timeoutMs
    )
)

// ============================================
// EXCEPCIONES DE VALIDACIÓN
// ============================================

/**
 * Error de validación de datos de entrada
 */
class ValidationException(
    field: String,
    message: String,
    value: Any? = null,
    val validationRule: String? = null
) : VetCareException(
    message = "Validación fallida en campo '$field': $message",
    errorCode = "VALIDATION_ERROR",
    context = mapOf(
        "field" to field,
        "invalidValue" to (value?.toString() ?: "null"),
        "rule" to (validationRule ?: "N/A")
    )
)

/**
 * Error cuando faltan campos requeridos
 */
class RequiredFieldException(
    vararg fields: String
) : VetCareException(
    message = "Campos requeridos faltantes: ${fields.joinToString(", ")}",
    errorCode = "REQUIRED_FIELD_ERROR",
    context = mapOf(
        "missingFields" to fields.toList(),
        "count" to fields.size
    )
)

// ============================================
// EXCEPCIONES DE AUTENTICACIÓN Y SESIÓN
// ============================================

/**
 * Error de autenticación
 */
class AuthenticationException(
    message: String,
    cause: Throwable? = null,
    val attemptedEmail: String? = null
) : VetCareException(
    message = message,
    cause = cause,
    errorCode = "AUTH_ERROR",
    context = mapOf(
        "attemptedEmail" to (attemptedEmail ?: "N/A")
    )
)

/**
 * Sesión expirada o inválida
 */
class SessionException(
    message: String = "La sesión ha expirado o es inválida. Por favor, inicie sesión nuevamente.",
    val userId: String? = null
) : VetCareException(
    message = message,
    errorCode = "SESSION_ERROR",
    context = mapOf(
        "userId" to (userId ?: "N/A")
    )
)

/**
 * Error de permisos insuficientes
 */
class PermissionDeniedException(
    action: String,
    requiredRole: String,
    currentRole: String? = null
) : VetCareException(
    message = "Permiso denegado para la acción '$action'. Se requiere rol: $requiredRole",
    errorCode = "PERMISSION_DENIED",
    context = mapOf(
        "action" to action,
        "requiredRole" to requiredRole,
        "currentRole" to (currentRole ?: "N/A")
    )
)

// ============================================
// EXCEPCIONES DE FLUJO DE NEGOCIO
// ============================================

/**
 * Error en operación de negocio
 */
class BusinessLogicException(
    message: String,
    cause: Throwable? = null,
    val operation: String,
    additionalContext: Map<String, Any>? = null
) : VetCareException(
    message = message,
    cause = cause,
    errorCode = "BUSINESS_ERROR",
    context = mapOf("operation" to operation).plus(additionalContext ?: emptyMap())
)

/**
 * Estado inválido para la operación
 */
class InvalidStateException(
    message: String,
    val currentState: String,
    val expectedState: String? = null,
    val operation: String
) : VetCareException(
    message = message,
    errorCode = "INVALID_STATE",
    context = mapOf(
        "currentState" to currentState,
        "expectedState" to (expectedState ?: "N/A"),
        "operation" to operation
    )
)

/**
 * Conflicto de datos (ej: cita en horario ocupado)
 */
class ConflictException(
    message: String,
    val conflictType: String,
    additionalContext: Map<String, Any>? = null
) : VetCareException(
    message = message,
    errorCode = "CONFLICT_ERROR",
    context = mapOf("conflictType" to conflictType).plus(additionalContext ?: emptyMap())
)

// ============================================
// EXCEPCIONES DE RECURSOS
// ============================================

/**
 * Recurso no disponible
 */
class ResourceUnavailableException(
    resourceType: String,
    resourceId: String? = null,
    message: String = "El recurso '$resourceType' no está disponible"
) : VetCareException(
    message = message,
    errorCode = "RESOURCE_UNAVAILABLE",
    context = mapOf(
        "resourceType" to resourceType,
        "resourceId" to (resourceId ?: "N/A")
    )
)

/**
 * Límite de recursos excedido
 */
class ResourceLimitExceededException(
    resourceType: String,
    limit: Int,
    current: Int
) : VetCareException(
    message = "Límite excedido para '$resourceType': actual $current, máximo $limit",
    errorCode = "RESOURCE_LIMIT_EXCEEDED",
    context = mapOf(
        "resourceType" to resourceType,
        "limit" to limit,
        "current" to current
    )
)

