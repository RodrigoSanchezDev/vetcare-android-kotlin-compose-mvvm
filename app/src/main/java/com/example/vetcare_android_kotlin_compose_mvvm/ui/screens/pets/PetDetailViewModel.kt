package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

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
 * Usa VetCareRepository con Room Database para persistencia local
 */
class PetDetailViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    fun loadPet(petId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val pet = repository.getPetById(petId)

                if (pet != null) {
                    val owner = repository.getOwnerById(pet.ownerId)
                    val consultations = repository.getConsultationsByPet(petId)
                    val appointments = repository.getAppointmentsByPet(petId)
                        .filter { it.dateTime.isAfter(LocalDateTime.now()) }
                    val vaccines = repository.getVaccinesByPet(petId)

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
            } catch (e: Exception) {
                _uiState.value = PetDetailUiState(
                    isLoading = false,
                    error = "Error al cargar: ${e.message}"
                )
            }
        }
    }

    fun deletePet(): Boolean {
        val petId = _uiState.value.pet?.id ?: return false
        viewModelScope.launch {
            repository.deletePet(petId)
        }
        return true
    }
}

