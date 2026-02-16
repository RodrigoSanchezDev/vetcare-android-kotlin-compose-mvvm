package com.example.vetcare_android_kotlin_compose_mvvm.data.remote

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.api.VetCareApiService
import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.dto.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.mapper.*
import com.example.vetcare_android_kotlin_compose_mvvm.debug.DebugLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Data Source para operaciones remotas con la API REST
 *
 * Esta clase encapsula todas las llamadas a la API y maneja:
 * - Conversión de respuestas a NetworkResult
 * - Mapeo de DTOs a modelos de dominio
 * - Logging de operaciones para debugging
 * - Manejo de errores HTTP
 *
 * PATRÓN: Repository Pattern - Data Source Layer
 *
 * La arquitectura de datos sigue este flujo:
 * ```
 * UI → ViewModel → Repository → RemoteDataSource → API
 *                             → LocalDataSource → Room
 * ```
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
class RemoteDataSource(
    private val apiService: VetCareApiService = RetrofitClient.apiService
) {

    companion object {
        private const val TAG = "RemoteDataSource"
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTHENTICATION
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Realiza login en el servidor
     */
    suspend fun login(email: String, password: String): NetworkResult<User> {
        return safeApiCall(
            operation = "login",
            apiCall = { apiService.login(LoginRequest(email, password)) },
            transform = { it.user.toUser() }
        )
    }

    /**
     * Solicita reset de contraseña
     */
    suspend fun resetPassword(email: String): NetworkResult<String> {
        return safeApiCall(
            operation = "resetPassword",
            apiCall = { apiService.resetPassword(ResetPasswordRequest(email)) },
            transform = { it.temporaryPassword ?: "Contraseña enviada al email" }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // PETS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene todas las mascotas del servidor
     */
    suspend fun getAllPets(): NetworkResult<List<Pet>> {
        return safeApiCall(
            operation = "getAllPets",
            apiCall = { apiService.getAllPets() },
            transform = { it.toPets() }
        )
    }

    /**
     * Obtiene una mascota por ID
     */
    suspend fun getPetById(id: String): NetworkResult<Pet> {
        return safeApiCall(
            operation = "getPetById",
            apiCall = { apiService.getPetById(id) },
            transform = { it.toPet() }
        )
    }

    /**
     * Obtiene mascotas de un dueño
     */
    suspend fun getPetsByOwner(ownerId: String): NetworkResult<List<Pet>> {
        return safeApiCall(
            operation = "getPetsByOwner",
            apiCall = { apiService.getPetsByOwner(ownerId) },
            transform = { it.toPets() }
        )
    }

    /**
     * Crea una nueva mascota
     */
    suspend fun createPet(pet: Pet): NetworkResult<Pet> {
        return safeApiCall(
            operation = "createPet",
            apiCall = { apiService.createPet(pet.toDto()) },
            transform = { it.toPet() }
        )
    }

    /**
     * Actualiza una mascota existente
     */
    suspend fun updatePet(pet: Pet): NetworkResult<Pet> {
        return safeApiCall(
            operation = "updatePet",
            apiCall = { apiService.updatePet(pet.id, pet.toDto()) },
            transform = { it.toPet() }
        )
    }

    /**
     * Elimina una mascota
     */
    suspend fun deletePet(id: String): NetworkResult<Unit> {
        return safeApiCall(
            operation = "deletePet",
            apiCall = { apiService.deletePet(id) },
            transform = { }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // APPOINTMENTS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene todas las citas
     */
    suspend fun getAllAppointments(): NetworkResult<List<Appointment>> {
        return safeApiCall(
            operation = "getAllAppointments",
            apiCall = { apiService.getAllAppointments() },
            transform = { it.toAppointments() }
        )
    }

    /**
     * Obtiene citas de una mascota
     */
    suspend fun getAppointmentsByPet(petId: String): NetworkResult<List<Appointment>> {
        return safeApiCall(
            operation = "getAppointmentsByPet",
            apiCall = { apiService.getAppointmentsByPet(petId) },
            transform = { it.toAppointments() }
        )
    }

    /**
     * Crea una nueva cita
     */
    suspend fun createAppointment(appointment: Appointment): NetworkResult<Appointment> {
        return safeApiCall(
            operation = "createAppointment",
            apiCall = { apiService.createAppointment(appointment.toDto()) },
            transform = { it.toAppointment() }
        )
    }

    /**
     * Cancela una cita
     */
    suspend fun cancelAppointment(id: String): NetworkResult<Appointment> {
        return safeApiCall(
            operation = "cancelAppointment",
            apiCall = { apiService.cancelAppointment(id) },
            transform = { it.toAppointment() }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // VETERINARIANS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene todos los veterinarios
     */
    suspend fun getAllVeterinarians(): NetworkResult<List<Veterinarian>> {
        return safeApiCall(
            operation = "getAllVeterinarians",
            apiCall = { apiService.getAllVeterinarians() },
            transform = { it.toVeterinarians() }
        )
    }

    /**
     * Obtiene un veterinario por ID
     */
    suspend fun getVeterinarianById(id: String): NetworkResult<Veterinarian> {
        return safeApiCall(
            operation = "getVeterinarianById",
            apiCall = { apiService.getVeterinarianById(id) },
            transform = { it.toVeterinarian() }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // CONSULTATIONS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene consultas de una mascota
     */
    suspend fun getConsultationsByPet(petId: String): NetworkResult<List<Consultation>> {
        return safeApiCall(
            operation = "getConsultationsByPet",
            apiCall = { apiService.getConsultationsByPet(petId) },
            transform = { it.toConsultations() }
        )
    }

    /**
     * Crea una nueva consulta
     */
    suspend fun createConsultation(consultation: Consultation): NetworkResult<Consultation> {
        return safeApiCall(
            operation = "createConsultation",
            apiCall = { apiService.createConsultation(consultation.toDto()) },
            transform = { it.toConsultation() }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // VACCINES
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene vacunas de una mascota
     */
    suspend fun getVaccinesByPet(petId: String): NetworkResult<List<VaccineRecord>> {
        return safeApiCall(
            operation = "getVaccinesByPet",
            apiCall = { apiService.getVaccinesByPet(petId) },
            transform = { it.toVaccineRecords() }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // OWNERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene un dueño por ID
     */
    suspend fun getOwnerById(id: String): NetworkResult<Owner> {
        return safeApiCall(
            operation = "getOwnerById",
            apiCall = { apiService.getOwnerById(id) },
            transform = { it.toOwner() }
        )
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Wrapper seguro para llamadas a la API
     *
     * Maneja:
     * - Ejecución en Dispatchers.IO
     * - Logging de operaciones
     * - Conversión de errores HTTP a NetworkResult.Error
     * - Excepciones de red
     *
     * @param operation Nombre de la operación (para logging)
     * @param apiCall Lambda que ejecuta la llamada Retrofit
     * @param transform Lambda para transformar la respuesta exitosa
     */
    private suspend fun <T, R> safeApiCall(
        operation: String,
        apiCall: suspend () -> Response<T>,
        transform: (T) -> R
    ): NetworkResult<R> {
        return withContext(Dispatchers.IO) {
            try {
                DebugLogger.d(DebugLogger.TAG_DB, "API Call: $operation")
                val startTime = System.currentTimeMillis()

                val response = apiCall()
                val duration = System.currentTimeMillis() - startTime

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        DebugLogger.i(DebugLogger.TAG_DB, "API Success: $operation", mapOf(
                            "durationMs" to duration,
                            "code" to response.code()
                        ))
                        NetworkResult.Success(transform(body))
                    } else {
                        DebugLogger.w(DebugLogger.TAG_ERROR, "API Empty Response: $operation")
                        NetworkResult.Error("Respuesta vacía del servidor", response.code())
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    DebugLogger.e(DebugLogger.TAG_ERROR, "API Error: $operation", null, mapOf(
                        "code" to response.code(),
                        "error" to (errorBody ?: "Unknown")
                    ))
                    NetworkResult.Error(
                        message = parseErrorMessage(errorBody) ?: "Error del servidor",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                DebugLogger.e(DebugLogger.TAG_ERROR, "API Exception: $operation", e)
                NetworkResult.Error(
                    message = getNetworkErrorMessage(e),
                    exception = e
                )
            }
        }
    }

    /**
     * Parsea el mensaje de error del body de respuesta
     */
    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            errorBody?.let {
                // Intentar parsear como ApiErrorResponse
                val gson = com.google.gson.Gson()
                val error = gson.fromJson(it, ApiErrorResponse::class.java)
                error.message
            }
        } catch (e: Exception) {
            errorBody
        }
    }

    /**
     * Genera un mensaje de error amigable según el tipo de excepción
     */
    private fun getNetworkErrorMessage(e: Exception): String {
        return when (e) {
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
            is java.net.ConnectException -> "No se pudo conectar al servidor"
            is javax.net.ssl.SSLException -> "Error de seguridad en la conexión"
            else -> e.message ?: "Error de conexión desconocido"
        }
    }
}

