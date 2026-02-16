package com.example.vetcare_android_kotlin_compose_mvvm.data.remote.mapper

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.dto.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Mappers para convertir entre DTOs de API y modelos de dominio
 *
 * Estos mappers mantienen la separación entre la capa de red
 * y la capa de dominio, permitiendo que cambios en el API
 * no afecten directamente la lógica de negocio.
 *
 * Patrón: Extension Functions para conversión limpia
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */

// ════════════════════════════════════════════════════════════════════════════
// DATE/TIME FORMATTERS
// ════════════════════════════════════════════════════════════════════════════

private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
private val dateFormatter = DateTimeFormatter.ISO_DATE

// ════════════════════════════════════════════════════════════════════════════
// USER MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte UserDto a modelo de dominio User
 */
fun UserDto.toUser(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = "", // No se almacena desde API
    role = UserRole.valueOf(role.uppercase()),
    ownerId = ownerId
)

/**
 * Convierte User a DTO para enviar a API
 */
fun User.toDto(): UserDto = UserDto(
    id = id,
    name = name,
    email = email,
    role = role.name,
    ownerId = ownerId
)

// ════════════════════════════════════════════════════════════════════════════
// PET MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte PetDto a modelo de dominio Pet
 */
fun PetDto.toPet(): Pet = Pet(
    id = id,
    ownerId = ownerId,
    name = name,
    species = PetSpecies.valueOf(species.uppercase()),
    breed = breed,
    ageYears = ageYears,
    weightKg = weightKg,
    photoRes = null, // Las fotos de API serían URLs, no recursos locales
    notes = notes
)

/**
 * Convierte Pet a DTO para enviar a API
 */
fun Pet.toDto(): PetDto = PetDto(
    id = id,
    ownerId = ownerId,
    name = name,
    species = species.name,
    breed = breed ?: "",
    ageYears = ageYears,
    weightKg = weightKg ?: 0.0,
    photoUrl = null, // Convertir photoRes a URL si es necesario
    notes = notes
)

/**
 * Convierte lista de PetDto a lista de Pet
 */
fun List<PetDto>.toPets(): List<Pet> = map { it.toPet() }

// ════════════════════════════════════════════════════════════════════════════
// OWNER MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte OwnerDto a modelo de dominio Owner
 */
fun OwnerDto.toOwner(): Owner = Owner(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    address = address
)

/**
 * Convierte Owner a DTO
 */
fun Owner.toDto(): OwnerDto = OwnerDto(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone ?: "",
    address = address ?: ""
)

// ════════════════════════════════════════════════════════════════════════════
// VETERINARIAN MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte VeterinarianDto a modelo de dominio Veterinarian
 */
fun VeterinarianDto.toVeterinarian(): Veterinarian = Veterinarian(
    id = id,
    name = name,
    specialty = specialty,
    phone = phone,
    avatarRes = null // Avatar de API sería URL
)

/**
 * Convierte Veterinarian a DTO
 */
fun Veterinarian.toDto(): VeterinarianDto = VeterinarianDto(
    id = id,
    name = name,
    specialty = specialty ?: "",
    phone = phone ?: "",
    avatarUrl = null
)

/**
 * Convierte lista de VeterinarianDto a lista de Veterinarian
 */
fun List<VeterinarianDto>.toVeterinarians(): List<Veterinarian> = map { it.toVeterinarian() }

// ════════════════════════════════════════════════════════════════════════════
// APPOINTMENT MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte AppointmentDto a modelo de dominio Appointment
 */
fun AppointmentDto.toAppointment(): Appointment = Appointment(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = LocalDateTime.parse(dateTime, dateTimeFormatter),
    reason = reason,
    status = AppointmentStatus.valueOf(status.uppercase())
)

/**
 * Convierte Appointment a DTO
 */
fun Appointment.toDto(): AppointmentDto = AppointmentDto(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime.format(dateTimeFormatter),
    reason = reason,
    status = status.name
)

/**
 * Convierte lista de AppointmentDto a lista de Appointment
 */
fun List<AppointmentDto>.toAppointments(): List<Appointment> = map { it.toAppointment() }

// ════════════════════════════════════════════════════════════════════════════
// CONSULTATION MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte ConsultationDto a modelo de dominio Consultation
 */
fun ConsultationDto.toConsultation(): Consultation = Consultation(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = LocalDateTime.parse(dateTime, dateTimeFormatter),
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes
)

/**
 * Convierte Consultation a DTO
 */
fun Consultation.toDto(): ConsultationDto = ConsultationDto(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime.format(dateTimeFormatter),
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes
)

/**
 * Convierte lista de ConsultationDto a lista de Consultation
 */
fun List<ConsultationDto>.toConsultations(): List<Consultation> = map { it.toConsultation() }

// ════════════════════════════════════════════════════════════════════════════
// VACCINE RECORD MAPPERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Convierte VaccineRecordDto a modelo de dominio VaccineRecord
 */
fun VaccineRecordDto.toVaccineRecord(): VaccineRecord = VaccineRecord(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = LocalDate.parse(lastDate, dateFormatter),
    nextDueDate = LocalDate.parse(nextDueDate, dateFormatter)
)

/**
 * Convierte VaccineRecord a DTO
 */
fun VaccineRecord.toDto(): VaccineRecordDto = VaccineRecordDto(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = lastDate.format(dateFormatter),
    nextDueDate = nextDueDate.format(dateFormatter)
)

/**
 * Convierte lista de VaccineRecordDto a lista de VaccineRecord
 */
fun List<VaccineRecordDto>.toVaccineRecords(): List<VaccineRecord> = map { it.toVaccineRecord() }




