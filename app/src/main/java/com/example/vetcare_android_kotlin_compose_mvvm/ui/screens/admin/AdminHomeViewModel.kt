package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
 * Estado UI del Dashboard Admin
 */
data class AdminHomeUiState(
    val stats: AdminStats = AdminStats(),
    val recentAppointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el Dashboard de Administrador con procesamiento asincrónico optimizado
 *
 * Implementación de Kotlin Coroutines avanzadas:
 * - Carga paralela de múltiples fuentes de datos con async/await
 * - Dispatchers.IO para todas las operaciones de Room Database
 * - Dispatchers.Default para cálculos y filtrado de estadísticas
 * - StateFlow para estado reactivo de la UI
 *
 * Optimizaciones de rendimiento:
 * 1. Todas las consultas a BD se ejecutan en paralelo (no secuencial)
 * 2. El cálculo de estadísticas se realiza en thread de CPU
 * 3. Pull-to-refresh sin bloquear la visualización actual
 * 4. Manejo de errores con recovery graceful
 */
class AdminHomeViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    // Mantener acceso directo a stats para compatibilidad
    val stats: StateFlow<AdminStats> get() = MutableStateFlow(_uiState.value.stats)
    val recentAppointments: StateFlow<List<Appointment>> get() = MutableStateFlow(_uiState.value.recentAppointments)

    init {
        loadDashboardData()
    }

    /**
     * Carga todos los datos del dashboard usando procesamiento paralelo
     *
     * Arquitectura async/await:
     * - Cada fuente de datos se carga en una coroutine independiente
     * - async {} inicia la coroutine inmediatamente sin bloquear
     * - await() suspende hasta obtener el resultado
     * - Total: 5 operaciones de BD ejecutándose simultáneamente
     *
     * Sin esta optimización: ~500ms (5 queries x 100ms cada una)
     * Con optimización paralela: ~100ms (todas en paralelo)
     */
    private fun loadDashboardData() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Ejecutar todas las consultas a Room en PARALELO
                val petsDeferred = async(Dispatchers.IO) { repository.getAllPets() }
                val ownersDeferred = async(Dispatchers.IO) { repository.getAllOwners() }
                val appointmentsDeferred = async(Dispatchers.IO) { repository.getAllAppointments() }
                val vetsDeferred = async(Dispatchers.IO) { repository.getAllVeterinarians() }
                val vaccinesDeferred = async(Dispatchers.IO) { repository.getUpcomingVaccines(7) }

                // Esperar resultados
                val pets = petsDeferred.await()
                val owners = ownersDeferred.await()
                val appointments = appointmentsDeferred.await()
                val vets = vetsDeferred.await()
                val upcomingVaccines = vaccinesDeferred.await()

                // Calcular estadísticas en Default dispatcher (CPU-bound)
                val stats = withContext(Dispatchers.Default) {
                    calculateStats(
                        totalPets = pets.size,
                        totalOwners = owners.size,
                        totalVets = vets.size,
                        appointments = appointments,
                        upcomingVaccinesCount = upcomingVaccines.size
                    )
                }

                // Filtrar citas recientes en Default dispatcher
                val recentAppointments = withContext(Dispatchers.Default) {
                    filterRecentAppointments(appointments)
                }

                _uiState.value = AdminHomeUiState(
                    stats = stats,
                    recentAppointments = recentAppointments,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar dashboard: ${e.message}"
                )
            }
        }
    }

    /**
     * Calcula las estadísticas del dashboard
     * Ejecutado en Dispatchers.Default para no bloquear IO
     */
    private fun calculateStats(
        totalPets: Int,
        totalOwners: Int,
        totalVets: Int,
        appointments: List<Appointment>,
        upcomingVaccinesCount: Int
    ): AdminStats {
        val today = LocalDate.now()

        val todayAppts = appointments.count { appt ->
            appt.dateTime.toLocalDate() == today &&
            appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }

        val pendingAppts = appointments.count { appt ->
            appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        }

        return AdminStats(
            totalPets = totalPets,
            totalOwners = totalOwners,
            todayAppointments = todayAppts,
            pendingAppointments = pendingAppts,
            upcomingVaccines = upcomingVaccinesCount,
            totalVets = totalVets
        )
    }

    /**
     * Filtra y ordena las citas más recientes/próximas
     */
    private fun filterRecentAppointments(appointments: List<Appointment>): List<Appointment> {
        val today = LocalDate.now()
        return appointments
            .filter { it.dateTime.toLocalDate() >= today }
            .sortedBy { it.dateTime }
            .take(5)
    }

    /**
     * Refresca los datos del dashboard
     * Muestra indicador de refresh sin ocultar datos actuales
     */
    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)

        viewModelScope.launch {
            try {
                val petsDeferred = async(Dispatchers.IO) { repository.getAllPets() }
                val ownersDeferred = async(Dispatchers.IO) { repository.getAllOwners() }
                val appointmentsDeferred = async(Dispatchers.IO) { repository.getAllAppointments() }
                val vetsDeferred = async(Dispatchers.IO) { repository.getAllVeterinarians() }
                val vaccinesDeferred = async(Dispatchers.IO) { repository.getUpcomingVaccines(7) }

                val appointments = appointmentsDeferred.await()

                val stats = withContext(Dispatchers.Default) {
                    calculateStats(
                        totalPets = petsDeferred.await().size,
                        totalOwners = ownersDeferred.await().size,
                        totalVets = vetsDeferred.await().size,
                        appointments = appointments,
                        upcomingVaccinesCount = vaccinesDeferred.await().size
                    )
                }

                val recentAppointments = withContext(Dispatchers.Default) {
                    filterRecentAppointments(appointments)
                }

                _uiState.value = AdminHomeUiState(
                    stats = stats,
                    recentAppointments = recentAppointments,
                    isLoading = false,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "Error al actualizar: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

