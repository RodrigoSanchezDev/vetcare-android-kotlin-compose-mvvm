package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val isRefreshing: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el detalle de mascota con optimización de rendimiento
 *
 * Implementación de Kotlin Coroutines avanzadas:
 * - async/await para carga paralela de datos
 * - Dispatchers.IO para operaciones de base de datos
 * - Dispatchers.Default para procesamiento de listas
 * - viewModelScope para lifecycle-aware coroutines
 *
 * Esta arquitectura garantiza:
 * 1. UI fluida sin bloqueos durante cargas intensivas
 * 2. Carga paralela de datos independientes (owner, consultations, appointments, vaccines)
 * 3. Separación de hilos: IO para Room, Default para filtrado, Main para UI
 */
class PetDetailViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    /**
     * Carga los datos de la mascota utilizando procesamiento asincrónico paralelo
     *
     * Optimización implementada:
     * - Primero carga la mascota (requerida para obtener ownerId)
     * - Luego carga en PARALELO: owner, consultations, appointments, vaccines
     * - Usa async/await pattern para ejecutar múltiples coroutines concurrentemente
     * - El filtrado de citas se realiza en Dispatchers.Default para no bloquear IO
     *
     * Beneficio: Reduce el tiempo de carga de ~4x (secuencial) a ~1x (paralelo)
     */
    fun loadPet(petId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Operación en IO dispatcher para acceso a Room Database
                val pet = withContext(Dispatchers.IO) {
                    repository.getPetById(petId)
                }

                if (pet != null) {
                    // Carga PARALELA de datos relacionados usando async
                    // Cada async inicia una coroutine independiente
                    val ownerDeferred = async(Dispatchers.IO) {
                        repository.getOwnerById(pet.ownerId)
                    }
                    val consultationsDeferred = async(Dispatchers.IO) {
                        repository.getConsultationsByPet(petId)
                    }
                    val appointmentsDeferred = async(Dispatchers.IO) {
                        repository.getAppointmentsByPet(petId)
                    }
                    val vaccinesDeferred = async(Dispatchers.IO) {
                        repository.getVaccinesByPet(petId)
                    }

                    // await() suspende hasta que cada operación complete
                    val owner = ownerDeferred.await()
                    val allAppointments = appointmentsDeferred.await()
                    val consultations = consultationsDeferred.await()
                    val vaccines = vaccinesDeferred.await()

                    // Filtrado de citas en Default dispatcher (CPU-intensive)
                    val upcomingAppointments = withContext(Dispatchers.Default) {
                        val now = LocalDateTime.now()
                        allAppointments.filter { it.dateTime.isAfter(now) }
                            .sortedBy { it.dateTime }
                    }

                    _uiState.value = PetDetailUiState(
                        pet = pet,
                        owner = owner,
                        consultations = consultations,
                        appointments = upcomingAppointments,
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

    /**
     * Refresca los datos sin mostrar loading completo
     * Útil para pull-to-refresh manteniendo datos visibles
     */
    fun refresh(petId: String) {
        _uiState.value = _uiState.value.copy(isRefreshing = true)

        viewModelScope.launch {
            try {
                val pet = withContext(Dispatchers.IO) {
                    repository.getPetById(petId)
                }

                if (pet != null) {
                    val ownerDeferred = async(Dispatchers.IO) { repository.getOwnerById(pet.ownerId) }
                    val consultationsDeferred = async(Dispatchers.IO) { repository.getConsultationsByPet(petId) }
                    val appointmentsDeferred = async(Dispatchers.IO) { repository.getAppointmentsByPet(petId) }
                    val vaccinesDeferred = async(Dispatchers.IO) { repository.getVaccinesByPet(petId) }

                    val upcomingAppointments = withContext(Dispatchers.Default) {
                        val now = LocalDateTime.now()
                        appointmentsDeferred.await().filter { it.dateTime.isAfter(now) }.sortedBy { it.dateTime }
                    }

                    _uiState.value = _uiState.value.copy(
                        pet = pet,
                        owner = ownerDeferred.await(),
                        consultations = consultationsDeferred.await(),
                        appointments = upcomingAppointments,
                        vaccines = vaccinesDeferred.await(),
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "Error al actualizar: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina la mascota en background sin bloquear UI
     */
    fun deletePet(): Boolean {
        val petId = _uiState.value.pet?.id ?: return false
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePet(petId)
        }
        return true
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

