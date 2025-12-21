package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado UI para el formulario de mascota
 */
data class PetFormUiState(
    val petId: String? = null,
    val isEditing: Boolean = false,
    val name: String = "",
    val species: PetSpecies = PetSpecies.DOG,
    val breed: String = "",
    val ageYears: String = "",
    val weightKg: String = "",
    val notes: String = "",
    val ownerId: String = "",

    // Errores
    val nameError: String? = null,
    val ageError: String? = null,
    val ownerError: String? = null,

    // Estado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para crear/editar mascota
 */
class PetFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PetFormUiState())
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    val owners = MockDataRepository.owners

    fun loadPet(petId: String?) {
        if (petId != null) {
            val pet = MockDataRepository.getPetById(petId)
            if (pet != null) {
                _uiState.value = PetFormUiState(
                    petId = pet.id,
                    isEditing = true,
                    name = pet.name,
                    species = pet.species,
                    breed = pet.breed ?: "",
                    ageYears = pet.ageYears.toString(),
                    weightKg = pet.weightKg?.toString() ?: "",
                    notes = pet.notes ?: "",
                    ownerId = pet.ownerId
                )
            }
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun updateSpecies(value: PetSpecies) {
        _uiState.value = _uiState.value.copy(species = value)
    }

    fun updateBreed(value: String) {
        _uiState.value = _uiState.value.copy(breed = value)
    }

    fun updateAge(value: String) {
        _uiState.value = _uiState.value.copy(ageYears = value, ageError = null)
    }

    fun updateWeight(value: String) {
        _uiState.value = _uiState.value.copy(weightKg = value)
    }

    fun updateNotes(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun updateOwner(ownerId: String) {
        _uiState.value = _uiState.value.copy(ownerId = ownerId, ownerError = null)
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(nameError = "El nombre es requerido")
            isValid = false
        }

        val age = state.ageYears.toIntOrNull()
        if (age == null || age < 0 || age > 30) {
            _uiState.value = _uiState.value.copy(ageError = "Ingresa una edad válida (0-30)")
            isValid = false
        }

        if (state.ownerId.isBlank()) {
            _uiState.value = _uiState.value.copy(ownerError = "Selecciona un dueño")
            isValid = false
        }

        return isValid
    }

    fun save(): Boolean {
        if (!validate()) return false

        _uiState.value = _uiState.value.copy(isSaving = true)

        val state = _uiState.value
        val pet = Pet(
            id = state.petId ?: MockDataRepository.generateId("pet"),
            ownerId = state.ownerId,
            name = state.name.trim(),
            species = state.species,
            breed = state.breed.trim().ifBlank { null },
            ageYears = state.ageYears.toInt(),
            weightKg = state.weightKg.toDoubleOrNull(),
            notes = state.notes.trim().ifBlank { null }
        )

        val success = if (state.isEditing) {
            MockDataRepository.updatePet(pet)
        } else {
            MockDataRepository.addPet(pet)
            true
        }

        // Log de creación/actualización
        if (success) {
            ActivityLogger.logCrud(
                screen = ActivityLogger.Screens.PET_FORM,
                action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                entityType = ActivityLogger.EntityTypes.PET,
                entityId = pet.id,
                entityName = pet.name
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
        _uiState.value = PetFormUiState()
    }
}

