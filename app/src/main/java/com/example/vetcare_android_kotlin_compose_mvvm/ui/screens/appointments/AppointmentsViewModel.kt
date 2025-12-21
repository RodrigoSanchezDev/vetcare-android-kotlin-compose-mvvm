package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Filtro de estado para citas
 */
enum class AppointmentFilter(val displayName: String) {
    ALL("Todas"),
    UPCOMING("Próximas"),
    TODAY("Hoy"),
    COMPLETED("Completadas"),
    CANCELLED("Canceladas")
}

/**
 * Estado UI para lista de citas
 */
data class AppointmentsListUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: AppointmentFilter = AppointmentFilter.UPCOMING,
    val selectedDate: LocalDate? = null,
    val isAdmin: Boolean = false,
    val error: String? = null,
    val snackbarMessage: String? = null
)

/**
 * ViewModel para gestión de citas
 */
class AppointmentsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsListUiState())
    val uiState: StateFlow<AppointmentsListUiState> = _uiState.asStateFlow()

    private val _allAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _filter = MutableStateFlow(AppointmentFilter.UPCOMING)
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    private var searchJob: Job? = null

    init {
        // Log de navegación
        ActivityLogger.logNavigation(ActivityLogger.Screens.APPOINTMENTS_LIST)
        loadAppointments()

        viewModelScope.launch {
            combine(
                _allAppointments,
                _searchQuery,
                _filter,
                _selectedDate
            ) { appointments, query, filter, date ->
                filterAppointments(appointments, query, filter, date)
            }.collect { filtered ->
                _uiState.value = _uiState.value.copy(
                    appointments = filtered,
                    searchQuery = _searchQuery.value,
                    selectedFilter = _filter.value,
                    selectedDate = _selectedDate.value
                )
            }
        }
    }

    private fun loadAppointments() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val isAdmin = SessionManager.isAdmin()
        val ownerId = SessionManager.getOwnerId()

        val appointments = if (isAdmin) {
            MockDataRepository.appointments
        } else {
            // Owner solo ve citas de sus mascotas
            val petIds = ownerId?.let {
                MockDataRepository.getPetsByOwner(it).map { pet -> pet.id }
            } ?: emptyList()
            MockDataRepository.appointments.filter { it.petId in petIds }
        }

        _allAppointments.value = appointments
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isAdmin = isAdmin
        )
    }

    private fun filterAppointments(
        appointments: List<Appointment>,
        query: String,
        filter: AppointmentFilter,
        date: LocalDate?
    ): List<Appointment> {
        val today = LocalDate.now()

        return appointments.filter { appt ->
            val apptDate = appt.dateTime.toLocalDate()

            // Filtro por búsqueda (mascota y veterinario)
            val pet = MockDataRepository.getPetById(appt.petId)
            val vet = MockDataRepository.getVetById(appt.vetId)
            val matchesQuery = query.isBlank() ||
                pet?.name?.contains(query, ignoreCase = true) == true ||
                vet?.name?.contains(query, ignoreCase = true) == true ||
                appt.reason.contains(query, ignoreCase = true)

            // Filtro por fecha específica
            val matchesDate = if (date != null) {
                apptDate == date
            } else {
                // Filtro por categoría
                when (filter) {
                    AppointmentFilter.ALL -> true
                    AppointmentFilter.UPCOMING -> apptDate >= today &&
                        appt.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
                    AppointmentFilter.TODAY -> apptDate == today
                    AppointmentFilter.COMPLETED -> appt.status == AppointmentStatus.COMPLETED
                    AppointmentFilter.CANCELLED -> appt.status in listOf(
                        AppointmentStatus.CANCELLED,
                        AppointmentStatus.NO_SHOW
                    )
                }
            }

            matchesQuery && matchesDate
        }.sortedBy { it.dateTime }
    }

    fun updateFilter(filter: AppointmentFilter) {
        _filter.value = filter
        _selectedDate.value = null // Limpiar fecha al cambiar filtro
        ActivityLogger.log(
            screen = ActivityLogger.Screens.APPOINTMENTS_LIST,
            action = ActivityLogger.Actions.FILTER,
            metadata = mapOf("filterType" to "status", "value" to filter.displayName)
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Debounce y log de búsqueda
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            if (query.isNotBlank()) {
                ActivityLogger.log(
                    screen = ActivityLogger.Screens.APPOINTMENTS_LIST,
                    action = ActivityLogger.Actions.SEARCH,
                    metadata = mapOf("query" to query)
                )
            }
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filter.value = AppointmentFilter.UPCOMING
        _selectedDate.value = null
    }

    fun selectDate(date: LocalDate?) {
        _selectedDate.value = date
        if (date != null) {
            ActivityLogger.log(
                screen = ActivityLogger.Screens.APPOINTMENTS_LIST,
                action = ActivityLogger.Actions.FILTER,
                metadata = mapOf("filterType" to "date", "value" to date.toString())
            )
        }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        val appointment = MockDataRepository.getAppointmentById(appointmentId)
        if (appointment != null) {
            try {
                MockDataRepository.updateAppointment(appointment.copy(status = newStatus))
                ActivityLogger.logCrud(
                    screen = ActivityLogger.Screens.APPOINTMENTS_LIST,
                    action = ActivityLogger.Actions.UPDATE,
                    entityType = ActivityLogger.EntityTypes.APPOINTMENT,
                    entityId = appointmentId,
                    entityName = "Estado: ${newStatus.displayName}"
                )
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Cita actualizada a: ${newStatus.displayName}"
                )
                loadAppointments()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar la cita"
                )
            }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        try {
            MockDataRepository.cancelAppointment(appointmentId)
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.APPOINTMENTS_LIST,
                action = ActivityLogger.Actions.CANCEL,
                entityType = ActivityLogger.EntityTypes.APPOINTMENT,
                entityId = appointmentId
            )
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Cita cancelada"
            )
            loadAppointments()
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Error al cancelar la cita"
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        loadAppointments()
    }
}

