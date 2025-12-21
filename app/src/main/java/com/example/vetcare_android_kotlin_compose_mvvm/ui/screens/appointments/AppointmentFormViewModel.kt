package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Estado UI para formulario de cita
 */
data class AppointmentFormUiState(
    val appointmentId: String? = null,
    val isEditing: Boolean = false,
    val petId: String = "",
    val vetId: String = "",
    val date: LocalDate = LocalDate.now().plusDays(1),
    val time: LocalTime = LocalTime.of(10, 0),
    val reason: String = "",
    val notes: String = "",

    // Errores
    val petError: String? = null,
    val vetError: String? = null,
    val reasonError: String? = null,

    // Estado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para crear/editar cita
 */
class AppointmentFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentFormUiState())
    val uiState: StateFlow<AppointmentFormUiState> = _uiState.asStateFlow()

    val pets: List<Pet> = MockDataRepository.pets
    val veterinarians: List<Veterinarian> = MockDataRepository.veterinarians

    fun loadAppointment(appointmentId: String?) {
        if (appointmentId != null) {
            val appointment = MockDataRepository.getAppointmentById(appointmentId)
            if (appointment != null) {
                _uiState.value = AppointmentFormUiState(
                    appointmentId = appointment.id,
                    isEditing = true,
                    petId = appointment.petId,
                    vetId = appointment.vetId,
                    date = appointment.dateTime.toLocalDate(),
                    time = appointment.dateTime.toLocalTime(),
                    reason = appointment.reason,
                    notes = appointment.notes ?: ""
                )
            }
        }
    }

    fun loadForPet(petId: String) {
        _uiState.value = _uiState.value.copy(petId = petId)
    }

    fun updatePet(petId: String) {
        _uiState.value = _uiState.value.copy(petId = petId, petError = null)
    }

    fun updateVet(vetId: String) {
        _uiState.value = _uiState.value.copy(vetId = vetId, vetError = null)
    }

    fun updateDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun updateTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(time = time)
    }

    fun updateReason(reason: String) {
        _uiState.value = _uiState.value.copy(reason = reason, reasonError = null)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.petId.isBlank()) {
            _uiState.value = state.copy(petError = "Selecciona una mascota")
            isValid = false
        }

        if (state.vetId.isBlank()) {
            _uiState.value = _uiState.value.copy(vetError = "Selecciona un veterinario")
            isValid = false
        }

        if (state.reason.isBlank()) {
            _uiState.value = _uiState.value.copy(reasonError = "El motivo es requerido")
            isValid = false
        }

        return isValid
    }

    fun save(): Boolean {
        if (!validate()) return false

        _uiState.value = _uiState.value.copy(isSaving = true)

        val state = _uiState.value
        val appointment = Appointment(
            id = state.appointmentId ?: MockDataRepository.generateId("apt"),
            petId = state.petId,
            vetId = state.vetId,
            dateTime = LocalDateTime.of(state.date, state.time),
            reason = state.reason.trim(),
            status = AppointmentStatus.SCHEDULED,
            notes = state.notes.trim().ifBlank { null }
        )

        val success = if (state.isEditing) {
            MockDataRepository.updateAppointment(appointment)
        } else {
            MockDataRepository.addAppointment(appointment)
            true
        }

        // Log de creación/actualización
        if (success) {
            val pet = MockDataRepository.getPetById(state.petId)
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.APPOINTMENT_FORM,
                action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                entityType = ActivityLogger.EntityTypes.APPOINTMENT,
                entityId = appointment.id,
                entityName = pet?.name
            )
        }

        _uiState.value = _uiState.value.copy(
            isSaving = false,
            saveSuccess = success,
            error = if (!success) "Error al guardar" else null
        )

        return success
    }

    fun resetState() {
        _uiState.value = AppointmentFormUiState()
    }
}

