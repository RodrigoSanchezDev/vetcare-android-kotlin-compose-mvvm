package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado UI para formulario de veterinario
 */
data class VetFormUiState(
    val vetId: String? = null,
    val isEditing: Boolean = false,
    val name: String = "",
    val specialty: String = "",
    val phone: String = "",

    // Errores
    val nameError: String? = null,
    val specialtyError: String? = null,

    // Estado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para crear/editar veterinario
 */
class VetFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VetFormUiState())
    val uiState: StateFlow<VetFormUiState> = _uiState.asStateFlow()

    // Especialidades predefinidas
    val specialties = listOf(
        "Medicina General",
        "Cirugía",
        "Dermatología",
        "Cardiología",
        "Oftalmología",
        "Neurología",
        "Oncología",
        "Odontología",
        "Traumatología",
        "Nutrición"
    )

    fun loadVet(vetId: String?) {
        if (vetId != null) {
            val vet = MockDataRepository.getVetById(vetId)
            if (vet != null) {
                _uiState.value = VetFormUiState(
                    vetId = vet.id,
                    isEditing = true,
                    name = vet.name,
                    specialty = vet.specialty ?: "",
                    phone = vet.phone ?: ""
                )
            }
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun updateSpecialty(value: String) {
        _uiState.value = _uiState.value.copy(specialty = value, specialtyError = null)
    }

    fun updatePhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }


    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(nameError = "El nombre es requerido")
            isValid = false
        }

        if (state.specialty.isBlank()) {
            _uiState.value = _uiState.value.copy(specialtyError = "La especialidad es requerida")
            isValid = false
        }

        return isValid
    }

    fun save(): Boolean {
        if (!validate()) return false

        _uiState.value = _uiState.value.copy(isSaving = true)

        val state = _uiState.value
        val vet = Veterinarian(
            id = state.vetId ?: MockDataRepository.generateId("vet"),
            name = state.name.trim(),
            specialty = state.specialty.trim().ifBlank { null },
            phone = state.phone.trim().ifBlank { null }
        )

        val success = if (state.isEditing) {
            MockDataRepository.updateVeterinarian(vet)
        } else {
            MockDataRepository.addVeterinarian(vet)
            true
        }

        // Log de creación/actualización
        if (success) {
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.VET_FORM,
                action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                entityType = ActivityLogger.EntityTypes.VETERINARIAN,
                entityId = vet.id,
                entityName = vet.name
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
        _uiState.value = VetFormUiState()
    }
}

