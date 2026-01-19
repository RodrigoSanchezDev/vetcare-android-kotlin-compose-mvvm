package com.example.vetcare_android_kotlin_compose_mvvm.data.local.mapper

import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*

/**
 * Mappers para convertir entre entidades Room y modelos de dominio
 * Mantiene separación entre capa de datos (Room) y capa de dominio
 */

// ============================================
// USER MAPPERS
// ============================================

fun UserEntity.toUser(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = role,
    ownerId = ownerId
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = role,
    ownerId = ownerId
)

fun List<UserEntity>.toUsers(): List<User> = map { it.toUser() }
fun List<User>.toUserEntities(): List<UserEntity> = map { it.toEntity() }

// ============================================
// OWNER MAPPERS
// ============================================

fun OwnerEntity.toOwner(): Owner = Owner(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    address = address,
    avatarRes = avatarRes
)

fun Owner.toEntity(): OwnerEntity = OwnerEntity(
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    address = address,
    avatarRes = avatarRes
)

fun List<OwnerEntity>.toOwners(): List<Owner> = map { it.toOwner() }
fun List<Owner>.toOwnerEntities(): List<OwnerEntity> = map { it.toEntity() }

// ============================================
// PET MAPPERS
// ============================================

fun PetEntity.toPet(): Pet = Pet(
    id = id,
    ownerId = ownerId,
    name = name,
    species = species,
    breed = breed,
    ageYears = ageYears,
    weightKg = weightKg,
    photoRes = photoRes,
    notes = notes
)

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    species = species,
    breed = breed,
    ageYears = ageYears,
    weightKg = weightKg,
    photoRes = photoRes,
    notes = notes
)

fun List<PetEntity>.toPets(): List<Pet> = map { it.toPet() }
fun List<Pet>.toPetEntities(): List<PetEntity> = map { it.toEntity() }

// ============================================
// VETERINARIAN MAPPERS
// ============================================

fun VeterinarianEntity.toVeterinarian(): Veterinarian = Veterinarian(
    id = id,
    name = name,
    specialty = specialty,
    phone = phone,
    avatarRes = avatarRes
)

fun Veterinarian.toEntity(): VeterinarianEntity = VeterinarianEntity(
    id = id,
    name = name,
    specialty = specialty,
    phone = phone,
    avatarRes = avatarRes
)

fun List<VeterinarianEntity>.toVeterinarians(): List<Veterinarian> = map { it.toVeterinarian() }
fun List<Veterinarian>.toVeterinarianEntities(): List<VeterinarianEntity> = map { it.toEntity() }

// ============================================
// APPOINTMENT MAPPERS
// ============================================

fun AppointmentEntity.toAppointment(): Appointment = Appointment(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime,
    reason = reason,
    status = status,
    notes = notes
)

fun Appointment.toEntity(): AppointmentEntity = AppointmentEntity(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime,
    reason = reason,
    status = status,
    notes = notes
)

fun List<AppointmentEntity>.toAppointments(): List<Appointment> = map { it.toAppointment() }
fun List<Appointment>.toAppointmentEntities(): List<AppointmentEntity> = map { it.toEntity() }

// ============================================
// CONSULTATION MAPPERS
// ============================================

fun ConsultationEntity.toConsultation(): Consultation = Consultation(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime,
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes
)

fun Consultation.toEntity(): ConsultationEntity = ConsultationEntity(
    id = id,
    petId = petId,
    vetId = vetId,
    dateTime = dateTime,
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes
)

fun List<ConsultationEntity>.toConsultations(): List<Consultation> = map { it.toConsultation() }
fun List<Consultation>.toConsultationEntities(): List<ConsultationEntity> = map { it.toEntity() }

// ============================================
// VACCINE RECORD MAPPERS
// ============================================

fun VaccineRecordEntity.toVaccineRecord(): VaccineRecord = VaccineRecord(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = lastDate,
    nextDueDate = nextDueDate,
    notes = notes
)

fun VaccineRecord.toEntity(): VaccineRecordEntity = VaccineRecordEntity(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = lastDate,
    nextDueDate = nextDueDate,
    notes = notes
)

fun List<VaccineRecordEntity>.toVaccineRecords(): List<VaccineRecord> = map { it.toVaccineRecord() }
fun List<VaccineRecord>.toVaccineRecordEntities(): List<VaccineRecordEntity> = map { it.toEntity() }

// ============================================
// ACTIVITY EVENT MAPPERS
// ============================================

/**
 * Convierte metadata Map a JSON String simple
 */
private fun Map<String, String>?.toJsonString(): String? {
    if (this == null || this.isEmpty()) return null
    return this.entries.joinToString(",", "{", "}") { "\"${it.key}\":\"${it.value}\"" }
}

/**
 * Convierte JSON String simple a metadata Map
 */
private fun String?.toMetadataMap(): Map<String, String>? {
    if (this == null || this == "{}" || this.isEmpty()) return null
    return try {
        this.trim('{', '}')
            .split(",")
            .filter { it.isNotEmpty() }
            .associate {
                val parts = it.split(":")
                parts[0].trim('"') to parts[1].trim('"')
            }
    } catch (e: Exception) {
        null
    }
}

fun ActivityEventEntity.toActivityEvent(): ActivityEvent = ActivityEvent(
    id = id,
    timestamp = timestamp,
    userId = userId,
    screen = screen,
    action = action,
    metadata = metadataJson.toMetadataMap()
)

fun ActivityEvent.toEntity(): ActivityEventEntity = ActivityEventEntity(
    id = id,
    timestamp = timestamp,
    userId = userId,
    screen = screen,
    action = action,
    metadataJson = metadata.toJsonString()
)

fun List<ActivityEventEntity>.toActivityEvents(): List<ActivityEvent> = map { it.toActivityEvent() }
fun List<ActivityEvent>.toActivityEventEntities(): List<ActivityEventEntity> = map { it.toEntity() }
