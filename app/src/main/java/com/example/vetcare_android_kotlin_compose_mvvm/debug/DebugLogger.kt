package com.example.vetcare_android_kotlin_compose_mvvm.debug

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Sistema de Logging Avanzado para Depuración - VetCare
 *
 * Este módulo implementa un sistema de logging estructurado con:
 * - Niveles de log (DEBUG, INFO, WARN, ERROR, FATAL)
 * - Etiquetas (tags) descriptivas por módulo
 * - Filtros personalizados para Logcat
 * - Métricas de rendimiento (tiempos de ejecución)
 * - Registro de excepciones con stack trace
 *
 * Uso de Logcat con filtros recomendados:
 * - Tag: VETCARE_DEBUG (todos los logs)
 * - Tag: VETCARE_PERF (métricas de rendimiento)
 * - Tag: VETCARE_ERROR (errores y excepciones)
 * - Tag: VETCARE_DB (operaciones de base de datos)
 * - Tag: VETCARE_COROUTINE (flujos asincrónicos)
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
object DebugLogger {

    // ============================================
    // TAGS PARA FILTRADO EN LOGCAT
    // ============================================

    /** Tag principal para debugging general */
    const val TAG_DEBUG = "VETCARE_DEBUG"

    /** Tag para métricas de rendimiento */
    const val TAG_PERF = "VETCARE_PERF"

    /** Tag para errores y excepciones */
    const val TAG_ERROR = "VETCARE_ERROR"

    /** Tag para operaciones de base de datos Room */
    const val TAG_DB = "VETCARE_DB"

    /** Tag para flujos de coroutines */
    const val TAG_COROUTINE = "VETCARE_COROUTINE"

    /** Tag para operaciones del ViewModel */
    const val TAG_VIEWMODEL = "VETCARE_VIEWMODEL"

    /** Tag para operaciones de UI */
    const val TAG_UI = "VETCARE_UI"

    // ============================================
    // NIVELES DE LOG
    // ============================================

    enum class LogLevel(val priority: Int, val emoji: String) {
        DEBUG(Log.DEBUG, "🔍"),
        INFO(Log.INFO, "ℹ️"),
        WARN(Log.WARN, "⚠️"),
        ERROR(Log.ERROR, "❌"),
        FATAL(Log.ASSERT, "💀")
    }

    // ============================================
    // CONFIGURACIÓN
    // ============================================

    /** Habilita/deshabilita logging (desactivar en producción) */
    var isEnabled: Boolean = true

    /** Nivel mínimo de log a mostrar */
    var minLevel: LogLevel = LogLevel.DEBUG

    /** Incluir timestamp en logs */
    var includeTimestamp: Boolean = true

    /** Incluir información del thread */
    var includeThreadInfo: Boolean = true

    private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    // ============================================
    // MÉTODOS DE LOGGING
    // ============================================

    /**
     * Log de nivel DEBUG
     * Para información detallada durante desarrollo
     */
    fun d(tag: String, message: String, metadata: Map<String, Any>? = null) {
        log(LogLevel.DEBUG, tag, message, metadata)
    }

    /**
     * Log de nivel INFO
     * Para eventos importantes del flujo normal
     */
    fun i(tag: String, message: String, metadata: Map<String, Any>? = null) {
        log(LogLevel.INFO, tag, message, metadata)
    }

    /**
     * Log de nivel WARNING
     * Para situaciones potencialmente problemáticas
     */
    fun w(tag: String, message: String, metadata: Map<String, Any>? = null) {
        log(LogLevel.WARN, tag, message, metadata)
    }

    /**
     * Log de nivel ERROR
     * Para errores que no detienen la ejecución
     */
    fun e(tag: String, message: String, throwable: Throwable? = null, metadata: Map<String, Any>? = null) {
        log(LogLevel.ERROR, tag, message, metadata, throwable)
    }

    /**
     * Log de nivel FATAL
     * Para errores críticos que pueden causar crash
     */
    fun fatal(tag: String, message: String, throwable: Throwable? = null, metadata: Map<String, Any>? = null) {
        log(LogLevel.FATAL, tag, message, metadata, throwable)
    }

    /**
     * Método interno de logging
     */
    private fun log(
        level: LogLevel,
        tag: String,
        message: String,
        metadata: Map<String, Any>? = null,
        throwable: Throwable? = null
    ) {
        if (!isEnabled || level.priority < minLevel.priority) return

        val formattedMessage = buildString {
            append(level.emoji)
            append(" ")

            if (includeTimestamp) {
                append("[${LocalDateTime.now().format(dateFormatter)}] ")
            }

            if (includeThreadInfo) {
                append("[${Thread.currentThread().name}] ")
            }

            append(message)

            metadata?.let { meta ->
                append(" | ")
                append(meta.entries.joinToString(", ") { "${it.key}=${it.value}" })
            }
        }

        when (level) {
            LogLevel.DEBUG -> Log.d(tag, formattedMessage)
            LogLevel.INFO -> Log.i(tag, formattedMessage)
            LogLevel.WARN -> Log.w(tag, formattedMessage)
            LogLevel.ERROR -> {
                if (throwable != null) {
                    Log.e(tag, formattedMessage, throwable)
                } else {
                    Log.e(tag, formattedMessage)
                }
            }
            LogLevel.FATAL -> {
                if (throwable != null) {
                    Log.wtf(tag, formattedMessage, throwable)
                } else {
                    Log.wtf(tag, formattedMessage)
                }
            }
        }
    }

    // ============================================
    // LOGGING ESPECIALIZADO
    // ============================================

    /**
     * Log de inicio de operación de base de datos
     */
    fun logDbOperation(operation: String, entity: String, id: String? = null) {
        d(TAG_DB, "DB Operation: $operation", mapOf(
            "entity" to entity,
            "id" to (id ?: "N/A"),
            "dispatcher" to Thread.currentThread().name
        ))
    }

