package com.example.vetcare_android_kotlin_compose_mvvm

import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.mapper.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Pruebas unitarias para los Entity Mappers
 *
 * Verifica la correcta transformación bidireccional entre
 * entidades Room (capa de datos) y modelos de dominio (capa de negocio).
 *
 * Principio testeado: Separación de capas en arquitectura MVVM
 *
 * @author Rodrigo Sánchez
 */
class EntityMappersTest {

    // ════════════════════════════════════════════════════════════════
    // USER MAPPER TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `UserEntity to User mapping preserves all fields`() {
        val entity = UserEntity(
            id = "user-001",
            name = "Admin VetCare",
            email = "admin@vet.cl",
            passwordHash = "hash123",
            role = UserRole.ADMIN,
            ownerId = null
        )

        val user = entity.toUser()

        assertEquals("user-001", user.id)
        assertEquals("Admin VetCare", user.name)
        assertEquals("admin@vet.cl", user.email)
        assertEquals("hash123", user.passwordHash)
        assertEquals(UserRole.ADMIN, user.role)
        assertNull(user.ownerId)
    }

    @Test
    fun `User to UserEntity mapping preserves all fields`() {
        val user = User(
            id = "user-002",
            name = "Juan Pérez",
            email = "owner@vet.cl",
            passwordHash = "pass456",
            role = UserRole.OWNER,
            ownerId = "owner-001"
        )

        val entity = user.toEntity()

        assertEquals("user-002", entity.id)
        assertEquals("Juan Pérez", entity.name)
        assertEquals("owner@vet.cl", entity.email)
        assertEquals("pass456", entity.passwordHash)
        assertEquals(UserRole.OWNER, entity.role)
        assertEquals("owner-001", entity.ownerId)
    }

    @Test
    fun `User mapping is bidirectional - roundtrip integrity`() {
        val originalUser = User(
            id = "user-003",
            name = "Test User",
            email = "test@vet.cl",
            passwordHash = "secret",
            role = UserRole.ADMIN,
            ownerId = null
        )

        val roundTrip = originalUser.toEntity().toUser()

        assertEquals(originalUser, roundTrip)
    }

    @Test
    fun `UserEntity list mapping converts all elements`() {
        val entities = listOf(
            UserEntity("u1", "User 1", "u1@vet.cl", "p1", UserRole.ADMIN, null),
            UserEntity("u2", "User 2", "u2@vet.cl", "p2", UserRole.OWNER, "o1"),
            UserEntity("u3", "User 3", "u3@vet.cl", "p3", UserRole.OWNER, "o2")
        )

        val users = entities.toUsers()

        assertEquals(3, users.size)
        assertEquals("u1", users[0].id)
        assertEquals("u2", users[1].id)
        assertEquals("u3", users[2].id)
        assertEquals(UserRole.ADMIN, users[0].role)
        assertEquals(UserRole.OWNER, users[1].role)
    }

    // ════════════════════════════════════════════════════════════════
    // PET MAPPER TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `PetEntity to Pet mapping preserves all fields including optionals`() {
        val entity = PetEntity(
            id = "pet-001",
            ownerId = "owner-001",
            name = "Luna",
            species = PetSpecies.CAT,
            breed = "Siamés",
            ageYears = 3,
            weightKg = 4.5,
            photoRes = 123,
            notes = "Vacunada"
        )

        val pet = entity.toPet()

        assertEquals("pet-001", pet.id)
        assertEquals("owner-001", pet.ownerId)
        assertEquals("Luna", pet.name)
        assertEquals(PetSpecies.CAT, pet.species)
        assertEquals("Siamés", pet.breed)
        assertEquals(3, pet.ageYears)
        assertEquals(4.5, pet.weightKg!!, 0.01)
        assertEquals(123, pet.photoRes)
        assertEquals("Vacunada", pet.notes)
    }

    @Test
    fun `Pet to PetEntity mapping handles null optional fields`() {
        val pet = Pet(
            id = "pet-002",
            ownerId = "owner-001",
            name = "Rocky",
            species = PetSpecies.DOG,
            breed = null,
            ageYears = 5,
            weightKg = null,
            photoRes = null,
            notes = null
        )

        val entity = pet.toEntity()

        assertEquals("pet-002", entity.id)
        assertNull(entity.breed)
        assertNull(entity.weightKg)
        assertNull(entity.photoRes)
        assertNull(entity.notes)
    }

    @Test
    fun `Pet mapping roundtrip preserves data integrity`() {
        val originalPet = Pet(
            id = "pet-003",
            ownerId = "owner-002",
            name = "Michi",
            species = PetSpecies.CAT,
            breed = "Persa",
            ageYears = 2,
            weightKg = 3.8,
            photoRes = null,
            notes = "Le gusta dormir"
        )

        val roundTrip = originalPet.toEntity().toPet()

        assertEquals(originalPet, roundTrip)
    }

    @Test
    fun `PetEntity list to Pet list maps all species correctly`() {
        val entities = listOf(
            PetEntity("p1", "o1", "Dog", PetSpecies.DOG, null, 1, null, null, null),
            PetEntity("p2", "o1", "Cat", PetSpecies.CAT, null, 2, null, null, null),
            PetEntity("p3", "o1", "Bird", PetSpecies.BIRD, null, 1, null, null, null),
            PetEntity("p4", "o1", "Rabbit", PetSpecies.RABBIT, null, 1, null, null, null),
            PetEntity("p5", "o1", "Hamster", PetSpecies.HAMSTER, null, 1, null, null, null),
            PetEntity("p6", "o1", "Other", PetSpecies.OTHER, null, 1, null, null, null)
        )

        val pets = entities.toPets()

        assertEquals(6, pets.size)
        assertEquals(PetSpecies.DOG, pets[0].species)
        assertEquals(PetSpecies.CAT, pets[1].species)
        assertEquals(PetSpecies.BIRD, pets[2].species)
        assertEquals(PetSpecies.RABBIT, pets[3].species)
        assertEquals(PetSpecies.HAMSTER, pets[4].species)
        assertEquals(PetSpecies.OTHER, pets[5].species)
    }

    // ════════════════════════════════════════════════════════════════
    // APPOINTMENT MAPPER TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `AppointmentEntity to Appointment mapping preserves datetime and status`() {
        val dateTime = LocalDateTime.of(2026, 3, 15, 10, 30)
        val entity = AppointmentEntity(
            id = "apt-001",
            petId = "pet-001",
            vetId = "vet-001",
            dateTime = dateTime,
            reason = "Control general",
            status = AppointmentStatus.SCHEDULED,
            notes = "Primera visita"
        )

        val appointment = entity.toAppointment()

        assertEquals("apt-001", appointment.id)
        assertEquals(dateTime, appointment.dateTime)
        assertEquals(AppointmentStatus.SCHEDULED, appointment.status)
        assertEquals("Control general", appointment.reason)
        assertEquals("Primera visita", appointment.notes)
    }

    @Test
    fun `Appointment mapping handles all status types`() {
        val statuses = AppointmentStatus.entries
        val dateTime = LocalDateTime.now()

        statuses.forEach { status ->
            val appointment = Appointment(
                id = "apt-${status.name}",
                petId = "pet-001",
                vetId = "vet-001",
                dateTime = dateTime,
                reason = "Test",
                status = status
            )

            val roundTrip = appointment.toEntity().toAppointment()

            assertEquals(status, roundTrip.status)
        }
    }

    // ════════════════════════════════════════════════════════════════
    // VACCINE RECORD MAPPER TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `VaccineRecordEntity to VaccineRecord mapping preserves dates`() {
        val lastDate = LocalDate.of(2026, 1, 15)
        val nextDate = LocalDate.of(2027, 1, 15)

        val entity = VaccineRecordEntity(
            id = "vac-001",
            petId = "pet-001",
            vaccineName = "Antirrábica",
            lastDate = lastDate,
            nextDueDate = nextDate,
            notes = "Aplicada correctamente"
        )

        val record = entity.toVaccineRecord()

        assertEquals("vac-001", record.id)
        assertEquals("Antirrábica", record.vaccineName)
        assertEquals(lastDate, record.lastDate)
        assertEquals(nextDate, record.nextDueDate)
        assertEquals("Aplicada correctamente", record.notes)
    }

    @Test
    fun `VaccineRecord roundtrip maintains date precision`() {
        val original = VaccineRecord(
            id = "vac-002",
            petId = "pet-002",
            vaccineName = "Triple Felina",
            lastDate = LocalDate.of(2025, 6, 20),
            nextDueDate = LocalDate.of(2026, 6, 20),
            notes = null
        )

        val roundTrip = original.toEntity().toVaccineRecord()

        assertEquals(original, roundTrip)
        assertEquals(original.lastDate, roundTrip.lastDate)
        assertEquals(original.nextDueDate, roundTrip.nextDueDate)
    }

    // ════════════════════════════════════════════════════════════════
    // OWNER MAPPER TESTS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Owner mapping preserves optional contact info`() {
        val owner = Owner(
            id = "owner-001",
            fullName = "Carlos Méndez",
            email = "carlos@email.com",
            phone = "+56912345678",
            address = "Av. Providencia 1234",
            avatarRes = null
        )

        val roundTrip = owner.toEntity().toOwner()

        assertEquals(owner, roundTrip)
        assertEquals("+56912345678", roundTrip.phone)
        assertEquals("Av. Providencia 1234", roundTrip.address)
    }

    @Test
    fun `Empty list mapping returns empty list`() {
        val emptyPets = emptyList<PetEntity>().toPets()
        val emptyUsers = emptyList<UserEntity>().toUsers()
        val emptyOwners = emptyList<OwnerEntity>().toOwners()

        assertTrue(emptyPets.isEmpty())
        assertTrue(emptyUsers.isEmpty())
        assertTrue(emptyOwners.isEmpty())
    }
}

