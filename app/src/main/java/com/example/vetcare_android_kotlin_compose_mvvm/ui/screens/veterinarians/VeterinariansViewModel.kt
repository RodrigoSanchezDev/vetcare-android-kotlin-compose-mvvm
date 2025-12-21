package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

/**
 * Datos de un veterinario con estadísticas
 */
data class VetWithStats(
    val vet: Veterinarian,
    val todayAppointments: Int,
    val totalAppointments: Int,
    val upcomingAppointments: List<Appointment>
)

/**
 * Estado UI para lista de veterinarios
 */
data class VeterinariansUiState(
    val veterinarians: List<VetWithStats> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val snackbarMessage: String? = null,
    val error: String? = null
)

/**
 * ViewModel para lista de veterinarios
 */
class VeterinariansViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VeterinariansUiState())
    val uiState: StateFlow<VeterinariansUiState> = _uiState.asStateFlow()

    init {
        ActivityLogger.logNavigation(ActivityLogger.Screens.VETS_LIST)
        loadVeterinarians()
    }

    private fun loadVeterinarians() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val today = LocalDate.now()
        val vetsWithStats = MockDataRepository.veterinarians.map { vet ->
            val vetAppointments = MockDataRepository.getAppointmentsByVet(vet.id)
            val todayAppts = vetAppointments.count { it.dateTime.toLocalDate() == today }
            val upcoming = vetAppointments
                .filter { it.dateTime.toLocalDate() >= today }
                .sortedBy { it.dateTime }
                .take(3)

            VetWithStats(
                vet = vet,
                todayAppointments = todayAppts,
                totalAppointments = vetAppointments.size,
                upcomingAppointments = upcoming
            )
        }

        _uiState.value = VeterinariansUiState(
            veterinarians = vetsWithStats,
            isLoading = false
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredVets(): List<VetWithStats> {
        val query = _uiState.value.searchQuery
        return if (query.isBlank()) {
            _uiState.value.veterinarians
        } else {
            _uiState.value.veterinarians.filter {
                it.vet.name.contains(query, ignoreCase = true) ||
                it.vet.specialty?.contains(query, ignoreCase = true) == true
            }
        }
    }

    fun refresh() {
        loadVeterinarians()
    }

    fun deleteVet(vetId: String) {
        val vet = MockDataRepository.getVetById(vetId)
        val vetName = vet?.name ?: "Veterinario"

        try {
            MockDataRepository.deleteVeterinarian(vetId)
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.VETS_LIST,
                action = ActivityLogger.Actions.DELETE,
                entityType = ActivityLogger.EntityTypes.VETERINARIAN,
                entityId = vetId,
                entityName = vetName
            )
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "$vetName eliminado correctamente"
            )
            loadVeterinarians()
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Error al eliminar $vetName"
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

