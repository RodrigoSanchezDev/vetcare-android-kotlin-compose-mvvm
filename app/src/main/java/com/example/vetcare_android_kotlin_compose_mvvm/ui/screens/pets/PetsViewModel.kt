package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Opciones de ordenamiento para la lista de mascotas
 */
enum class PetSortOption(val displayName: String) {
    NAME_ASC("Nombre A-Z"),
    NAME_DESC("Nombre Z-A"),
    AGE_ASC("Edad ↑"),
    AGE_DESC("Edad ↓"),
    SPECIES("Especie")
}

/**
 * Estado UI para la lista de mascotas
 */
data class PetsListUiState(
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedSpeciesFilter: PetSpecies? = null,
    val selectedOwnerFilter: String? = null, // Solo para admin
    val sortOption: PetSortOption = PetSortOption.NAME_ASC,
    val isAdmin: Boolean = false,
    val availableOwners: List<Pair<String, String>> = emptyList(), // ownerId to ownerName
    val error: String? = null,
    val snackbarMessage: String? = null
)

/**
 * ViewModel para la gestión de mascotas
 * Usa VetCareRepository con Room Database para persistencia local
 */
class PetsViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(PetsListUiState())
    val uiState: StateFlow<PetsListUiState> = _uiState.asStateFlow()

    private val _allPets = MutableStateFlow<List<Pet>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _speciesFilter = MutableStateFlow<PetSpecies?>(null)
    private val _ownerFilter = MutableStateFlow<String?>(null)
    private val _sortOption = MutableStateFlow(PetSortOption.NAME_ASC)
    private var searchJob: Job? = null

    init {
        // Log de navegación a la pantalla
        ActivityLogger.logNavigation(ActivityLogger.Screens.PETS_LIST)
        loadPets()

        // Combinar filtros y ordenamiento
        viewModelScope.launch {
            combine(
                _allPets,
                _searchQuery,
                _speciesFilter,
                _ownerFilter,
                _sortOption
            ) { pets, query, species, owner, sort ->
                filterAndSortPets(pets, query, species, owner, sort)
            }.collect { filteredPets ->
                _uiState.value = _uiState.value.copy(
                    pets = filteredPets,
                    searchQuery = _searchQuery.value,
                    selectedSpeciesFilter = _speciesFilter.value,
                    selectedOwnerFilter = _ownerFilter.value,
                    sortOption = _sortOption.value
                )
            }
        }
    }

    private fun loadPets() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val isAdmin = SessionManager.isAdmin()
                val ownerId = SessionManager.getOwnerId()

                // Obtener mascotas desde Room Database
                val pets = if (isAdmin) {
                    repository.getAllPets()
                } else {
                    ownerId?.let { repository.getPetsByOwner(it) } ?: emptyList()
                }

                // Obtener lista de dueños para filtro (solo admin)
                val availableOwners = if (isAdmin) {
                    repository.getAllOwners().map { it.id to it.fullName }
                } else {
                    emptyList()
                }

                _allPets.value = pets
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAdmin = isAdmin,
                    availableOwners = availableOwners
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar mascotas: ${e.message}"
                )
            }
        }
    }

    private suspend fun filterAndSortPets(
        pets: List<Pet>,
        query: String,
        species: PetSpecies?,
        ownerId: String?,
        sortOption: PetSortOption
    ): List<Pet> {
        return pets
            .filter { pet ->
                // Filtro por búsqueda (nombre, raza, y dueño si es admin)
                val ownerName = repository.getOwnerById(pet.ownerId)?.fullName ?: ""
                val matchesQuery = query.isBlank() ||
                    pet.name.contains(query, ignoreCase = true) ||
                    pet.breed?.contains(query, ignoreCase = true) == true ||
                    ownerName.contains(query, ignoreCase = true)

                // Filtro por especie
                val matchesSpecies = species == null || pet.species == species

                // Filtro por dueño
                val matchesOwner = ownerId == null || pet.ownerId == ownerId

                matchesQuery && matchesSpecies && matchesOwner
            }
            .let { filteredList ->
                // Ordenamiento
                when (sortOption) {
                    PetSortOption.NAME_ASC -> filteredList.sortedBy { it.name.lowercase() }
                    PetSortOption.NAME_DESC -> filteredList.sortedByDescending { it.name.lowercase() }
                    PetSortOption.AGE_ASC -> filteredList.sortedBy { it.ageYears }
                    PetSortOption.AGE_DESC -> filteredList.sortedByDescending { it.ageYears }
                    PetSortOption.SPECIES -> filteredList.sortedBy { it.species.displayName }
                }
            }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Debounce y log de búsqueda
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            if (query.isNotBlank()) {
                ActivityLogger.log(
                    screen = ActivityLogger.Screens.PETS_LIST,
                    action = ActivityLogger.Actions.SEARCH,
                    metadata = mapOf("query" to query)
                )
            }
        }
    }

    fun updateSpeciesFilter(species: PetSpecies?) {
        _speciesFilter.value = species
        if (species != null) {
            ActivityLogger.log(
                screen = ActivityLogger.Screens.PETS_LIST,
                action = ActivityLogger.Actions.FILTER,
                metadata = mapOf("filterType" to "species", "value" to species.displayName)
            )
        }
    }

    fun updateSortOption(option: PetSortOption) {
        _sortOption.value = option
        ActivityLogger.log(
            screen = ActivityLogger.Screens.PETS_LIST,
            action = ActivityLogger.Actions.FILTER,
            metadata = mapOf("filterType" to "sort", "value" to option.displayName)
        )
    }

    fun updateOwnerFilter(ownerId: String?) {
        _ownerFilter.value = ownerId
        if (ownerId != null) {
            viewModelScope.launch {
                val ownerName = repository.getOwnerById(ownerId)?.fullName ?: ""
                ActivityLogger.log(
                    screen = ActivityLogger.Screens.PETS_LIST,
                    action = ActivityLogger.Actions.FILTER,
                    metadata = mapOf("filterType" to "owner", "value" to ownerName)
                )
            }
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _speciesFilter.value = null
        _ownerFilter.value = null
        _sortOption.value = PetSortOption.NAME_ASC
    }

    fun deletePet(petId: String) {
        viewModelScope.launch {
            try {
                val pet = repository.getPetById(petId)
                val petName = pet?.name ?: "Mascota"

                val success = repository.deletePet(petId)

                if (success) {
                    // Log de eliminación
                    ActivityLogger.logCrud(
                        screen = ActivityLogger.Screens.PETS_LIST,
                        action = ActivityLogger.Actions.DELETE,
                        entityType = ActivityLogger.EntityTypes.PET,
                        entityId = petId,
                        entityName = pet?.name
                    )
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "$petName eliminado correctamente"
                    )
                    loadPets() // Recargar lista desde Room
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al eliminar $petName"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.message}"
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        loadPets()
    }
}

