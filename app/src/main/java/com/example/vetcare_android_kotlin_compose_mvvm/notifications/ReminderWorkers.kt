package com.example.vetcare_android_kotlin_compose_mvvm.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Worker para verificar y enviar recordatorios de citas
 *
 * Implementación con Kotlin Coroutines:
 * - Hereda de CoroutineWorker para operaciones asíncronas
 * - Usa Dispatchers.IO para acceso a Room Database
 * - Se ejecuta en background sin afectar la UI
 */
class AppointmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository: VetCareRepository = VetCareRepository.getInstance(context)

    override suspend fun doWork(): Result {
        return try {
            checkAndNotifyAppointments()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * Verifica citas próximas y envía notificaciones
     * Todas las operaciones de BD se ejecutan en IO dispatcher
     */
    private suspend fun checkAndNotifyAppointments() {
        withContext(Dispatchers.IO) {
            val now = LocalDateTime.now()
            val tomorrow = now.plusDays(1)

            // Buscar citas para mañana desde Room Database
            val allAppointments = repository.getAllAppointments()
            val upcomingAppointments = allAppointments.filter { appointment ->
                val apptDate = appointment.dateTime.toLocalDate()
                val tomorrowDate = tomorrow.toLocalDate()

                apptDate == tomorrowDate &&
                appointment.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
            }

            upcomingAppointments.forEachIndexed { index, appointment ->
                val pet = repository.getPetById(appointment.petId)
                val vet = repository.getVetById(appointment.vetId)

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
    }

    companion object {
        const val WORK_NAME = "appointment_reminder_work"
    }
}

/**
 * Worker para verificar y enviar recordatorios de vacunas
 *
 * Implementación con Kotlin Coroutines:
 * - CoroutineWorker permite suspend functions
 * - Acceso a Room Database en background thread
 * - Manejo de errores con Result.failure()
 */
class VaccineReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository: VetCareRepository = VetCareRepository.getInstance(context)

    override suspend fun doWork(): Result {
        return try {
            checkAndNotifyVaccines()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * Verifica vacunas próximas a vencer y envía notificaciones
     */
    private suspend fun checkAndNotifyVaccines() {
        withContext(Dispatchers.IO) {
            val today = LocalDate.now()

            // Obtener vacunas próximas (3 días) desde Room Database
            val upcomingVaccines = repository.getUpcomingVaccines(3)

            upcomingVaccines.forEachIndexed { index, vaccine ->
                val pet = repository.getPetById(vaccine.petId)

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
    }

    companion object {
        const val WORK_NAME = "vaccine_reminder_work"
    }
}

