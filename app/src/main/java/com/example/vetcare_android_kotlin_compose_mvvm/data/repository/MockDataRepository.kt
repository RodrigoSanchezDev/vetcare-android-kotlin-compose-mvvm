package com.example.vetcare_android_kotlin_compose_mvvm.data.repository

import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio Mock con datos in-memory para VetCare
 * Singleton que simula persistencia de datos
 */
object MockDataRepository {

    // ============================================
    // USUARIOS (para autenticación)
    // ============================================
    private val _users = mutableListOf(
        User(
            id = "user-admin-001",
            name = "Administrador VetCare",
            email = "admin@vet.cl",
            passwordHash = "123456",
            role = UserRole.ADMIN,
            ownerId = null
        ),
        User(
            id = "user-owner-001",
            name = "María González",
            email = "owner@vet.cl",
            passwordHash = "123456",
            role = UserRole.OWNER,
            ownerId = "owner-001"
        )
    )
    val users: List<User> get() = _users.toList()

    // ============================================
    // DUEÑOS DE MASCOTAS
    // ============================================
    private val _owners = mutableListOf(
        Owner(
            id = "owner-001",
            fullName = "María González",
            email = "owner@vet.cl",
            phone = "+56 9 1234 5678",
            address = "Av. Principal 123, Santiago"
        ),
        Owner(
            id = "owner-002",
            fullName = "Carlos Rodríguez",
            email = "carlos@email.cl",
            phone = "+56 9 8765 4321",
            address = "Calle Los Aromos 456, Providencia"
        )
    )
    val owners: List<Owner> get() = _owners.toList()

    // ============================================
    // MASCOTAS
    // ============================================
    private val _pets = mutableListOf(
        Pet(
            id = "pet-001",
            ownerId = "owner-001",
            name = "Max",
            species = PetSpecies.DOG,
            breed = "West Highland Terrier",
            ageYears = 3,
            weightKg = 8.5,
            photoRes = R.drawable.pet_max,
            notes = "Muy activo, le gusta pasear"
        ),
        Pet(
            id = "pet-002",
            ownerId = "owner-001",
            name = "Luna",
            species = PetSpecies.CAT,
            breed = "Gato Naranja",
            ageYears = 2,
            weightKg = 4.2,
            photoRes = R.drawable.pet_luna,
            notes = "Tranquila, le gusta dormir al sol"
        ),
        Pet(
            id = "pet-003",
            ownerId = "owner-002",
            name = "Rocky",
            species = PetSpecies.DOG,
            breed = "Golden Retriever",
            ageYears = 5,
            weightKg = 32.0,
            photoRes = R.drawable.pet_rocky,
            notes = "Muy amigable con otros perros"
        ),
        Pet(
            id = "pet-004",
            ownerId = "owner-001",
            name = "Michi",
            species = PetSpecies.CAT,
            breed = "Gato Mestizo",
            ageYears = 4,
            weightKg = 5.0,
            photoRes = R.drawable.pet_michi,
            notes = "Muy cariñoso"
        )
    )
    val pets: List<Pet> get() = _pets.toList()

    // ============================================
    // VETERINARIOS
    // ============================================
    private val _veterinarians = mutableListOf(
        Veterinarian(
            id = "vet-001",
            name = "Dr. Pedro Sánchez",
            specialty = "Medicina General",
            phone = "+56 9 1111 2222",
            avatarRes = R.drawable.vet_pedro_gonzalez
        ),
        Veterinarian(
            id = "vet-002",
            name = "Dra. Ana Martínez",
            specialty = "Cirugía",
            phone = "+56 9 3333 4444",
            avatarRes = R.drawable.vet_maria_rodriguez
        ),
        Veterinarian(
            id = "vet-003",
            name = "Dr. Luis Torres",
            specialty = "Dermatología",
            phone = "+56 9 5555 6666",
            avatarRes = R.drawable.vet_carlos_martinez
        )
    )
    val veterinarians: List<Veterinarian> get() = _veterinarians.toList()

    // ============================================
    // CONSULTAS MÉDICAS
    // ============================================
    private val _consultations = mutableListOf(
        Consultation(
            id = "cons-001",
            petId = "pet-001",
            vetId = "vet-001",
            dateTime = LocalDateTime.now().minusDays(30),
            diagnosis = "Control de rutina - mascota saludable",
            treatment = "Vitaminas preventivas por 15 días",
            notes = "Próximo control en 6 meses"
        ),
        Consultation(
            id = "cons-002",
            petId = "pet-001",
            vetId = "vet-003",
            dateTime = LocalDateTime.now().minusDays(15),
            diagnosis = "Dermatitis leve en zona abdominal",
            treatment = "Shampoo medicado + crema tópica",
            notes = "Evolución favorable"
        ),
        Consultation(
            id = "cons-003",
            petId = "pet-002",
            vetId = "vet-001",
            dateTime = LocalDateTime.now().minusDays(60),
            diagnosis = "Vacunación anual completada",
            treatment = "Triple felina aplicada",
            notes = "Sin reacciones adversas"
        )
    )
    val consultations: List<Consultation> get() = _consultations.toList()

