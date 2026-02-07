package com.example.vetcare_android_kotlin_compose_mvvm.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.time.LocalDateTime

/**
 * Estado UI para el detalle de mascota con información de debug
 */
data class DebugPetDetailUiState(
    val pet: Pet? = null,
    val owner: Owner? = null,
    val consultations: List<Consultation> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val vaccines: List<VaccineRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    // Métricas de debug
    val debugInfo: DebugInfo? = null
)

/**
 * Información de depuración y métricas
 */
data class DebugInfo(
    val totalLoadTimeMs: Long = 0,
    val petLoadTimeMs: Long = 0,
    val ownerLoadTimeMs: Long = 0,
    val consultationsLoadTimeMs: Long = 0,
    val appointmentsLoadTimeMs: Long = 0,
    val vaccinesLoadTimeMs: Long = 0,
    val filterTimeMs: Long = 0,
    val dataSource: String = "Room Database",
    val threadInfo: String = "",
    val memoryUsageMB: Long = 0
)

/**
 * ViewModel para el detalle de mascota con DEPURACIÓN Y MANEJO DE ERRORES ROBUSTO
 *
 * Este ViewModel implementa:
 *
 * 1. LOGGING AVANZADO CON LOGCAT
 *    - Etiquetas (tags) descriptivas: VETCARE_DEBUG, VETCARE_PERF, VETCARE_DB, etc.
 *    - Niveles de log: DEBUG, INFO, WARN, ERROR
 *    - Filtros personalizados para cada tipo de operación
 *    - Timestamps y información de thread
 *
 * 2. MANEJO ESTRUCTURADO DE EXCEPCIONES (TRY-CATCH)
 *    - Bloques try-catch estratégicamente colocados
 *    - Excepciones personalizadas con contexto
 *    - Mensajes de error claros y útiles
 *    - Recovery graceful sin crashes
 *
 * 3. MÉTRICAS DE RENDIMIENTO
 *    - Tiempo de ejecución de cada operación
 *    - Uso de memoria
 *    - Detección de operaciones lentas (>500ms)
 *    - Comparación paralelo vs secuencial
 *
 * 4. CARGA PARALELA OPTIMIZADA
 *    - async/await para datos independientes
 *    - Dispatchers apropiados (IO, Default)
 *    - Timeout configurable
 *    - Manejo de cancelación
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
class DebugPetDetailViewModel : ViewModel() {

    companion object {
        /** Timeout máximo para operaciones de base de datos (ms) */
        private const val DB_TIMEOUT_MS = 5000L

        /** Umbral para detectar operaciones lentas (ms) */
        private const val SLOW_OPERATION_THRESHOLD_MS = 500L

        /** Nombre del ViewModel para logging */
        private const val VIEW_MODEL_NAME = "DebugPetDetailViewModel"
    }

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(DebugPetDetailUiState())
    val uiState: StateFlow<DebugPetDetailUiState> = _uiState.asStateFlow()

    // Exception handler para coroutines no manejadas
    private val exceptionHandler = DebugLogger.createExceptionHandler(VIEW_MODEL_NAME)

    /**
     * Carga los datos de la mascota con depuración completa y manejo de errores robusto
     *
     * FLUJO CRÍTICO SELECCIONADO PARA DEPURACIÓN:
     * Este método fue seleccionado porque:
     * 1. Involucra múltiples operaciones de BD (puede fallar en cualquiera)
     * 2. Usa carga paralela (puede causar condiciones de carrera)
     * 3. Procesa datos en diferentes dispatchers (puede causar problemas de threading)
     * 4. Es un flujo optimizado en semana 3 (ideal para validar)
     *
     * @param petId ID de la mascota a cargar
     */
    fun loadPetWithDebug(petId: String) {
        // ═══════════════════════════════════════════════════════════════
        // INICIO DE FLUJO CRÍTICO - LOGGING INICIAL
        // ═══════════════════════════════════════════════════════════════
        DebugLogger.logSeparator("CARGA DE DETALLE DE MASCOTA")
        DebugLogger.i(DebugLogger.TAG_VIEWMODEL, "Iniciando carga de mascota", mapOf(
            "petId" to petId,
            "viewModel" to VIEW_MODEL_NAME
        ))
        DebugLogger.logMemoryStatus("BEFORE_LOAD")

        val totalStartTime = System.currentTimeMillis()

        // Actualizar estado a loading
        updateState { copy(isLoading = true, error = null) }

        // Lanzar coroutine con exception handler
        viewModelScope.launch(exceptionHandler) {
            // Variables para métricas de tiempo
            var petLoadTime = 0L
            var ownerLoadTime = 0L
            var consultationsLoadTime = 0L
            var appointmentsLoadTime = 0L
            var vaccinesLoadTime = 0L
            var filterTime = 0L

            try {
                // ═══════════════════════════════════════════════════════════════
                // PASO 1: CARGAR MASCOTA (Operación crítica)
                // ═══════════════════════════════════════════════════════════════
                DebugLogger.d(DebugLogger.TAG_DB, "Iniciando consulta de mascota", mapOf("petId" to petId))

                val pet: Pet?
                try {
                    val petStart = System.currentTimeMillis()

                    // Timeout para evitar bloqueos indefinidos
                    pet = withTimeout(DB_TIMEOUT_MS) {
                        withContext(Dispatchers.IO) {
                            DebugLogger.logDbOperation("SELECT", "Pet", petId)
                            repository.getPetById(petId)
                        }
                    }

                    petLoadTime = System.currentTimeMillis() - petStart

                    // Log de resultado con verificación de operación lenta
                    if (petLoadTime > SLOW_OPERATION_THRESHOLD_MS) {
                        DebugLogger.w(DebugLogger.TAG_PERF, "⚠️ OPERACIÓN LENTA: Carga de mascota", mapOf(
                            "petId" to petId,
                            "durationMs" to petLoadTime,
                            "threshold" to SLOW_OPERATION_THRESHOLD_MS
                        ))
                    }
                    DebugLogger.logDbResult("SELECT", "Pet", pet != null, durationMs = petLoadTime)

                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    // TRY-CATCH: Timeout específico
                    throw TimeoutException("Cargar mascota", DB_TIMEOUT_MS, e)
                } catch (e: Exception) {
                    // TRY-CATCH: Error de base de datos
                    throw DatabaseException(
                        message = "Error al consultar mascota en la base de datos",
                        cause = e,
                        operation = "SELECT",
                        entity = "Pet",
                        entityId = petId
                    )
                }

                // ═══════════════════════════════════════════════════════════════
                // VALIDACIÓN: Mascota debe existir
                // ═══════════════════════════════════════════════════════════════
                if (pet == null) {
                    throw EntityNotFoundException("Pet", petId, mapOf(
                        "searchedAt" to LocalDateTime.now().toString()
                    ))
                }

                DebugLogger.i(DebugLogger.TAG_DEBUG, "✅ Mascota cargada exitosamente", mapOf(
                    "petId" to pet.id,
                    "petName" to pet.name,
                    "ownerId" to pet.ownerId
                ))

                // ═══════════════════════════════════════════════════════════════
                // PASO 2: CARGA PARALELA DE DATOS RELACIONADOS
                // ═══════════════════════════════════════════════════════════════
                DebugLogger.d(DebugLogger.TAG_COROUTINE, "Iniciando carga paralela de datos relacionados")
                DebugLogger.logCoroutineStart("viewModelScope", "ParallelDataLoad")

                val parallelStartTime = System.currentTimeMillis()

                // -------- OWNER (async) --------
                val ownerDeferred = async(Dispatchers.IO) {
                    try {
                        val start = System.currentTimeMillis()
                        DebugLogger.logDbOperation("SELECT", "Owner", pet.ownerId)
                        val result = repository.getOwnerById(pet.ownerId)
                        ownerLoadTime = System.currentTimeMillis() - start
                        DebugLogger.logDbResult("SELECT", "Owner", result != null, durationMs = ownerLoadTime)
                        result
                    } catch (e: Exception) {
                        DebugLogger.e(DebugLogger.TAG_DB, "Error cargando owner", e, mapOf(
                            "ownerId" to pet.ownerId
                        ))
                        null // Permitir continuar sin owner (recovery graceful)
                    }
                }

                // -------- CONSULTATIONS (async) --------
                val consultationsDeferred = async(Dispatchers.IO) {
                    try {
                        val start = System.currentTimeMillis()
                        DebugLogger.logDbOperation("SELECT_ALL", "Consultation", petId)
                        val result = repository.getConsultationsByPet(petId)
                        consultationsLoadTime = System.currentTimeMillis() - start
                        DebugLogger.logDbResult("SELECT_ALL", "Consultation", true, result.size, consultationsLoadTime)
                        result
                    } catch (e: Exception) {
                        DebugLogger.e(DebugLogger.TAG_DB, "Error cargando consultas", e, mapOf(
                            "petId" to petId
                        ))
                        emptyList() // Recovery graceful
                    }
                }

                // -------- APPOINTMENTS (async) --------
                val appointmentsDeferred = async(Dispatchers.IO) {
                    try {
                        val start = System.currentTimeMillis()
                        DebugLogger.logDbOperation("SELECT_ALL", "Appointment", petId)
                        val result = repository.getAppointmentsByPet(petId)
                        appointmentsLoadTime = System.currentTimeMillis() - start
                        DebugLogger.logDbResult("SELECT_ALL", "Appointment", true, result.size, appointmentsLoadTime)
                        result
                    } catch (e: Exception) {
                        DebugLogger.e(DebugLogger.TAG_DB, "Error cargando citas", e, mapOf(
                            "petId" to petId
                        ))
                        emptyList()
                    }
                }

                // -------- VACCINES (async) --------
                val vaccinesDeferred = async(Dispatchers.IO) {
                    try {
                        val start = System.currentTimeMillis()
                        DebugLogger.logDbOperation("SELECT_ALL", "VaccineRecord", petId)
                        val result = repository.getVaccinesByPet(petId)
                        vaccinesLoadTime = System.currentTimeMillis() - start
                        DebugLogger.logDbResult("SELECT_ALL", "VaccineRecord", true, result.size, vaccinesLoadTime)
                        result
                    } catch (e: Exception) {
                        DebugLogger.e(DebugLogger.TAG_DB, "Error cargando vacunas", e, mapOf(
                            "petId" to petId
                        ))
                        emptyList()
                    }
                }

                // ═══════════════════════════════════════════════════════════════
                // PASO 3: ESPERAR RESULTADOS PARALELOS
                // ═══════════════════════════════════════════════════════════════
                val owner = ownerDeferred.await()
                val allAppointments = appointmentsDeferred.await()
                val consultations = consultationsDeferred.await()
                val vaccines = vaccinesDeferred.await()

                val parallelDuration = System.currentTimeMillis() - parallelStartTime
                DebugLogger.logCoroutineEnd("viewModelScope", "ParallelDataLoad", parallelDuration)

                // Log de comparación: paralelo vs secuencial
                val sequentialEstimate = ownerLoadTime + consultationsLoadTime + appointmentsLoadTime + vaccinesLoadTime
                DebugLogger.i(DebugLogger.TAG_PERF, "📊 Comparación de rendimiento", mapOf(
                    "parallelTimeMs" to parallelDuration,
                    "sequentialEstimateMs" to sequentialEstimate,
                    "timeSavedMs" to (sequentialEstimate - parallelDuration),
                    "speedupFactor" to String.format("%.2fx", sequentialEstimate.toDouble() / parallelDuration)
                ))

                // ═══════════════════════════════════════════════════════════════
                // PASO 4: FILTRADO DE CITAS (CPU-intensive)
                // ═══════════════════════════════════════════════════════════════
                val filterStart = System.currentTimeMillis()

                val upcomingAppointments = try {
                    withContext(Dispatchers.Default) {
                        DebugLogger.d(DebugLogger.TAG_DEBUG, "Filtrando citas futuras en Default dispatcher")
                        val now = LocalDateTime.now()
                        allAppointments.filter { it.dateTime.isAfter(now) }
                            .sortedBy { it.dateTime }
                    }
                } catch (e: Exception) {
                    DebugLogger.e(DebugLogger.TAG_DEBUG, "Error filtrando citas", e)
                    allAppointments // Fallback: mostrar todas
                }

                filterTime = System.currentTimeMillis() - filterStart
                DebugLogger.d(DebugLogger.TAG_PERF, "Filtrado completado", mapOf(
                    "totalAppointments" to allAppointments.size,
                    "filteredAppointments" to upcomingAppointments.size,
                    "filterTimeMs" to filterTime
                ))

                // ═══════════════════════════════════════════════════════════════
                // PASO 5: ACTUALIZAR ESTADO UI
                // ═══════════════════════════════════════════════════════════════
                val totalLoadTime = System.currentTimeMillis() - totalStartTime

                val debugInfo = DebugInfo(
                    totalLoadTimeMs = totalLoadTime,
                    petLoadTimeMs = petLoadTime,
                    ownerLoadTimeMs = ownerLoadTime,
                    consultationsLoadTimeMs = consultationsLoadTime,
                    appointmentsLoadTimeMs = appointmentsLoadTime,
                    vaccinesLoadTimeMs = vaccinesLoadTime,
                    filterTimeMs = filterTime,
                    dataSource = "Room Database (SQLite)",
                    threadInfo = "Main + IO + Default dispatchers",
                    memoryUsageMB = getMemoryUsageMB()
                )

                _uiState.value = DebugPetDetailUiState(
                    pet = pet,
                    owner = owner,
                    consultations = consultations,
                    appointments = upcomingAppointments,
                    vaccines = vaccines,
                    isLoading = false,
                    debugInfo = debugInfo
                )

                // ═══════════════════════════════════════════════════════════════
                // LOGGING FINAL - RESUMEN DE OPERACIÓN
                // ═══════════════════════════════════════════════════════════════
                DebugLogger.logSeparator("CARGA COMPLETADA EXITOSAMENTE")
                DebugLogger.i(DebugLogger.TAG_DEBUG, "✅ Detalle de mascota cargado", mapOf(
                    "petId" to pet.id,
                    "petName" to pet.name,
                    "totalTimeMs" to totalLoadTime,
                    "consultationsCount" to consultations.size,
                    "appointmentsCount" to upcomingAppointments.size,
                    "vaccinesCount" to vaccines.size
                ))
                DebugLogger.logMemoryStatus("AFTER_LOAD")

            } catch (e: VetCareException) {
                // ═══════════════════════════════════════════════════════════════
                // MANEJO DE EXCEPCIONES PERSONALIZADAS
                // ═══════════════════════════════════════════════════════════════
                handleError(e, petId, totalStartTime)

            } catch (e: Exception) {
                // ═══════════════════════════════════════════════════════════════
                // MANEJO DE EXCEPCIONES GENÉRICAS
                // ═══════════════════════════════════════════════════════════════
                val wrappedException = BusinessLogicException(
                    message = "Error inesperado al cargar detalle de mascota",
                    cause = e,
                    operation = "loadPetDetail",
                    additionalContext = mapOf("petId" to petId)
                )
                handleError(wrappedException, petId, totalStartTime)
            }
        }
    }

    /**
     * Maneja errores de forma estructurada con logging completo
     *
     * TRY-CATCH ESTRATÉGICO:
     * Este método centraliza el manejo de errores para:
     * 1. Logging consistente de todos los errores
     * 2. Mensajes de usuario contextualizados
     * 3. Métricas de tiempo incluso en fallo
     * 4. Recovery graceful sin crash
     */
    private fun handleError(exception: VetCareException, petId: String, startTime: Long) {
        val totalTime = System.currentTimeMillis() - startTime

        // Log detallado del error
        DebugLogger.logSeparator("ERROR EN FLUJO CRÍTICO")
        DebugLogger.e(DebugLogger.TAG_ERROR, exception.toDetailedMessage(), exception, mapOf(
            "petId" to petId,
            "errorCode" to exception.errorCode,
            "durationBeforeErrorMs" to totalTime
        ))

        // Mensaje amigable para el usuario según tipo de error
        val userMessage = when (exception) {
            is EntityNotFoundException -> "La mascota no fue encontrada. Puede haber sido eliminada."
            is DatabaseException -> "Error al acceder a la base de datos. Por favor, intente nuevamente."
            is TimeoutException -> "La operación tardó demasiado. Verifique su dispositivo y reintente."
            is NetworkException -> "Error de conexión. Verifique su conexión a internet."
            is ValidationException -> "Datos inválidos: ${exception.message}"
            else -> "Ocurrió un error: ${exception.message}"
        }

        // Actualizar estado con error
        _uiState.value = DebugPetDetailUiState(
            isLoading = false,
            error = userMessage,
            debugInfo = DebugInfo(
                totalLoadTimeMs = totalTime,
                dataSource = "Error - ${exception.errorCode}"
            )
        )

        // Log de memoria después del error
        DebugLogger.logMemoryStatus("AFTER_ERROR")
    }

    /**
     * Refresca los datos sin mostrar loading completo
     * Implementa los mismos patrones de depuración
     */
    fun refresh(petId: String) {
        DebugLogger.i(DebugLogger.TAG_VIEWMODEL, "Iniciando refresh de mascota", mapOf("petId" to petId))

        _uiState.value = _uiState.value.copy(isRefreshing = true)

        viewModelScope.launch(exceptionHandler) {
            val startTime = System.currentTimeMillis()

            try {
                val pet = DebugLogger.measureSuspendPerformance(
                    tag = "refresh",
                    operation = "getPetById"
                ) {
                    withContext(Dispatchers.IO) {
                        repository.getPetById(petId)
                    }
                }

                if (pet != null) {
                    val ownerDeferred = async(Dispatchers.IO) {
                        try { repository.getOwnerById(pet.ownerId) } catch (e: Exception) { null }
                    }
                    val consultationsDeferred = async(Dispatchers.IO) {
                        try { repository.getConsultationsByPet(petId) } catch (e: Exception) { emptyList() }
                    }
                    val appointmentsDeferred = async(Dispatchers.IO) {
                        try { repository.getAppointmentsByPet(petId) } catch (e: Exception) { emptyList() }
                    }
                    val vaccinesDeferred = async(Dispatchers.IO) {
                        try { repository.getVaccinesByPet(petId) } catch (e: Exception) { emptyList() }
                    }

                    val upcomingAppointments = withContext(Dispatchers.Default) {
                        val now = LocalDateTime.now()
                        appointmentsDeferred.await().filter { it.dateTime.isAfter(now) }.sortedBy { it.dateTime }
                    }

                    val refreshTime = System.currentTimeMillis() - startTime

                    _uiState.value = _uiState.value.copy(
                        pet = pet,
                        owner = ownerDeferred.await(),
                        consultations = consultationsDeferred.await(),
                        appointments = upcomingAppointments,
                        vaccines = vaccinesDeferred.await(),
                        isRefreshing = false,
                        error = null,
                        debugInfo = _uiState.value.debugInfo?.copy(totalLoadTimeMs = refreshTime)
                    )

                    DebugLogger.i(DebugLogger.TAG_DEBUG, "✅ Refresh completado", mapOf(
                        "petId" to petId,
                        "durationMs" to refreshTime
                    ))
                } else {
                    throw EntityNotFoundException("Pet", petId)
                }

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is VetCareException -> e.message
                    else -> "Error al actualizar: ${e.message}"
                }

                DebugLogger.e(DebugLogger.TAG_ERROR, "Error en refresh", e, mapOf("petId" to petId))

                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = errorMessage
                )
            }
        }
    }

    /**
     * Elimina la mascota con manejo de errores robusto
     */
    fun deletePet(): Boolean {
        val petId = _uiState.value.pet?.id ?: run {
            DebugLogger.w(DebugLogger.TAG_VIEWMODEL, "Intento de eliminar sin mascota cargada")
            return false
        }

        DebugLogger.i(DebugLogger.TAG_VIEWMODEL, "Iniciando eliminación de mascota", mapOf("petId" to petId))

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                val success = repository.deletePet(petId)

                if (success) {
                    DebugLogger.i(DebugLogger.TAG_DB, "✅ Mascota eliminada", mapOf("petId" to petId))
                } else {
                    DebugLogger.w(DebugLogger.TAG_DB, "⚠️ No se pudo eliminar la mascota", mapOf("petId" to petId))
                }
            } catch (e: Exception) {
                DebugLogger.e(DebugLogger.TAG_ERROR, "Error eliminando mascota", e, mapOf("petId" to petId))
            }
        }

        return true
    }

    /**
     * Limpia el error actual
     */
    fun clearError() {
        DebugLogger.d(DebugLogger.TAG_VIEWMODEL, "Limpiando error de estado")
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Helper para actualizar estado de forma segura
     */
    private inline fun updateState(update: DebugPetDetailUiState.() -> DebugPetDetailUiState) {
        val oldState = _uiState.value
        val newState = oldState.update()
        _uiState.value = newState

        // Log cambio de estado significativo
        if (oldState.isLoading != newState.isLoading || oldState.error != newState.error) {
            DebugLogger.logStateChange(
                VIEW_MODEL_NAME,
                "uiState",
                "loading=${oldState.isLoading}, error=${oldState.error}",
                "loading=${newState.isLoading}, error=${newState.error}"
            )
        }
    }

    /**
     * Obtiene el uso de memoria actual en MB
     */
    private fun getMemoryUsageMB(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
    }

    /**
     * Método de utilidad para simular errores (solo para testing de debug)
     */
    fun simulateError(errorType: String) {
        DebugLogger.w(DebugLogger.TAG_DEBUG, "🧪 Simulando error para testing", mapOf("errorType" to errorType))

        val exception = when (errorType) {
            "database" -> DatabaseException("Simulación de error de BD", null, "SELECT", "Pet", "test-id")
            "notfound" -> EntityNotFoundException("Pet", "nonexistent-id")
            "timeout" -> TimeoutException("simulatedOperation", 5000)
            "network" -> NetworkException("Simulación de error de red", null, "/api/pets", 500)
            else -> BusinessLogicException("Error simulado genérico", null, "test")
        }

        handleError(exception, "simulated-pet-id", System.currentTimeMillis())
    }
}

