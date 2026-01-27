package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Cita con información de mascota incluida
 */
data class AppointmentWithPet(
    val appointment: Appointment,
    val pet: Pet?
)

/**
 * Datos de un veterinario con estadísticas
 */
data class VetWithStats(
    val vet: Veterinarian,
    val todayAppointments: Int,
    val totalAppointments: Int,
    val upcomingAppointments: List<AppointmentWithPet>
)

/**
 * Estado UI para lista de veterinarios
 */
data class VeterinariansUiState(
    val veterinarians: List<VetWithStats> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val snackbarMessage: String? = null,
    val error: String? = null
)

/**
 * ViewModel para lista de veterinarios con procesamiento asincrónico optimizado
 *
 * Implementación de Kotlin Coroutines avanzadas:
 * - Carga paralela de datos de múltiples veterinarios con async/await
 * - Carga paralela de mascotas para las citas
 * - Dispatchers.IO para acceso a Room Database
 * - Dispatchers.Default para procesamiento de listas
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

    /**
     * Carga veterinarios con estadísticas usando procesamiento paralelo
     *
     * Optimización:
     * - Todas las citas de cada veterinario se cargan en paralelo
     * - Las mascotas de las citas se cargan en paralelo
     * - El filtrado y ordenamiento se ejecuta en Dispatchers.Default
     */
    private fun loadVeterinarians() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val today = LocalDate.now()

                // Cargar veterinarios desde Room
                val veterinarians = withContext(Dispatchers.IO) {
                    repository.getAllVeterinarians()
                }

                // Cargar estadísticas para cada veterinario en PARALELO
                val vetsWithStats = withContext(Dispatchers.IO) {
                    veterinarians.map { vet ->
                        async {
                            val vetAppointments = repository.getAppointmentsByVet(vet.id)

                            // Calcular estadísticas en Default dispatcher
                            val (todayAppts, upcoming) = withContext(Dispatchers.Default) {
                                val todayCount = vetAppointments.count { it.dateTime.toLocalDate() == today }
                                val upcomingList = vetAppointments
                                    .filter { it.dateTime.toLocalDate() >= today }
                                    .sortedBy { it.dateTime }
                                    .take(3)
                                Pair(todayCount, upcomingList)
                            }

                            // Cargar mascotas de las citas próximas en paralelo
                            val upcomingWithPets = upcoming.map { appointment ->
                                async {
                                    val pet = repository.getPetById(appointment.petId)
                                    AppointmentWithPet(appointment, pet)
                                }
                            }.awaitAll()

                            VetWithStats(
                                vet = vet,
                                todayAppointments = todayAppts,
                                totalAppointments = vetAppointments.size,
                                upcomingAppointments = upcomingWithPets
                            )
                        }
                    }.awaitAll()
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
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        viewModelScope.launch {
            try {
                loadVeterinarians()
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun deleteVet(vetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val vet = repository.getVetById(vetId)
                val vetName = vet?.name ?: "Veterinario"

                val success = repository.deleteVeterinarian(vetId)

                withContext(Dispatchers.Main) {
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
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al eliminar: ${e.message}"
                    )
                }
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

