package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de lista de mascotas con búsqueda, filtros y ordenamiento
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsListScreen(
    viewModel: PetsViewModel = viewModel(),
    onPetClick: (String) -> Unit = {},
    onAddPet: () -> Unit = {},
    isAdmin: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showOwnerFilter by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Mostrar error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddPet,
                    containerColor = VetCareColors.Accent,
                    contentColor = VetCareColors.OnSurface
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VetCareColors.Background)
                .padding(paddingValues)
        ) {
            // Header
            Column(
                modifier = Modifier.padding(
                    horizontal = VetCareSpacing.md,
                    vertical = VetCareSpacing.sm
                )
            ) {
                Text(
                    text = if (isAdmin) "Todas las Mascotas" else "Mis Mascotas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VetCareColors.OnBackground
                )
                Text(
                    text = "${uiState.pets.size} ${if (uiState.pets.size == 1) "mascota" else "mascotas"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.MutedText
                )
            }

            // Barra de búsqueda
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier.padding(horizontal = VetCareSpacing.md)
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Filtros y ordenamiento
            FilterSortRow(
                selectedSpecies = uiState.selectedSpeciesFilter,
                onSpeciesSelect = viewModel::updateSpeciesFilter,
                selectedOwner = uiState.selectedOwnerFilter,
                availableOwners = uiState.availableOwners,
                onOwnerSelect = viewModel::updateOwnerFilter,
                showOwnerFilter = isAdmin,
                sortOption = uiState.sortOption,
                onSortClick = { showSortMenu = true },
                onClearFilters = viewModel::clearFilters,
                modifier = Modifier.padding(horizontal = VetCareSpacing.md)
            )

            // Sort Menu Dropdown
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                PetSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            viewModel.updateSortOption(option)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (uiState.sortOption == option) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = VetCareColors.Primary)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Lista de mascotas
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState.pets.isEmpty()) {
                    EmptyPetsState(
                        hasFilters = uiState.searchQuery.isNotBlank() || uiState.selectedSpeciesFilter != null,
                        onClearFilters = viewModel::clearFilters,
                        onAddPet = onAddPet,
                        isAdmin = isAdmin
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = VetCareSpacing.md,
                            vertical = VetCareSpacing.xs
                        ),
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                    ) {
                        items(
                            items = uiState.pets,
                            key = { it.id }
                        ) { pet ->
                            PetListItem(
                                pet = pet,
                                onClick = { onPetClick(pet.id) },
                                showOwner = isAdmin
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(VetCareSpacing.xxl * 2))
                        }
                    }
                }
            }

            // Loading
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VetCareColors.Primary)
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar por nombre o raza...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = VetCareColors.MutedText
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = VetCareColors.MutedText
                    )
                }
            }
        },
        singleLine = true,
        shape = VetCareShapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetCareColors.Primary,
            unfocusedBorderColor = VetCareColors.Divider,
            focusedContainerColor = VetCareColors.Surface,
            unfocusedContainerColor = VetCareColors.Surface
        )
    )
}