    /**
     * Log de resultado de operación de base de datos
     */
    fun logDbResult(operation: String, entity: String, success: Boolean, count: Int? = null, durationMs: Long? = null) {
        val level = if (success) LogLevel.INFO else LogLevel.WARN
        log(level, TAG_DB, "DB Result: $operation $entity", mapOf(
            "success" to success,
            "count" to (count ?: "N/A"),
            "durationMs" to (durationMs ?: "N/A")
        ))
    }

    /**
     * Log de inicio de coroutine
     */
    fun logCoroutineStart(scope: String, operation: String) {
        d(TAG_COROUTINE, "Coroutine START: $operation", mapOf(
            "scope" to scope,
            "thread" to Thread.currentThread().name
        ))
    }

    /**
     * Log de fin de coroutine
     */
    fun logCoroutineEnd(scope: String, operation: String, durationMs: Long) {
        i(TAG_COROUTINE, "Coroutine END: $operation", mapOf(
            "scope" to scope,
            "durationMs" to durationMs,
            "thread" to Thread.currentThread().name
        ))
    }

    /**
     * Log de excepción en coroutine
     */
    fun logCoroutineException(scope: String, operation: String, exception: Throwable) {
        e(TAG_COROUTINE, "Coroutine EXCEPTION: $operation", exception, mapOf(
            "scope" to scope,
            "exceptionType" to exception.javaClass.simpleName,
            "message" to (exception.message ?: "No message")
        ))
    }

    /**
     * Log de cambio de estado en ViewModel
     */
    fun logStateChange(viewModel: String, field: String, oldValue: Any?, newValue: Any?) {
        d(TAG_VIEWMODEL, "State Change: $viewModel.$field", mapOf(
            "oldValue" to (oldValue?.toString() ?: "null"),
            "newValue" to (newValue?.toString() ?: "null")
        ))
    }

    /**
     * Log de navegación de UI
     */
    fun logNavigation(from: String, to: String, params: Map<String, Any>? = null) {
        i(TAG_UI, "Navigation: $from -> $to", params)
    }

    // ============================================
    // MÉTRICAS DE RENDIMIENTO
    // ============================================

    /**
     * Mide el tiempo de ejecución de un bloque de código
     */
    fun <T> measurePerformance(
        tag: String,
        operation: String,
        block: () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        d(TAG_PERF, "⏱️ START: $operation", mapOf("tag" to tag))

        return try {
            val result = block()
            val duration = System.currentTimeMillis() - startTime

            val level = when {
                duration > 1000 -> LogLevel.WARN  // Más de 1 segundo
                duration > 500 -> LogLevel.INFO   // Más de 500ms
                else -> LogLevel.DEBUG
            }

            log(level, TAG_PERF, "⏱️ END: $operation", mapOf(
                "tag" to tag,
                "durationMs" to duration,
                "status" to "SUCCESS"
            ))

            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            e(TAG_PERF, "⏱️ FAILED: $operation", e, mapOf(
                "tag" to tag,
                "durationMs" to duration,
                "status" to "FAILED"
            ))
            throw e
        }
    }

    /**
     * Mide el tiempo de ejecución de una operación suspend
     * Nota: No se usa inline para evitar problemas con KSP
     */
    suspend fun <T> measureSuspendPerformance(
        tag: String,
        operation: String,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        d(TAG_PERF, "⏱️ ASYNC START: $operation", mapOf("tag" to tag))

        return try {
            val result = block()
            val duration = System.currentTimeMillis() - startTime

            val level = when {
                duration > 1000 -> LogLevel.WARN
                duration > 500 -> LogLevel.INFO
                else -> LogLevel.DEBUG
            }

            log(level, TAG_PERF, "⏱️ ASYNC END: $operation", mapOf(
                "tag" to tag,
                "durationMs" to duration,
                "status" to "SUCCESS"
            ))

            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            e(TAG_PERF, "⏱️ ASYNC FAILED: $operation", e, mapOf(
                "tag" to tag,
                "durationMs" to duration,
                "status" to "FAILED"
            ))
            throw e
        }
    }

    // ============================================
    // EXCEPTION HANDLER PARA COROUTINES
    // ============================================

    /**
     * CoroutineExceptionHandler para capturar excepciones no manejadas
     * Usar en viewModelScope o cualquier CoroutineScope
     */
    fun createExceptionHandler(context: String): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            fatal(TAG_COROUTINE, "Unhandled exception in $context", throwable, mapOf(
                "context" to context,
                "exceptionClass" to throwable.javaClass.name,
                "message" to (throwable.message ?: "No message")
            ))
        }
    }

    // ============================================
    // HELPERS PARA DIAGNÓSTICO
    // ============================================

    /**
     * Log de memoria actual
     */
    fun logMemoryStatus(tag: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024

        val usagePercent = (usedMemory.toDouble() / maxMemory.toDouble() * 100).toInt()

        val level = when {
            usagePercent > 80 -> LogLevel.WARN
            usagePercent > 60 -> LogLevel.INFO
            else -> LogLevel.DEBUG
        }

        log(level, TAG_PERF, "📊 Memory Status", mapOf(
            "tag" to tag,
            "usedMB" to usedMemory,
            "freeMB" to freeMemory,
            "maxMB" to maxMemory,
            "usagePercent" to "$usagePercent%"
        ))
    }

    /**
     * Log de separador visual para Logcat
     */
    fun logSeparator(title: String) {
        Log.d(TAG_DEBUG, "═══════════════════════════════════════════════════")
        Log.d(TAG_DEBUG, "  $title")
        Log.d(TAG_DEBUG, "═══════════════════════════════════════════════════")
    }
}

