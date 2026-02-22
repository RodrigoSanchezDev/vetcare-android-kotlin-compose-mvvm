package com.example.vetcare_android_kotlin_compose_mvvm

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Pruebas unitarias para validación de modelos de dominio
 *
 * Verifica reglas de negocio, invariantes de datos y construcción
 * correcta de los modelos del sistema VetCare.
 *
 * Aspectos testeados:
 * - Creación de modelos con valores válidos e inválidos
 * - Validación de reglas de negocio (edades, estados, fechas)
 * - Integridad referencial entre entidades
 * - Comportamiento de enums y display names
 * - Transformaciones y filtrado de datos
 *
 * Principio testeado: Correctitud del modelo de dominio
 *
 * @author Rodrigo Sánchez
 */
class ModelValidationTest {

    // ════════════════════════════════════════════════════════════════
    // TESTS DE PetSpecies
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `PetSpecies has correct display names in Spanish`() {
        assertEquals("Perro", PetSpecies.DOG.displayName)
        assertEquals("Gato", PetSpecies.CAT.displayName)
        assertEquals("Ave", PetSpecies.BIRD.displayName)
        assertEquals("Conejo", PetSpecies.RABBIT.displayName)
        assertEquals("Hámster", PetSpecies.HAMSTER.displayName)
        assertEquals("Otro", PetSpecies.OTHER.displayName)
    }

    @Test
    fun `PetSpecies contains exactly 6 species`() {
        assertEquals(6, PetSpecies.entries.size)
    }

