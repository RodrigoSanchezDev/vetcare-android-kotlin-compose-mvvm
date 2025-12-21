package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.VaccineRecord
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

/**
 * Estado UI para formulario de vacuna
 */
data class VaccineFormUiState(
    val vaccineId: String? = null,
    val isEditing: Boolean = false,
    val petId: String = "",
    val vaccineName: String = "",
    val lastDate: LocalDate = LocalDate.now(),
    val nextDueDate: LocalDate = LocalDate.now().plusMonths(12),
    val notes: String = "",

    // Errores
    val nameError: String? = null,

    // Estado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para crear/editar vacuna
 */
class VaccineFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VaccineFormUiState())
    val uiState: StateFlow<VaccineFormUiState> = _uiState.asStateFlow()

    // Vacunas comunes predefinidas
    val commonVaccines = listOf(
        "Antirrábica",
        "Séxtuple",
        "Triple Felina",
        "Parvovirus",
        "Moquillo",
        "Bordetella",
        "Leucemia Felina",
        "Otra"
    )

    fun loadVaccine(vaccineId: String?, petId: String?) {
        if (vaccineId != null) {
            val vaccine = MockDataRepository.vaccineRecords.find { it.id == vaccineId }
            if (vaccine != null) {
                _uiState.value = VaccineFormUiState(
                    vaccineId = vaccine.id,
                    isEditing = true,
                    petId = vaccine.petId,
                    vaccineName = vaccine.vaccineName,
                    lastDate = vaccine.lastDate,
                    nextDueDate = vaccine.nextDueDate,
                    notes = vaccine.notes ?: ""
                )
            }
        } else if (petId != null) {
            _uiState.value = _uiState.value.copy(petId = petId)
        }
    }

    fun updateVaccineName(name: String) {
        _uiState.value = _uiState.value.copy(vaccineName = name, nameError = null)
    }

    fun updateLastDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(lastDate = date)
        // Auto-calcular próxima fecha si está vacía o es anterior
        if (_uiState.value.nextDueDate.isBefore(date)) {
            _uiState.value = _uiState.value.copy(nextDueDate = date.plusMonths(12))
        }
    }

    fun updateNextDueDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(nextDueDate = date)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.vaccineName.isBlank()) {
            _uiState.value = state.copy(nameError = "El nombre de la vacuna es requerido")
            isValid = false
        }

        return isValid
    }

    fun save(): Boolean {
        if (!validate()) return false

        _uiState.value = _uiState.value.copy(isSaving = true)

        val state = _uiState.value
        val vaccine = VaccineRecord(
            id = state.vaccineId ?: MockDataRepository.generateId("vac"),
            petId = state.petId,
            vaccineName = state.vaccineName.trim(),
            lastDate = state.lastDate,
            nextDueDate = state.nextDueDate,
            notes = state.notes.trim().ifBlank { null }
        )

        val success = if (state.isEditing) {
            MockDataRepository.updateVaccineRecord(vaccine)
        } else {
            MockDataRepository.addVaccineRecord(vaccine)
            true
        }

        // Log de creación/actualización
        if (success) {
            val pet = MockDataRepository.getPetById(state.petId)
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.VACCINE_FORM,
                action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                entityType = ActivityLogger.EntityTypes.VACCINE,
                entityId = vaccine.id,
                entityName = "${vaccine.vaccineName} (${pet?.name ?: ""})"
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
        _uiState.value = VaccineFormUiState()
    }
}

