package com.example.vetcare_android_kotlin_compose_mvvm.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Programador de recordatorios usando WorkManager
 */
object ReminderScheduler {

    /**
     * Programar todos los recordatorios (llamar después del login)
     */
    fun scheduleAllReminders(context: Context) {
        scheduleAppointmentReminders(context)
        scheduleVaccineReminders(context)
    }

    /**
     * Programar verificación de citas cada 12 horas
     */
    fun scheduleAppointmentReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Verificar citas próximas cada 12 horas
        val periodicWork = PeriodicWorkRequestBuilder<AppointmentReminderWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.MINUTES) // Delay inicial para testing
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                AppointmentReminderWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    /**
     * Programar verificación de vacunas cada 24 horas
     */
    fun scheduleVaccineReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Verificar vacunas próximas cada 24 horas
        val periodicWork = PeriodicWorkRequestBuilder<VaccineReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(2, TimeUnit.MINUTES) // Delay inicial para testing
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                VaccineReminderWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    /**
     * Ejecutar verificación inmediata de recordatorios (para testing)
     */
    fun runImmediateCheck(context: Context) {
        // Citas
        val appointmentWork = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .build()

        // Vacunas
        val vaccineWork = OneTimeWorkRequestBuilder<VaccineReminderWorker>()
            .build()

        WorkManager.getInstance(context)
            .beginWith(appointmentWork)
            .then(vaccineWork)
            .enqueue()
    }

    /**
     * Cancelar todos los recordatorios
     */
    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(AppointmentReminderWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(VaccineReminderWorker.WORK_NAME)
    }
}