    // ============================================
    // CITAS PROGRAMADAS
    // ============================================
    private val _appointments = mutableListOf(
        Appointment(
            id = "apt-001",
            petId = "pet-001",
            vetId = "vet-001",
            dateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
            reason = "Control post-tratamiento dermatológico",
            status = AppointmentStatus.CONFIRMED
        ),
        Appointment(
            id = "apt-002",
            petId = "pet-002",
            vetId = "vet-002",
            dateTime = LocalDateTime.now().plusDays(7).withHour(15).withMinute(30),
            reason = "Esterilización programada",
            status = AppointmentStatus.SCHEDULED
        ),
        Appointment(
            id = "apt-003",
            petId = "pet-003",
            vetId = "vet-001",
            dateTime = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0),
            reason = "Vacunación anual",
            status = AppointmentStatus.CONFIRMED
        )
    )
    val appointments: List<Appointment> get() = _appointments.toList()

    // ============================================
    // REGISTROS DE VACUNAS
    // ============================================
    private val _vaccineRecords = mutableListOf(
        VaccineRecord(
            id = "vac-001",
            petId = "pet-001",
            vaccineName = "Antirrábica",
            lastDate = LocalDate.now().minusMonths(6),
            nextDueDate = LocalDate.now().plusMonths(6)
        ),
        VaccineRecord(
            id = "vac-002",
            petId = "pet-001",
            vaccineName = "Séxtuple",
            lastDate = LocalDate.now().minusMonths(10),
            nextDueDate = LocalDate.now().plusDays(5) // ¡Próxima a vencer!
        ),
        VaccineRecord(
            id = "vac-003",
            petId = "pet-002",
            vaccineName = "Triple Felina",
            lastDate = LocalDate.now().minusMonths(2),
            nextDueDate = LocalDate.now().plusMonths(10)
        ),
        VaccineRecord(
            id = "vac-004",
            petId = "pet-002",
            vaccineName = "Antirrábica",
            lastDate = LocalDate.now().minusMonths(11),
            nextDueDate = LocalDate.now().plusDays(3) // ¡Próxima a vencer!
        )
    )
    val vaccineRecords: List<VaccineRecord> get() = _vaccineRecords.toList()

    // ============================================
    // EVENTOS DE ACTIVIDAD (LOG)
    // ============================================
    private val _activityEvents = mutableListOf<ActivityEvent>()
    private val _activityEventsFlow = MutableStateFlow<List<ActivityEvent>>(emptyList())
    val activityEvents: List<ActivityEvent> get() = _activityEvents.toList()
    val activityEventsFlow: StateFlow<List<ActivityEvent>> = _activityEventsFlow.asStateFlow()

    // ============================================
    // FUNCIONES CRUD - USERS
    // ============================================
    fun findUserByEmail(email: String): User? = _users.find { it.email.equals(email, ignoreCase = true) }

    fun authenticateUser(email: String, password: String): User? {
        return _users.find {
            it.email.equals(email, ignoreCase = true) && it.passwordHash == password
        }
    }

    fun updateUserPassword(userId: String, newPassword: String): Boolean {
        val index = _users.indexOfFirst { it.id == userId }
        if (index != -1) {
            _users[index] = _users[index].copy(passwordHash = newPassword)
            return true
        }
        return false
    }

    // ============================================
    // FUNCIONES CRUD - OWNERS
    // ============================================
    fun getOwnerById(id: String): Owner? = _owners.find { it.id == id }

    fun addOwner(owner: Owner) {
        _owners.add(owner)
    }

    fun updateOwner(owner: Owner): Boolean {
        val index = _owners.indexOfFirst { it.id == owner.id }
        if (index != -1) {
            _owners[index] = owner
            return true
        }
        return false
    }

    // ============================================
    // FUNCIONES CRUD - PETS
    // ============================================
    fun getPetById(id: String): Pet? = _pets.find { it.id == id }

    fun getPetsByOwner(ownerId: String): List<Pet> = _pets.filter { it.ownerId == ownerId }

    fun addPet(pet: Pet) {
        _pets.add(pet)
    }

    fun updatePet(pet: Pet): Boolean {
        val index = _pets.indexOfFirst { it.id == pet.id }
        if (index != -1) {
            _pets[index] = pet
            return true
        }
        return false
    }

    fun deletePet(petId: String): Boolean {
        return _pets.removeIf { it.id == petId }
    }

    // ============================================
    // FUNCIONES CRUD - VETERINARIANS
    // ============================================
    fun getVetById(id: String): Veterinarian? = _veterinarians.find { it.id == id }

    fun addVeterinarian(vet: Veterinarian) {
        _veterinarians.add(vet)
    }

    fun updateVeterinarian(vet: Veterinarian): Boolean {
        val index = _veterinarians.indexOfFirst { it.id == vet.id }
        if (index != -1) {
            _veterinarians[index] = vet
            return true
        }
        return false
    }

    fun deleteVeterinarian(vetId: String): Boolean {
        return _veterinarians.removeIf { it.id == vetId }
    }

    // ============================================
    // FUNCIONES CRUD - CONSULTATIONS
    // ============================================
    fun getConsultationsByPet(petId: String): List<Consultation> =
        _consultations.filter { it.petId == petId }.sortedByDescending { it.dateTime }

    fun addConsultation(consultation: Consultation) {
        _consultations.add(consultation)
    }

    fun updateConsultation(consultation: Consultation): Boolean {
        val index = _consultations.indexOfFirst { it.id == consultation.id }
        if (index != -1) {
            _consultations[index] = consultation
            return true
        }
        return false
    }

    // ============================================
    // FUNCIONES CRUD - APPOINTMENTS
    // ============================================
    fun getAppointmentById(id: String): Appointment? = _appointments.find { it.id == id }

    fun getAppointmentsByPet(petId: String): List<Appointment> =
        _appointments.filter { it.petId == petId }.sortedBy { it.dateTime }

    fun getUpcomingAppointments(): List<Appointment> =
        _appointments.filter {
            it.dateTime.isAfter(LocalDateTime.now()) &&
            it.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }.sortedBy { it.dateTime }

    fun getAppointmentsByVet(vetId: String): List<Appointment> =
        _appointments.filter { it.vetId == vetId }.sortedBy { it.dateTime }

    fun addAppointment(appointment: Appointment) {
        _appointments.add(appointment)
    }

    fun updateAppointment(appointment: Appointment): Boolean {
        val index = _appointments.indexOfFirst { it.id == appointment.id }
        if (index != -1) {
            _appointments[index] = appointment
            return true
        }
        return false
    }

    fun cancelAppointment(appointmentId: String): Boolean {
        val index = _appointments.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            _appointments[index] = _appointments[index].copy(status = AppointmentStatus.CANCELLED)
            return true
        }
        return false
    }

    // ============================================
    // FUNCIONES CRUD - VACCINE RECORDS
    // ============================================
    fun getVaccinesByPet(petId: String): List<VaccineRecord> =
        _vaccineRecords.filter { it.petId == petId }.sortedBy { it.nextDueDate }

    fun getUpcomingVaccines(daysAhead: Int = 7): List<VaccineRecord> {
        val limitDate = LocalDate.now().plusDays(daysAhead.toLong())
        return _vaccineRecords.filter {
            it.nextDueDate.isBefore(limitDate) || it.nextDueDate.isEqual(limitDate)
        }.sortedBy { it.nextDueDate }
    }

    fun addVaccineRecord(vaccine: VaccineRecord) {
        _vaccineRecords.add(vaccine)
    }

    fun updateVaccineRecord(vaccine: VaccineRecord): Boolean {
        val index = _vaccineRecords.indexOfFirst { it.id == vaccine.id }
        if (index != -1) {
            _vaccineRecords[index] = vaccine
            return true
        }
        return false
    }

    // ============================================
    // FUNCIONES - ACTIVITY LOG
    // ============================================
    fun logActivity(event: ActivityEvent) {
        _activityEvents.add(event)
        _activityEventsFlow.value = _activityEvents.toList()
    }

    fun getActivitiesByUser(userId: String): List<ActivityEvent> =
        _activityEvents.filter { it.userId == userId }.sortedByDescending { it.timestamp }

    fun getActivitiesByScreen(screen: String): List<ActivityEvent> =
        _activityEvents.filter { it.screen == screen }.sortedByDescending { it.timestamp }

    fun clearActivityLog() {
        _activityEvents.clear()
        _activityEventsFlow.value = emptyList()
    }

    // ============================================
    // UTILIDADES
    // ============================================
    fun generateId(prefix: String): String = "$prefix-${System.currentTimeMillis()}"
}
