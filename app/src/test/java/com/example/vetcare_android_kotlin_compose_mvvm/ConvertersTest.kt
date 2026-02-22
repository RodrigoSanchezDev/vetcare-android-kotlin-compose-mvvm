package com.example.vetcare_android_kotlin_compose_mvvm

import com.example.vetcare_android_kotlin_compose_mvvm.data.local.Converters
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Pruebas unitarias para los TypeConverters de Room
 *
 * Verifica la correcta serialización/deserialización de tipos complejos
 * hacia tipos primitivos almacenables en SQLite.
 *
 * Tipos testeados:
 * - LocalDateTime ↔ String (ISO 8601)
 * - LocalDate ↔ String (ISO 8601)
 * - UserRole ↔ String
 * - PetSpecies ↔ String
 * - AppointmentStatus ↔ String
 *
 * Principio testeado: Integridad de datos en la capa de persistencia
 *
 * @author Rodrigo Sánchez
 */
class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS PARA LocalDateTime
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `fromLocalDateTime converts to ISO string`() {
        val dateTime = LocalDateTime.of(2026, 3, 15, 10, 30, 0)

        val result = converters.fromLocalDateTime(dateTime)

        assertEquals("2026-03-15T10:30:00", result)
    }

    @Test
    fun `toLocalDateTime parses ISO string correctly`() {
        val isoString = "2026-03-15T10:30:00"

        val result = converters.toLocalDateTime(isoString)

        assertNotNull(result)
        assertEquals(2026, result!!.year)
        assertEquals(3, result.monthValue)
        assertEquals(15, result.dayOfMonth)
        assertEquals(10, result.hour)
        assertEquals(30, result.minute)
    }

    @Test
    fun `LocalDateTime conversion is bidirectional`() {
        val original = LocalDateTime.of(2026, 12, 25, 23, 59, 59)

        val serialized = converters.fromLocalDateTime(original)
        val deserialized = converters.toLocalDateTime(serialized)

        assertEquals(original, deserialized)
    }

    @Test
    fun `fromLocalDateTime handles null`() {
        val result = converters.fromLocalDateTime(null)
        assertNull(result)
    }

    @Test
    fun `toLocalDateTime handles null`() {
        val result = converters.toLocalDateTime(null)
        assertNull(result)
    }

    @Test
    fun `LocalDateTime preserves midnight time`() {
        val midnight = LocalDateTime.of(2026, 1, 1, 0, 0, 0)

        val roundTrip = converters.toLocalDateTime(converters.fromLocalDateTime(midnight))

        assertEquals(midnight, roundTrip)
        assertEquals(0, roundTrip!!.hour)
        assertEquals(0, roundTrip.minute)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS PARA LocalDate
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `fromLocalDate converts to ISO date string`() {
        val date = LocalDate.of(2026, 6, 20)

        val result = converters.fromLocalDate(date)

        assertEquals("2026-06-20", result)
    }

    @Test
    fun `toLocalDate parses ISO date string correctly`() {
        val isoDate = "2026-06-20"

        val result = converters.toLocalDate(isoDate)

        assertNotNull(result)
        assertEquals(2026, result!!.year)
        assertEquals(6, result.monthValue)
        assertEquals(20, result.dayOfMonth)
    }

    @Test
    fun `LocalDate conversion is bidirectional`() {
        val original = LocalDate.of(2026, 2, 28)

        val serialized = converters.fromLocalDate(original)
        val deserialized = converters.toLocalDate(serialized)

        assertEquals(original, deserialized)
    }

    @Test
    fun `fromLocalDate handles null`() {
        assertNull(converters.fromLocalDate(null))
    }

    @Test
    fun `toLocalDate handles null`() {
        assertNull(converters.toLocalDate(null))
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS PARA UserRole ENUM
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `fromUserRole converts ADMIN to string`() {
        assertEquals("ADMIN", converters.fromUserRole(UserRole.ADMIN))
    }

    @Test
    fun `fromUserRole converts OWNER to string`() {
        assertEquals("OWNER", converters.fromUserRole(UserRole.OWNER))
    }

    @Test
    fun `toUserRole parses ADMIN string`() {
        assertEquals(UserRole.ADMIN, converters.toUserRole("ADMIN"))
    }

    @Test
    fun `toUserRole parses OWNER string`() {
        assertEquals(UserRole.OWNER, converters.toUserRole("OWNER"))
    }

    @Test
    fun `UserRole roundtrip preserves value`() {
        UserRole.entries.forEach { role ->
            val roundTrip = converters.toUserRole(converters.fromUserRole(role))
            assertEquals(role, roundTrip)
        }
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS PARA PetSpecies ENUM
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `PetSpecies conversion covers all species`() {
        PetSpecies.entries.forEach { species ->
            val serialized = converters.fromPetSpecies(species)
            val deserialized = converters.toPetSpecies(serialized)

            assertEquals(species, deserialized)
            assertEquals(species.name, serialized)
        }
    }

    @Test
    fun `fromPetSpecies returns correct string for DOG`() {
        assertEquals("DOG", converters.fromPetSpecies(PetSpecies.DOG))
    }

    @Test
    fun `toPetSpecies returns correct enum for CAT`() {
        assertEquals(PetSpecies.CAT, converters.toPetSpecies("CAT"))
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS PARA AppointmentStatus ENUM
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `AppointmentStatus conversion covers all statuses`() {
        AppointmentStatus.entries.forEach { status ->
            val serialized = converters.fromAppointmentStatus(status)
            val deserialized = converters.toAppointmentStatus(serialized)

            assertEquals(status, deserialized)
            assertEquals(status.name, serialized)
        }
    }

    @Test
    fun `fromAppointmentStatus returns SCHEDULED string`() {
        assertEquals("SCHEDULED", converters.fromAppointmentStatus(AppointmentStatus.SCHEDULED))
    }

    @Test
    fun `fromAppointmentStatus returns COMPLETED string`() {
        assertEquals("COMPLETED", converters.fromAppointmentStatus(AppointmentStatus.COMPLETED))
    }

    @Test
    fun `fromAppointmentStatus returns CANCELLED string`() {
        assertEquals("CANCELLED", converters.fromAppointmentStatus(AppointmentStatus.CANCELLED))
    }

    @Test
    fun `toAppointmentStatus parses IN_PROGRESS correctly`() {
        assertEquals(AppointmentStatus.IN_PROGRESS, converters.toAppointmentStatus("IN_PROGRESS"))
    }

    @Test
    fun `toAppointmentStatus parses NO_SHOW correctly`() {
        assertEquals(AppointmentStatus.NO_SHOW, converters.toAppointmentStatus("NO_SHOW"))
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE EDGE CASES
    // ════════════════════════════════════════════════════════════════

    @Test(expected = IllegalArgumentException::class)
    fun `toUserRole throws for invalid string`() {
        converters.toUserRole("INVALID_ROLE")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toPetSpecies throws for invalid string`() {
        converters.toPetSpecies("DRAGON")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toAppointmentStatus throws for invalid string`() {
        converters.toAppointmentStatus("UNKNOWN_STATUS")
    }
}

