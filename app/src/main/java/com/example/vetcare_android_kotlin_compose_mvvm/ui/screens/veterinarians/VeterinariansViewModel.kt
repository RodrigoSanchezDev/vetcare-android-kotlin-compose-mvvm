package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
 * Usa VetCareRepository con Room Database para persistencia local
 */
class VeterinariansViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(VeterinariansUiState())
    val uiState: StateFlow<VeterinariansUiState> = _uiState.asStateFlow()

    init {
        ActivityLogger.logNavigation(ActivityLogger.Screens.VETS_LIST)
        loadVeterinarians()
    }

    private fun loadVeterinarians() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val veterinarians = repository.getAllVeterinarians()

                val vetsWithStats = veterinarians.map { vet ->
                    val vetAppointments = repository.getAppointmentsByVet(vet.id)
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar veterinarios: ${e.message}"
                )
            }
        }
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
        viewModelScope.launch {
            try {
                val vet = repository.getVetById(vetId)
                val vetName = vet?.name ?: "Veterinario"

                val success = repository.deleteVeterinarian(vetId)
                if (success) {
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
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al eliminar $vetName"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.message}"
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

