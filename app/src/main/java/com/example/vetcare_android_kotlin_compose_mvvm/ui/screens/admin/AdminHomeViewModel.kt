package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.admin

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

/**
 * Estadísticas del dashboard admin
 */
data class AdminStats(
    val totalPets: Int = 0,
    val totalOwners: Int = 0,
    val todayAppointments: Int = 0,
    val pendingAppointments: Int = 0,
    val upcomingVaccines: Int = 0,
    val totalVets: Int = 0
)

/**
 * ViewModel para el dashboard de Admin
 */
class AdminHomeViewModel : ViewModel() {

    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats.asStateFlow()

    private val _recentAppointments = MutableStateFlow(MockDataRepository.getUpcomingAppointments().take(5))
    val recentAppointments = _recentAppointments.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        val today = LocalDate.now()

        val todayAppts = MockDataRepository.appointments.count { appt ->
            appt.dateTime.toLocalDate() == today &&
            appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }

        val pendingAppts = MockDataRepository.appointments.count { appt ->
            appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }

        val upcomingVaccines = MockDataRepository.getUpcomingVaccines(7).size

        _stats.value = AdminStats(
            totalPets = MockDataRepository.pets.size,
            totalOwners = MockDataRepository.owners.size,
            todayAppointments = todayAppts,
            pendingAppointments = pendingAppts,
            upcomingVaccines = upcomingVaccines,
            totalVets = MockDataRepository.veterinarians.size
        )
    }

    fun refresh() {
        loadStats()
        _recentAppointments.value = MockDataRepository.getUpcomingAppointments().take(5)
    }
}