    @Test
    fun `PetSpecies valueOf resolves correctly`() {
        assertEquals(PetSpecies.DOG, PetSpecies.valueOf("DOG"))
        assertEquals(PetSpecies.CAT, PetSpecies.valueOf("CAT"))
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE AppointmentStatus
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `AppointmentStatus has correct display names in Spanish`() {
        assertEquals("Programada", AppointmentStatus.SCHEDULED.displayName)
        assertEquals("Confirmada", AppointmentStatus.CONFIRMED.displayName)
        assertEquals("En progreso", AppointmentStatus.IN_PROGRESS.displayName)
        assertEquals("Completada", AppointmentStatus.COMPLETED.displayName)
        assertEquals("Cancelada", AppointmentStatus.CANCELLED.displayName)
        assertEquals("No asistió", AppointmentStatus.NO_SHOW.displayName)
    }

    @Test
    fun `AppointmentStatus contains exactly 6 statuses`() {
        assertEquals(6, AppointmentStatus.entries.size)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE UserRole
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `UserRole contains ADMIN and OWNER`() {
        val roles = UserRole.entries
        assertEquals(2, roles.size)
        assertTrue(roles.contains(UserRole.ADMIN))
        assertTrue(roles.contains(UserRole.OWNER))
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO Pet
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Pet creation with all fields`() {
        val pet = Pet(
            id = "pet-001",
            ownerId = "owner-001",
            name = "Luna",
            species = PetSpecies.CAT,
            breed = "Siamés",
            ageYears = 3,
            weightKg = 4.5,
            photoRes = 123,
            notes = "Muy cariñosa"
        )

        assertEquals("pet-001", pet.id)
        assertEquals("Luna", pet.name)
        assertEquals(PetSpecies.CAT, pet.species)
        assertEquals(3, pet.ageYears)
    }

    @Test
    fun `Pet creation with minimal required fields`() {
        val pet = Pet(
            id = "pet-002",
            ownerId = "owner-001",
            name = "Rocky",
            species = PetSpecies.DOG,
            ageYears = 5
        )

        assertNull(pet.breed)
        assertNull(pet.weightKg)
        assertNull(pet.photoRes)
        assertNull(pet.notes)
    }

    @Test
    fun `Pet data class equality works correctly`() {
        val pet1 = Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null)
        val pet2 = Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null)
        val pet3 = Pet("p2", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null)

        assertEquals(pet1, pet2)
        assertNotEquals(pet1, pet3)
    }

    @Test
    fun `Pet copy preserves unchanged fields`() {
        val original = Pet("p1", "o1", "Luna", PetSpecies.CAT, "Siamés", 3, 4.5, null, null)
        val updated = original.copy(ageYears = 4, weightKg = 5.0)

        assertEquals("Luna", updated.name)
        assertEquals(PetSpecies.CAT, updated.species)
        assertEquals("Siamés", updated.breed)
        assertEquals(4, updated.ageYears)
        assertEquals(5.0, updated.weightKg!!, 0.01)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO User
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Admin user has no ownerId`() {
        val admin = User(
            id = "user-001",
            name = "Admin",
            email = "admin@vet.cl",
            passwordHash = "hash",
            role = UserRole.ADMIN,
            ownerId = null
        )

        assertEquals(UserRole.ADMIN, admin.role)
        assertNull(admin.ownerId)
    }

    @Test
    fun `Owner user has ownerId`() {
        val owner = User(
            id = "user-002",
            name = "Owner",
            email = "owner@vet.cl",
            passwordHash = "hash",
            role = UserRole.OWNER,
            ownerId = "owner-001"
        )

        assertEquals(UserRole.OWNER, owner.role)
        assertNotNull(owner.ownerId)
        assertEquals("owner-001", owner.ownerId)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO Appointment
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Appointment default status is SCHEDULED`() {
        val appointment = Appointment(
            id = "apt-001",
            petId = "pet-001",
            vetId = "vet-001",
            dateTime = LocalDateTime.now(),
            reason = "Control"
        )

        assertEquals(AppointmentStatus.SCHEDULED, appointment.status)
    }

    @Test
    fun `Appointment stores datetime with precision`() {
        val specificTime = LocalDateTime.of(2026, 6, 15, 14, 30, 0)
        val appointment = Appointment(
            id = "apt-002",
            petId = "pet-001",
            vetId = "vet-001",
            dateTime = specificTime,
            reason = "Vacunación"
        )

        assertEquals(2026, appointment.dateTime.year)
        assertEquals(6, appointment.dateTime.monthValue)
        assertEquals(15, appointment.dateTime.dayOfMonth)
        assertEquals(14, appointment.dateTime.hour)
        assertEquals(30, appointment.dateTime.minute)
    }

    @Test
    fun `Appointment notes are optional`() {
        val withNotes = Appointment(
            "apt-1", "p1", "v1", LocalDateTime.now(), "Test",
            notes = "Nota importante"
        )
        val withoutNotes = Appointment(
            "apt-2", "p1", "v1", LocalDateTime.now(), "Test"
        )

        assertEquals("Nota importante", withNotes.notes)
        assertNull(withoutNotes.notes)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO VaccineRecord
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `VaccineRecord nextDueDate is after lastDate`() {
        val vaccine = VaccineRecord(
            id = "vac-001",
            petId = "pet-001",
            vaccineName = "Antirrábica",
            lastDate = LocalDate.of(2026, 1, 15),
            nextDueDate = LocalDate.of(2027, 1, 15)
        )

        assertTrue(vaccine.nextDueDate.isAfter(vaccine.lastDate))
    }

    @Test
    fun `VaccineRecord stores vaccine name correctly`() {
        val vaccine = VaccineRecord(
            id = "vac-002",
            petId = "pet-001",
            vaccineName = "Triple Felina",
            lastDate = LocalDate.now(),
            nextDueDate = LocalDate.now().plusYears(1)
        )

        assertEquals("Triple Felina", vaccine.vaccineName)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO Owner
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Owner optional fields default to null`() {
        val owner = Owner(
            id = "owner-001",
            fullName = "Carlos Méndez",
            email = "carlos@email.com"
        )

        assertNull(owner.phone)
        assertNull(owner.address)
        assertNull(owner.avatarRes)
    }

    @Test
    fun `Owner with complete information`() {
        val owner = Owner(
            id = "owner-002",
            fullName = "Ana López",
            email = "ana@email.com",
            phone = "+56912345678",
            address = "Av. Providencia 1234",
            avatarRes = 456
        )

        assertEquals("+56912345678", owner.phone)
        assertEquals("Av. Providencia 1234", owner.address)
        assertEquals(456, owner.avatarRes)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE MODELO Veterinarian
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `Veterinarian with specialty`() {
        val vet = Veterinarian(
            id = "vet-001",
            name = "Dra. María Rodríguez",
            specialty = "Cirugía",
            phone = "+56998765432"
        )

        assertEquals("Cirugía", vet.specialty)
        assertEquals("+56998765432", vet.phone)
    }

    @Test
    fun `Veterinarian without specialty`() {
        val vet = Veterinarian(
            id = "vet-002",
            name = "Dr. Pedro González"
        )

        assertNull(vet.specialty)
        assertNull(vet.phone)
        assertNull(vet.avatarRes)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE LÓGICA DE FILTRADO Y TRANSFORMACIÓN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `filter pets by species returns correct results`() {
        val pets = listOf(
            Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null),
            Pet("p2", "o1", "Rocky", PetSpecies.DOG, null, 5, null, null, null),
            Pet("p3", "o1", "Michi", PetSpecies.CAT, null, 2, null, null, null),
            Pet("p4", "o2", "Max", PetSpecies.DOG, null, 4, null, null, null)
        )

        val cats = pets.filter { it.species == PetSpecies.CAT }
        val dogs = pets.filter { it.species == PetSpecies.DOG }

        assertEquals(2, cats.size)
        assertEquals(2, dogs.size)
        assertTrue(cats.all { it.species == PetSpecies.CAT })
    }

    @Test
    fun `filter pets by owner returns correct results`() {
        val pets = listOf(
            Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null),
            Pet("p2", "o1", "Rocky", PetSpecies.DOG, null, 5, null, null, null),
            Pet("p3", "o2", "Michi", PetSpecies.CAT, null, 2, null, null, null)
        )

        val owner1Pets = pets.filter { it.ownerId == "o1" }
        val owner2Pets = pets.filter { it.ownerId == "o2" }

        assertEquals(2, owner1Pets.size)
        assertEquals(1, owner2Pets.size)
    }

    @Test
    fun `sort pets by name ascending`() {
        val pets = listOf(
            Pet("p1", "o1", "Rocky", PetSpecies.DOG, null, 5, null, null, null),
            Pet("p2", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null),
            Pet("p3", "o1", "Max", PetSpecies.DOG, null, 4, null, null, null)
        )

        val sorted = pets.sortedBy { it.name.lowercase() }

        assertEquals("Luna", sorted[0].name)
        assertEquals("Max", sorted[1].name)
        assertEquals("Rocky", sorted[2].name)
    }

    @Test
    fun `sort pets by age descending`() {
        val pets = listOf(
            Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null),
            Pet("p2", "o1", "Rocky", PetSpecies.DOG, null, 5, null, null, null),
            Pet("p3", "o1", "Max", PetSpecies.DOG, null, 1, null, null, null)
        )

        val sorted = pets.sortedByDescending { it.ageYears }

        assertEquals(5, sorted[0].ageYears)
        assertEquals(3, sorted[1].ageYears)
        assertEquals(1, sorted[2].ageYears)
    }

    @Test
    fun `filter appointments by status`() {
        val now = LocalDateTime.now()
        val appointments = listOf(
            Appointment("a1", "p1", "v1", now, "Control", AppointmentStatus.SCHEDULED),
            Appointment("a2", "p1", "v1", now, "Vacuna", AppointmentStatus.COMPLETED),
            Appointment("a3", "p2", "v1", now, "Revisión", AppointmentStatus.CANCELLED),
            Appointment("a4", "p2", "v1", now, "Urgencia", AppointmentStatus.SCHEDULED)
        )

        val scheduled = appointments.filter { it.status == AppointmentStatus.SCHEDULED }
        val completed = appointments.filter { it.status == AppointmentStatus.COMPLETED }
        val cancelled = appointments.filter { it.status == AppointmentStatus.CANCELLED }

        assertEquals(2, scheduled.size)
        assertEquals(1, completed.size)
        assertEquals(1, cancelled.size)
    }

    @Test
    fun `search pets by name is case insensitive`() {
        val pets = listOf(
            Pet("p1", "o1", "Luna", PetSpecies.CAT, null, 3, null, null, null),
            Pet("p2", "o1", "Rocky", PetSpecies.DOG, null, 5, null, null, null),
            Pet("p3", "o1", "LUNA BELLA", PetSpecies.CAT, null, 2, null, null, null)
        )

        val query = "luna"
        val results = pets.filter { it.name.contains(query, ignoreCase = true) }

        assertEquals(2, results.size)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE VALIDACIÓN DE FORMULARIO (simulada)
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `email validation accepts valid formats`() {
        val validEmails = listOf(
            "admin@vet.cl",
            "user@domain.com",
            "test.user@company.org"
        )

        validEmails.forEach { email ->
            assertTrue("$email should be valid", email.contains("@") && email.contains("."))
        }
    }

    @Test
    fun `pet age validation - valid range is 0 to 30`() {
        val validAges = listOf(0, 1, 5, 15, 30)
        val invalidAges = listOf(-1, 31, 100)

        validAges.forEach { age ->
            assertTrue("Age $age should be valid", age in 0..30)
        }

        invalidAges.forEach { age ->
            assertFalse("Age $age should be invalid", age in 0..30)
        }
    }

    @Test
    fun `pet name validation - blank names are invalid`() {
        val invalidNames = listOf("", " ", "   ")
        val validNames = listOf("Luna", "Rocky Jr.", "Max 2")

        invalidNames.forEach { name ->
            assertTrue("'$name' should be blank", name.isBlank())
        }

        validNames.forEach { name ->
            assertFalse("'$name' should not be blank", name.isBlank())
        }
    }

    @Test
    fun `password validation - minimum 6 characters`() {
        val shortPasswords = listOf("", "a", "12345")
        val validPasswords = listOf("123456", "password", "strongP@ss1")

        shortPasswords.forEach { pass ->
            assertTrue("'$pass' should be too short", pass.length < 6)
        }

        validPasswords.forEach { pass ->
            assertTrue("'$pass' should be valid length", pass.length >= 6)
        }
    }
}

