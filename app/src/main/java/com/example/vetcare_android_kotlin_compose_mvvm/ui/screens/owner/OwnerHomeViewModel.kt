package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.owner

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.VaccineRecord
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

/**
 * ViewModel para el dashboard de Owner
 */
class OwnerHomeViewModel : ViewModel() {

    private val _myPets = MutableStateFlow<List<Pet>>(emptyList())
    val myPets: StateFlow<List<Pet>> = _myPets.asStateFlow()

    private val _upcomingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val upcomingAppointments: StateFlow<List<Appointment>> = _upcomingAppointments.asStateFlow()

    private val _upcomingVaccines = MutableStateFlow<List<VaccineRecord>>(emptyList())
    val upcomingVaccines: StateFlow<List<VaccineRecord>> = _upcomingVaccines.asStateFlow()

    private val _ownerName = MutableStateFlow("Usuario")
    val ownerName: StateFlow<String> = _ownerName.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val ownerId = SessionManager.getOwnerId()
        val currentUser = SessionManager.getCurrentUser()

        _ownerName.value = currentUser?.name ?: "Usuario"

        if (ownerId != null) {
            // Cargar mascotas del owner
            _myPets.value = MockDataRepository.getPetsByOwner(ownerId)

            // Cargar citas próximas de las mascotas del owner
            val petIds = _myPets.value.map { it.id }
            _upcomingAppointments.value = MockDataRepository.appointments
                .filter { appt ->
                    appt.petId in petIds &&
                    appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
                }
                .sortedBy { it.dateTime }
                .take(3)

            // Cargar vacunas próximas
            _upcomingVaccines.value = petIds.flatMap { petId ->
                MockDataRepository.getVaccinesByPet(petId)
            }.filter { vaccine ->
                vaccine.nextDueDate.isBefore(LocalDate.now().plusDays(30)) ||
                vaccine.nextDueDate.isEqual(LocalDate.now().plusDays(30))
            }.sortedBy { it.nextDueDate }
            .take(3)
        }
    }

    fun refresh() {
        loadData()
    }
}