@Composable
private fun FilterSortRow(
    selectedSpecies: PetSpecies?,
    onSpeciesSelect: (PetSpecies?) -> Unit,
    selectedOwner: String?,
    availableOwners: List<Pair<String, String>>,
    onOwnerSelect: (String?) -> Unit,
    showOwnerFilter: Boolean,
    sortOption: PetSortOption,
    onSortClick: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOwnerDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Species filter chips + Owner filter
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            item {
                FilterChip(
                    selected = selectedSpecies == null,
                    onClick = { onSpeciesSelect(null) },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                        selectedLabelColor = VetCareColors.Primary
                    )
                )
            }
            items(listOf(PetSpecies.DOG, PetSpecies.CAT, PetSpecies.BIRD, PetSpecies.OTHER)) { species ->
                FilterChip(
                    selected = selectedSpecies == species,
                    onClick = { onSpeciesSelect(if (selectedSpecies == species) null else species) },
                    label = { Text(species.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                        selectedLabelColor = VetCareColors.Primary
                    )
                )
            }

            // Filtro por dueño (solo admin)
            if (showOwnerFilter && availableOwners.isNotEmpty()) {
                item {
                    Box {
                        FilterChip(
                            selected = selectedOwner != null,
                            onClick = { showOwnerDropdown = true },
                            label = {
                                Text(availableOwners.find { it.first == selectedOwner }?.second ?: "Dueño")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VetCareColors.Accent.copy(alpha = 0.2f),
                                selectedLabelColor = VetCareColors.Accent
                            )
                        )
                        DropdownMenu(
                            expanded = showOwnerDropdown,
                            onDismissRequest = { showOwnerDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todos los dueños") },
                                onClick = { onOwnerSelect(null); showOwnerDropdown = false }
                            )
                            availableOwners.forEach { (ownerId, ownerName) ->
                                DropdownMenuItem(
                                    text = { Text(ownerName) },
                                    onClick = { onOwnerSelect(ownerId); showOwnerDropdown = false },
                                    trailingIcon = {
                                        if (selectedOwner == ownerId) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = VetCareColors.Primary)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sort button
        IconButton(onClick = onSortClick) {
            Icon(
                Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Ordenar",
                tint = VetCareColors.Primary
            )
        }
    }
}

@Composable
private fun PetListItem(
    pet: Pet,
    onClick: () -> Unit,
    showOwner: Boolean = false,
    modifier: Modifier = Modifier
) {
    val owner = if (showOwner) MockDataRepository.getOwnerById(pet.ownerId) else null
    val upcomingAppointments = MockDataRepository.getAppointmentsByPet(pet.id)
        .filter { it.dateTime.isAfter(java.time.LocalDateTime.now()) }

    PremiumCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Avatar de mascota con imagen real
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        when (pet.species) {
                            PetSpecies.DOG -> VetCareColors.Primary.copy(alpha = 0.15f)
                            PetSpecies.CAT -> VetCareColors.Accent.copy(alpha = 0.25f)
                            else -> VetCareColors.SurfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (pet.photoRes != null) {
                    Image(
                        painter = painterResource(id = pet.photoRes),
                        contentDescription = pet.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = pet.name,
                        modifier = Modifier.size(32.dp),
                        tint = when (pet.species) {
                            PetSpecies.DOG -> VetCareColors.Primary
                            PetSpecies.CAT -> VetCareColors.Accent
                            else -> VetCareColors.MutedText
                        }
                    )
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = VetCareColors.OnSurface
                )
                Text(
                    text = "${pet.species.displayName} • ${pet.breed ?: "Sin raza"} • ${pet.ageYears} años",
                    style = MaterialTheme.typography.bodySmall,
                    color = VetCareColors.MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showOwner && owner != null) {
                    Text(
                        text = "Dueño: ${owner.fullName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.Primary
                    )
                }
            }

            // Badge próxima cita
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (upcomingAppointments.isNotEmpty()) {
                    Surface(
                        color = VetCareColors.Success.copy(alpha = 0.1f),
                        shape = VetCareShapes.small
                    ) {
                        Text(
                            text = "Cita próxima",
                            style = MaterialTheme.typography.labelSmall,
                            color = VetCareColors.Success,
                            modifier = Modifier.padding(horizontal = VetCareSpacing.xs, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Ver detalle",
                        tint = VetCareColors.MutedText
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPetsState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit,
    onAddPet: () -> Unit,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = VetCareColors.MutedText
            )

            Text(
                text = if (hasFilters) "No se encontraron mascotas" else "No hay mascotas registradas",
                style = MaterialTheme.typography.titleMedium,
                color = VetCareColors.OnBackground
            )

            Text(
                text = if (hasFilters) "Intenta con otros filtros" else "Agrega tu primera mascota",
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.MutedText
            )

            if (hasFilters) {
                SecondaryButton(
                    text = "Limpiar filtros",
                    onClick = onClearFilters,
                    modifier = Modifier.width(200.dp)
                )
            } else if (isAdmin) {
                PrimaryButton(
                    text = "Agregar Mascota",
                    onClick = onAddPet,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetsListScreenPreview() {
    VetCareTheme {
        PetsListScreen(isAdmin = true)
    }
}

