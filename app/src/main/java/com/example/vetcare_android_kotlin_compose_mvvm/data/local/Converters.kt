package com.example.vetcare_android_kotlin_compose_mvvm.data.local

import androidx.room.TypeConverter
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * TypeConverters para Room Database
 * Convierte tipos complejos a tipos primitivos que SQLite puede almacenar
 */
class Converters {

    // ============================================
    // CONVERTERS PARA LocalDateTime
    // ============================================

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    // ============================================
    // CONVERTERS PARA LocalDate
    // ============================================

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    // ============================================
    // CONVERTERS PARA ENUMS
    // ============================================

    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }

    @TypeConverter
    fun fromPetSpecies(species: PetSpecies): String {
        return species.name
    }

    @TypeConverter
    fun toPetSpecies(value: String): PetSpecies {
        return PetSpecies.valueOf(value)
    }

    @TypeConverter
    fun fromAppointmentStatus(status: AppointmentStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAppointmentStatus(value: String): AppointmentStatus {
        return AppointmentStatus.valueOf(value)
    }
}
