package com.example.vetcare_android_kotlin_compose_mvvm.data.repository

import android.content.Context
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.VetCareDatabase
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.mapper.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * VetCare Repository con persistencia Room (SQLite)
 *
 * Este repositorio proporciona acceso a todos los datos de la aplicación
 * usando Room Database para persistencia local.
 *
 * Los datos se mantienen almacenados en SQLite incluso cuando la app se cierra,
 * cumpliendo el requisito de persistencia local sin conexión a internet.
 */
class VetCareRepository private constructor(context: Context) {

    private val database = VetCareDatabase.getDatabase(context)

    // DAOs
    private val userDao = database.userDao()
    private val ownerDao = database.ownerDao()
    private val petDao = database.petDao()
    private val veterinarianDao = database.veterinarianDao()
    private val appointmentDao = database.appointmentDao()
    private val consultationDao = database.consultationDao()
    private val vaccineRecordDao = database.vaccineRecordDao()
    private val activityEventDao = database.activityEventDao()

    // ============================================
    // USERS - Autenticación
    // ============================================

    fun getAllUsersFlow(): Flow<List<User>> = userDao.getAllUsers().map { it.toUsers() }

    suspend fun getUserById(id: String): User? = userDao.getUserById(id)?.toUser()

    suspend fun findUserByEmail(email: String): User? = userDao.getUserByEmail(email)?.toUser()

    suspend fun authenticateUser(email: String, password: String): User? =
        userDao.authenticateUser(email, password)?.toUser()

    suspend fun insertUser(user: User) = userDao.insertUser(user.toEntity())

    suspend fun updateUser(user: User) = userDao.updateUser(user.toEntity())

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean =
        userDao.updatePassword(userId, newPassword) > 0

    // ============================================
    // OWNERS - Dueños de Mascotas
    // ============================================

    fun getAllOwnersFlow(): Flow<List<Owner>> = ownerDao.getAllOwners().map { it.toOwners() }

    suspend fun getAllOwners(): List<Owner> = ownerDao.getAllOwnersList().toOwners()

    suspend fun getOwnerById(id: String): Owner? = ownerDao.getOwnerById(id)?.toOwner()

    suspend fun insertOwner(owner: Owner) = ownerDao.insertOwner(owner.toEntity())

    suspend fun updateOwner(owner: Owner) = ownerDao.updateOwner(owner.toEntity())

    suspend fun deleteOwner(ownerId: String) = ownerDao.deleteOwnerById(ownerId)

    // ============================================
    // PETS - Mascotas
    // ============================================

    fun getAllPetsFlow(): Flow<List<Pet>> = petDao.getAllPets().map { it.toPets() }

    suspend fun getAllPets(): List<Pet> = petDao.getAllPetsList().toPets()

    suspend fun getPetById(id: String): Pet? = petDao.getPetById(id)?.toPet()

    fun getPetsByOwnerFlow(ownerId: String): Flow<List<Pet>> =
        petDao.getPetsByOwner(ownerId).map { it.toPets() }

    suspend fun getPetsByOwner(ownerId: String): List<Pet> =
        petDao.getPetsByOwnerList(ownerId).toPets()

    suspend fun insertPet(pet: Pet) = petDao.insertPet(pet.toEntity())

    suspend fun updatePet(pet: Pet) = petDao.updatePet(pet.toEntity())

    suspend fun deletePet(petId: String): Boolean = petDao.deletePetById(petId) > 0

    // ============================================
    // VETERINARIANS - Veterinarios
    // ============================================

    fun getAllVeterinariansFlow(): Flow<List<Veterinarian>> =
        veterinarianDao.getAllVeterinarians().map { it.toVeterinarians() }

    suspend fun getAllVeterinarians(): List<Veterinarian> =
        veterinarianDao.getAllVeterinariansList().toVeterinarians()

    suspend fun getVetById(id: String): Veterinarian? =
        veterinarianDao.getVeterinarianById(id)?.toVeterinarian()

    suspend fun insertVeterinarian(vet: Veterinarian) =
        veterinarianDao.insertVeterinarian(vet.toEntity())

    suspend fun updateVeterinarian(vet: Veterinarian) =
        veterinarianDao.updateVeterinarian(vet.toEntity())

    suspend fun deleteVeterinarian(vetId: String): Boolean =
        veterinarianDao.deleteVeterinarianById(vetId) > 0

    // ============================================
    // APPOINTMENTS - Citas
    // ============================================

    fun getAllAppointmentsFlow(): Flow<List<Appointment>> =
        appointmentDao.getAllAppointments().map { it.toAppointments() }

    suspend fun getAllAppointments(): List<Appointment> =
        appointmentDao.getAllAppointmentsList().toAppointments()

    suspend fun getAppointmentById(id: String): Appointment? =
        appointmentDao.getAppointmentById(id)?.toAppointment()

    fun getAppointmentsByPetFlow(petId: String): Flow<List<Appointment>> =
        appointmentDao.getAppointmentsByPet(petId).map { it.toAppointments() }

