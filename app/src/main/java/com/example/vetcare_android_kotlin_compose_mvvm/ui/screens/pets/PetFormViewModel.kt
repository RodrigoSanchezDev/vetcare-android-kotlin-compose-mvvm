package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Owner
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
 * Usa VetCareRepository con Room Database para persistencia local
 */
class PetFormViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(PetFormUiState())
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    private val _owners = MutableStateFlow<List<Owner>>(emptyList())
    val owners: List<Owner> get() = _owners.value

    init {
        loadOwners()
    }

    private fun loadOwners() {
        viewModelScope.launch {
            _owners.value = repository.getAllOwners()
        }
    }

    fun loadPet(petId: String?) {
        if (petId != null) {
            viewModelScope.launch {
                val pet = repository.getPetById(petId)
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
            id = state.petId ?: repository.generateId("pet"),
            ownerId = state.ownerId,
            name = state.name.trim(),
            species = state.species,
            breed = state.breed.trim().ifBlank { null },
            ageYears = state.ageYears.toInt(),
            weightKg = state.weightKg.toDoubleOrNull(),
            notes = state.notes.trim().ifBlank { null }
        )

        viewModelScope.launch {
            try {
                if (state.isEditing) {
                    repository.updatePet(pet)
                } else {
                    repository.insertPet(pet)
                }

                // Log de creación/actualización
                ActivityLogger.logCrud(
                    screen = ActivityLogger.Screens.PET_FORM,
                    action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                    entityType = ActivityLogger.EntityTypes.PET,
                    entityId = pet.id,
                    entityName = pet.name
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = false,
                    error = "Error al guardar: ${e.message}"
                )
            }
        }

        return true
    }

    fun resetState() {
        _uiState.value = PetFormUiState()
    }
}

