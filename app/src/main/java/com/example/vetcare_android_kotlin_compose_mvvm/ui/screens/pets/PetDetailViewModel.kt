package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado UI para el detalle de mascota
 */
data class PetDetailUiState(
    val pet: Pet? = null,
    val owner: Owner? = null,
    val consultations: List<Consultation> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val vaccines: List<VaccineRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el detalle de mascota
 */
class PetDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    fun loadPet(petId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val pet = MockDataRepository.getPetById(petId)

        if (pet != null) {
            val owner = MockDataRepository.getOwnerById(pet.ownerId)
            val consultations = MockDataRepository.getConsultationsByPet(petId)
            val appointments = MockDataRepository.getAppointmentsByPet(petId)
                .filter { it.dateTime.isAfter(java.time.LocalDateTime.now()) }
            val vaccines = MockDataRepository.getVaccinesByPet(petId)

            _uiState.value = PetDetailUiState(
                pet = pet,
                owner = owner,
                consultations = consultations,
                appointments = appointments,
                vaccines = vaccines,
                isLoading = false
            )
        } else {
            _uiState.value = PetDetailUiState(
                isLoading = false,
                error = "Mascota no encontrada"
            )
        }
    }

    fun deletePet(): Boolean {
        val petId = _uiState.value.pet?.id ?: return false
        return MockDataRepository.deletePet(petId)
    }
}

