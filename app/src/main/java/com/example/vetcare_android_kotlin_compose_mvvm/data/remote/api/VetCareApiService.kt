package com.example.vetcare_android_kotlin_compose_mvvm.data.remote.api

import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de API REST para VetCare usando Retrofit
 *
 * Define todos los endpoints disponibles para comunicación con el backend.
 * Aunque actualmente la app usa datos locales con Room, esta interfaz
 * está preparada para integración futura con un servidor real.
 *
 * JUSTIFICACIÓN TÉCNICA DE RETROFIT:
 * ─────────────────────────────────────────────────────────────────────────
 * 1. Type-Safety: Convierte JSON a objetos Kotlin automáticamente
 * 2. Coroutines Support: Funciones suspend para integración con viewModelScope
 * 3. Interceptors: Logging, autenticación, retry automático
 * 4. Escalabilidad: Preparado para migración a backend real
 * 5. Estándar de Industria: Biblioteca más utilizada para REST en Android
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
interface VetCareApiService {

    // ════════════════════════════════════════════════════════════════════
    // AUTHENTICATION ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Autenticación de usuario
     * POST /api/auth/login
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registro de nuevo usuario
     * POST /api/auth/register
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    /**
     * Solicitar reset de contraseña
     * POST /api/auth/reset-password
     */
    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // ════════════════════════════════════════════════════════════════════
    // PETS ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener todas las mascotas
     * GET /api/pets
     */
    @GET("api/pets")
    suspend fun getAllPets(): Response<List<PetDto>>

    /**
     * Obtener mascota por ID
     * GET /api/pets/{id}
     */
    @GET("api/pets/{id}")
    suspend fun getPetById(@Path("id") id: String): Response<PetDto>

    /**
     * Obtener mascotas de un dueño
     * GET /api/pets/owner/{ownerId}
     */
    @GET("api/pets/owner/{ownerId}")
    suspend fun getPetsByOwner(@Path("ownerId") ownerId: String): Response<List<PetDto>>

    /**
     * Crear nueva mascota
     * POST /api/pets
     */
    @POST("api/pets")
    suspend fun createPet(@Body pet: PetDto): Response<PetDto>

    /**
     * Actualizar mascota
     * PUT /api/pets/{id}
     */
    @PUT("api/pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body pet: PetDto): Response<PetDto>

    /**
     * Eliminar mascota
     * DELETE /api/pets/{id}
     */
    @DELETE("api/pets/{id}")
    suspend fun deletePet(@Path("id") id: String): Response<Unit>

    // ════════════════════════════════════════════════════════════════════
    // APPOINTMENTS ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener todas las citas
     * GET /api/appointments
     */
    @GET("api/appointments")
    suspend fun getAllAppointments(): Response<List<AppointmentDto>>

    /**
     * Obtener citas de una mascota
     * GET /api/appointments/pet/{petId}
     */
    @GET("api/appointments/pet/{petId}")
    suspend fun getAppointmentsByPet(@Path("petId") petId: String): Response<List<AppointmentDto>>

    /**
     * Obtener citas de un veterinario
     * GET /api/appointments/vet/{vetId}
     */
    @GET("api/appointments/vet/{vetId}")
    suspend fun getAppointmentsByVet(@Path("vetId") vetId: String): Response<List<AppointmentDto>>

    /**
     * Crear nueva cita
     * POST /api/appointments
     */
    @POST("api/appointments")
    suspend fun createAppointment(@Body appointment: AppointmentDto): Response<AppointmentDto>

    /**
     * Actualizar cita
     * PUT /api/appointments/{id}
     */
    @PUT("api/appointments/{id}")
    suspend fun updateAppointment(
        @Path("id") id: String,
        @Body appointment: AppointmentDto
    ): Response<AppointmentDto>

    /**
     * Cancelar cita
     * PATCH /api/appointments/{id}/cancel
     */
    @PATCH("api/appointments/{id}/cancel")
    suspend fun cancelAppointment(@Path("id") id: String): Response<AppointmentDto>

    // ════════════════════════════════════════════════════════════════════
    // VETERINARIANS ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener todos los veterinarios
     * GET /api/veterinarians
     */
    @GET("api/veterinarians")
    suspend fun getAllVeterinarians(): Response<List<VeterinarianDto>>

    /**
     * Obtener veterinario por ID
     * GET /api/veterinarians/{id}
     */
    @GET("api/veterinarians/{id}")
    suspend fun getVeterinarianById(@Path("id") id: String): Response<VeterinarianDto>

    // ════════════════════════════════════════════════════════════════════
    // CONSULTATIONS ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener consultas de una mascota
     * GET /api/consultations/pet/{petId}
     */
    @GET("api/consultations/pet/{petId}")
    suspend fun getConsultationsByPet(@Path("petId") petId: String): Response<List<ConsultationDto>>

    /**
     * Crear nueva consulta
     * POST /api/consultations
     */
    @POST("api/consultations")
    suspend fun createConsultation(@Body consultation: ConsultationDto): Response<ConsultationDto>

    // ════════════════════════════════════════════════════════════════════
    // VACCINES ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener vacunas de una mascota
     * GET /api/vaccines/pet/{petId}
     */
    @GET("api/vaccines/pet/{petId}")
    suspend fun getVaccinesByPet(@Path("petId") petId: String): Response<List<VaccineRecordDto>>

    /**
     * Registrar nueva vacuna
     * POST /api/vaccines
     */
    @POST("api/vaccines")
    suspend fun createVaccineRecord(@Body vaccine: VaccineRecordDto): Response<VaccineRecordDto>

    // ════════════════════════════════════════════════════════════════════
    // OWNERS ENDPOINTS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Obtener dueño por ID
     * GET /api/owners/{id}
     */
    @GET("api/owners/{id}")
    suspend fun getOwnerById(@Path("id") id: String): Response<OwnerDto>

    /**
     * Actualizar información de dueño
     * PUT /api/owners/{id}
     */
    @PUT("api/owners/{id}")
    suspend fun updateOwner(@Path("id") id: String, @Body owner: OwnerDto): Response<OwnerDto>
}