    suspend fun getAppointmentsByPet(petId: String): List<Appointment> =
        appointmentDao.getAppointmentsByPetList(petId).toAppointments()

    fun getAppointmentsByVetFlow(vetId: String): Flow<List<Appointment>> =
        appointmentDao.getAppointmentsByVet(vetId).map { it.toAppointments() }

    suspend fun getAppointmentsByVet(vetId: String): List<Appointment> =
        appointmentDao.getAppointmentsByVetList(vetId).toAppointments()

    fun getUpcomingAppointmentsFlow(): Flow<List<Appointment>> =
        appointmentDao.getUpcomingAppointments(LocalDateTime.now()).map { it.toAppointments() }

    suspend fun getUpcomingAppointments(): List<Appointment> =
        appointmentDao.getUpcomingAppointmentsList(LocalDateTime.now()).toAppointments()

    suspend fun insertAppointment(appointment: Appointment) =
        appointmentDao.insertAppointment(appointment.toEntity())

    suspend fun updateAppointment(appointment: Appointment) =
        appointmentDao.updateAppointment(appointment.toEntity())

    suspend fun cancelAppointment(appointmentId: String): Boolean =
        appointmentDao.updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED) > 0

    // ============================================
    // CONSULTATIONS - Consultas Médicas
    // ============================================

    fun getAllConsultationsFlow(): Flow<List<Consultation>> =
        consultationDao.getAllConsultations().map { it.toConsultations() }

    suspend fun getAllConsultations(): List<Consultation> =
        consultationDao.getAllConsultationsList().toConsultations()

    fun getConsultationsByPetFlow(petId: String): Flow<List<Consultation>> =
        consultationDao.getConsultationsByPet(petId).map { it.toConsultations() }

    suspend fun getConsultationsByPet(petId: String): List<Consultation> =
        consultationDao.getConsultationsByPetList(petId).toConsultations()

    suspend fun insertConsultation(consultation: Consultation) =
        consultationDao.insertConsultation(consultation.toEntity())

    suspend fun updateConsultation(consultation: Consultation) =
        consultationDao.updateConsultation(consultation.toEntity())

    // ============================================
    // VACCINE RECORDS - Registros de Vacunas
    // ============================================

    fun getAllVaccineRecordsFlow(): Flow<List<VaccineRecord>> =
        vaccineRecordDao.getAllVaccineRecords().map { it.toVaccineRecords() }

    fun getVaccinesByPetFlow(petId: String): Flow<List<VaccineRecord>> =
        vaccineRecordDao.getVaccineRecordsByPet(petId).map { it.toVaccineRecords() }

    suspend fun getVaccinesByPet(petId: String): List<VaccineRecord> =
        vaccineRecordDao.getVaccineRecordsByPetList(petId).toVaccineRecords()

    fun getUpcomingVaccinesFlow(daysAhead: Int = 7): Flow<List<VaccineRecord>> {
        val limitDate = LocalDate.now().plusDays(daysAhead.toLong())
        return vaccineRecordDao.getUpcomingVaccines(limitDate).map { it.toVaccineRecords() }
    }

    suspend fun getUpcomingVaccines(daysAhead: Int = 7): List<VaccineRecord> {
        val limitDate = LocalDate.now().plusDays(daysAhead.toLong())
        return vaccineRecordDao.getUpcomingVaccinesList(limitDate).toVaccineRecords()
    }

    suspend fun insertVaccineRecord(vaccine: VaccineRecord) =
        vaccineRecordDao.insertVaccineRecord(vaccine.toEntity())

    suspend fun updateVaccineRecord(vaccine: VaccineRecord) =
        vaccineRecordDao.updateVaccineRecord(vaccine.toEntity())

    // ============================================
    // ACTIVITY LOG - Registro de Actividad
    // ============================================

    fun getAllActivityEventsFlow(): Flow<List<ActivityEvent>> =
        activityEventDao.getAllActivityEvents().map { it.toActivityEvents() }

    fun getActivitiesByUserFlow(userId: String): Flow<List<ActivityEvent>> =
        activityEventDao.getActivityEventsByUser(userId).map { it.toActivityEvents() }

    suspend fun getActivitiesByUser(userId: String): List<ActivityEvent> =
        activityEventDao.getActivityEventsByUserList(userId).toActivityEvents()

    suspend fun logActivity(event: ActivityEvent) =
        activityEventDao.insertActivityEvent(event.toEntity())

    suspend fun clearActivityLog() = activityEventDao.deleteAllActivityEvents()

    // ============================================
    // UTILIDADES
    // ============================================

    fun generateId(prefix: String): String = "$prefix-${System.currentTimeMillis()}"

    companion object {
        @Volatile
        private var INSTANCE: VetCareRepository? = null

        /**
         * Obtiene la instancia singleton del repositorio
         */
        fun getInstance(context: Context): VetCareRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VetCareRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
