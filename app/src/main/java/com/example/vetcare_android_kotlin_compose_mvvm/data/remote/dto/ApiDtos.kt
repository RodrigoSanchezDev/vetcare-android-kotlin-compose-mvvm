package com.example.vetcare_android_kotlin_compose_mvvm.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects (DTOs) para comunicación con API REST
 *
 * Estos DTOs representan el formato JSON esperado del servidor.
 * Se mantienen separados de los modelos de dominio para:
 * - Desacoplar la capa de red de la lógica de negocio
 * - Facilitar cambios en el contrato de API sin afectar el dominio
 * - Permitir mapeo personalizado entre API y modelo local
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */

// ════════════════════════════════════════════════════════════════════════════
// AUTHENTICATION DTOs
// ════════════════════════════════════════════════════════════════════════════

/**
 * Request para login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

/**
 * Response de login exitoso
 */
data class LoginResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String,
    @SerializedName("expiresAt")
    val expiresAt: String
)

/**
 * Request para registro
 */
data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String
)

/**
 * Request para reset de contraseña
 */
data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String
)

/**
 * Response de reset de contraseña
 */
data class ResetPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("temporaryPassword")
    val temporaryPassword: String?
)

// ════════════════════════════════════════════════════════════════════════════
// USER DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Usuario
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("ownerId")
    val ownerId: String?
)

// ════════════════════════════════════════════════════════════════════════════
// PET DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Mascota
 */
data class PetDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("breed")
    val breed: String,
    @SerializedName("ageYears")
    val ageYears: Int,
    @SerializedName("weightKg")
    val weightKg: Double,
    @SerializedName("photoUrl")
    val photoUrl: String?,
    @SerializedName("notes")
    val notes: String?
)

// ════════════════════════════════════════════════════════════════════════════
// OWNER DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Dueño
 */
data class OwnerDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String
)

// ════════════════════════════════════════════════════════════════════════════
// VETERINARIAN DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Veterinario
 */
data class VeterinarianDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("specialty")
    val specialty: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("avatarUrl")
    val avatarUrl: String?
)

// ════════════════════════════════════════════════════════════════════════════
// APPOINTMENT DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Cita
 */
data class AppointmentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("petId")
    val petId: String,
    @SerializedName("vetId")
    val vetId: String,
    @SerializedName("dateTime")
    val dateTime: String, // ISO 8601 format
    @SerializedName("reason")
    val reason: String,
    @SerializedName("status")
    val status: String
)

// ════════════════════════════════════════════════════════════════════════════
// CONSULTATION DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Consulta Médica
 */
data class ConsultationDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("petId")
    val petId: String,
    @SerializedName("vetId")
    val vetId: String,
    @SerializedName("dateTime")
    val dateTime: String, // ISO 8601 format
    @SerializedName("diagnosis")
    val diagnosis: String,
    @SerializedName("treatment")
    val treatment: String,
    @SerializedName("notes")
    val notes: String?
)

// ════════════════════════════════════════════════════════════════════════════
// VACCINE RECORD DTO
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Registro de Vacuna
 */
data class VaccineRecordDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("petId")
    val petId: String,
    @SerializedName("vaccineName")
    val vaccineName: String,
    @SerializedName("lastDate")
    val lastDate: String, // ISO 8601 date format
    @SerializedName("nextDueDate")
    val nextDueDate: String // ISO 8601 date format
)

// ════════════════════════════════════════════════════════════════════════════
// API ERROR RESPONSE
// ════════════════════════════════════════════════════════════════════════════

/**
 * DTO de Error de API
 */
data class ApiErrorResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("timestamp")
    val timestamp: String?
)

