package com.example.vetcare_android_kotlin_compose_mvvm.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Worker para verificar y enviar recordatorios de citas
 */
class AppointmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            checkAndNotifyAppointments()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun checkAndNotifyAppointments() {
        val now = LocalDateTime.now()
        val tomorrow = now.plusDays(1)

        // Buscar citas para mañana
        val upcomingAppointments = MockDataRepository.appointments.filter { appointment ->
            val apptDate = appointment.dateTime.toLocalDate()
            val tomorrowDate = tomorrow.toLocalDate()

            apptDate == tomorrowDate &&
            appointment.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }

        upcomingAppointments.forEachIndexed { index, appointment ->
            val pet = MockDataRepository.getPetById(appointment.petId)
            val vet = MockDataRepository.getVetById(appointment.vetId)

            if (pet != null && vet != null) {
                val timeStr = appointment.dateTime.toLocalTime().toString()
                NotificationHelper.showAppointmentNotification(
                    context = applicationContext,
                    notificationId = 1000 + index,
                    petName = pet.name,
                    vetName = vet.name,
                    dateTime = timeStr
                )
            }
        }
    }

    companion object {
        const val WORK_NAME = "appointment_reminder_work"
    }
}

/**
 * Worker para verificar y enviar recordatorios de vacunas
 */
class VaccineReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            checkAndNotifyVaccines()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun checkAndNotifyVaccines() {
        val today = LocalDate.now()
        val limitDate = today.plusDays(3)

        // Buscar vacunas próximas a vencer (3 días o menos)
        val upcomingVaccines = MockDataRepository.vaccineRecords.filter { vaccine ->
            vaccine.nextDueDate.isBefore(limitDate) || vaccine.nextDueDate.isEqual(limitDate)
        }

        upcomingVaccines.forEachIndexed { index, vaccine ->
            val pet = MockDataRepository.getPetById(vaccine.petId)

            if (pet != null) {
                val daysUntil = ChronoUnit.DAYS.between(today, vaccine.nextDueDate).toInt()
                NotificationHelper.showVaccineNotification(
                    context = applicationContext,
                    notificationId = 2000 + index,
                    petName = pet.name,
                    vaccineName = vaccine.vaccineName,
                    daysUntil = daysUntil
                )
            }
        }
    }

    companion object {
        const val WORK_NAME = "vaccine_reminder_work"
    }
}

