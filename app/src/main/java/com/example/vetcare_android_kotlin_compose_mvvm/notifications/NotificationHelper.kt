package com.example.vetcare_android_kotlin_compose_mvvm.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.vetcare_android_kotlin_compose_mvvm.R

/**
 * Gestor de notificaciones de VetCare
 */
object NotificationHelper {

    const val CHANNEL_APPOINTMENTS = "vetcare_appointments"
    const val CHANNEL_VACCINES = "vetcare_vaccines"

    private const val CHANNEL_APPOINTMENTS_NAME = "Citas Veterinarias"
    private const val CHANNEL_VACCINES_NAME = "Vacunas"

    /**
     * Crear canales de notificación (llamar en Application o MainActivity)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal de citas
            val appointmentsChannel = NotificationChannel(
                CHANNEL_APPOINTMENTS,
                CHANNEL_APPOINTMENTS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de citas veterinarias"
                enableVibration(true)
            }

            // Canal de vacunas
            val vaccinesChannel = NotificationChannel(
                CHANNEL_VACCINES,
                CHANNEL_VACCINES_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de vacunas próximas"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(appointmentsChannel)
            notificationManager.createNotificationChannel(vaccinesChannel)
        }
    }

    /**
     * Mostrar notificación de cita
     */
    fun showAppointmentNotification(
        context: Context,
        notificationId: Int,
        petName: String,
        vetName: String,
        dateTime: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_APPOINTMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cita próxima - $petName")
            .setContentText("Mañana a las $dateTime con $vetName")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Recuerda que $petName tiene cita mañana a las $dateTime con $vetName"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * Mostrar notificación de vacuna
     */
    fun showVaccineNotification(
        context: Context,
        notificationId: Int,
        petName: String,
        vaccineName: String,
        daysUntil: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val daysText = when {
            daysUntil <= 0 -> "¡Vencida!"
            daysUntil == 1 -> "mañana"
            else -> "en $daysUntil días"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_VACCINES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Vacuna próxima - $petName")
            .setContentText("$vaccineName vence $daysText")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("La vacuna $vaccineName de $petName vence $daysText. ¡Agenda una cita!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}

