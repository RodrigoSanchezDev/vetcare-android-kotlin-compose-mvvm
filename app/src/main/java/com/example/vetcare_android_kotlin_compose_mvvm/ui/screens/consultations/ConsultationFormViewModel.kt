package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Consultation
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Veterinarian
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Estado UI para formulario de consulta
 */
data class ConsultationFormUiState(
    val consultationId: String? = null,
    val isEditing: Boolean = false,
    val petId: String = "",
    val vetId: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val notes: String = "",

    // Errores
    val vetError: String? = null,
    val diagnosisError: String? = null,
    val treatmentError: String? = null,

    // Estado
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para crear/editar consulta médica
 * Usa VetCareRepository con Room Database para persistencia local
 */
class ConsultationFormViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(ConsultationFormUiState())
    val uiState: StateFlow<ConsultationFormUiState> = _uiState.asStateFlow()

    private val _veterinarians = MutableStateFlow<List<Veterinarian>>(emptyList())
    val veterinarians: List<Veterinarian> get() = _veterinarians.value

    init {
        loadVeterinarians()
    }

    private fun loadVeterinarians() {
        viewModelScope.launch {
            _veterinarians.value = repository.getAllVeterinarians()
        }
    }

    fun loadConsultation(consultationId: String?, petId: String?) {
        if (consultationId != null) {
            viewModelScope.launch {
                // Cargar consulta existente para editar
                val consultations = repository.getConsultationsByPet(petId ?: "")
                val consultation = consultations.find { it.id == consultationId }
                if (consultation != null) {
                    _uiState.value = ConsultationFormUiState(
                        consultationId = consultation.id,
                        isEditing = true,
                        petId = consultation.petId,
                        vetId = consultation.vetId,
                        diagnosis = consultation.diagnosis,
                        treatment = consultation.treatment,
                        notes = consultation.notes ?: ""
                    )
                }
            }
        } else if (petId != null) {
            // Nueva consulta para mascota específica
            _uiState.value = _uiState.value.copy(petId = petId)
        }
    }

    fun updateVet(vetId: String) {
        _uiState.value = _uiState.value.copy(vetId = vetId, vetError = null)
    }

    fun updateDiagnosis(value: String) {
        _uiState.value = _uiState.value.copy(diagnosis = value, diagnosisError = null)
    }

    fun updateTreatment(value: String) {
        _uiState.value = _uiState.value.copy(treatment = value, treatmentError = null)
    }

    fun updateNotes(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        if (state.vetId.isBlank()) {
            _uiState.value = state.copy(vetError = "Selecciona un veterinario")
            isValid = false
        }

        if (state.diagnosis.isBlank()) {
            _uiState.value = _uiState.value.copy(diagnosisError = "El diagnóstico es requerido")
            isValid = false
        }

        if (state.treatment.isBlank()) {
            _uiState.value = _uiState.value.copy(treatmentError = "El tratamiento es requerido")
            isValid = false
        }

        return isValid
    }

    fun save(): Boolean {
        if (!validate()) return false

        _uiState.value = _uiState.value.copy(isSaving = true)

        val state = _uiState.value
        val consultation = Consultation(
            id = state.consultationId ?: repository.generateId("cons"),
            petId = state.petId,
            vetId = state.vetId,
            dateTime = LocalDateTime.now(),
            diagnosis = state.diagnosis.trim(),
            treatment = state.treatment.trim(),
            notes = state.notes.trim().ifBlank { null }
        )

        viewModelScope.launch {
            try {
                if (state.isEditing) {
                    repository.updateConsultation(consultation)
                } else {
                    repository.insertConsultation(consultation)
                }

                // Log de creación/actualización
                val pet = repository.getPetById(state.petId)
                ActivityLogger.logCrud(
                    screen = ActivityLogger.Screens.CONSULTATION_FORM,
                    action = if (state.isEditing) ActivityLogger.Actions.UPDATE else ActivityLogger.Actions.CREATE,
                    entityType = ActivityLogger.EntityTypes.CONSULTATION,
                    entityId = consultation.id,
                    entityName = pet?.name
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
        _uiState.value = ConsultationFormUiState()
    }
}

