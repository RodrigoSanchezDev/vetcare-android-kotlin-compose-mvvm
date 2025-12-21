package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de formulario para crear/editar mascota
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: String? = null,
    viewModel: PetFormViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOwnerDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar Mascota" else "Nueva Mascota") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(VetCareColors.Background)
                .padding(paddingValues),
            contentPadding = PaddingValues(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Información básica
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Información Básica",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        // Nombre
                        VetCareTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            label = "Nombre *",
                            isError = uiState.nameError != null,
                            errorMessage = uiState.nameError,
                            enabled = !uiState.isSaving
                        )

                        // Especie
                        Text(
                            text = "Especie",
                            style = MaterialTheme.typography.labelMedium,
                            color = VetCareColors.MutedText
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                        ) {
                            PetSpecies.entries.take(4).forEach { species ->
                                FilterChip(
                                    selected = uiState.species == species,
                                    onClick = { viewModel.updateSpecies(species) },
                                    label = { Text(species.displayName) },
                                    enabled = !uiState.isSaving,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                                        selectedLabelColor = VetCareColors.Primary
                                    )
                                )
                            }
                        }

                        // Raza
                        VetCareTextField(
                            value = uiState.breed,
                            onValueChange = viewModel::updateBreed,
                            label = "Raza (opcional)",
                            enabled = !uiState.isSaving
                        )

                        // Edad y Peso en row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                        ) {
                            VetCareTextField(
                                value = uiState.ageYears,
                                onValueChange = viewModel::updateAge,
                                label = "Edad (años) *",
                                modifier = Modifier.weight(1f),
                                isError = uiState.ageError != null,
                                errorMessage = uiState.ageError,
                                enabled = !uiState.isSaving,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            VetCareTextField(
                                value = uiState.weightKg,
                                onValueChange = viewModel::updateWeight,
                                label = "Peso (kg)",
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                    }
                }
            }

            // Dueño
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Dueño",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        ExposedDropdownMenuBox(
                            expanded = showOwnerDropdown,
                            onExpandedChange = { showOwnerDropdown = it }
                        ) {
                            val selectedOwner = viewModel.owners.find { it.id == uiState.ownerId }

                            OutlinedTextField(
                                value = selectedOwner?.fullName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Seleccionar dueño *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOwnerDropdown)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                isError = uiState.ownerError != null,
                                shape = VetCareShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VetCareColors.Primary,
                                    unfocusedBorderColor = VetCareColors.Divider
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = showOwnerDropdown,
                                onDismissRequest = { showOwnerDropdown = false }
                            ) {
                                viewModel.owners.forEach { owner ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(owner.fullName)
                                                Text(
                                                    owner.email,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = VetCareColors.MutedText
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.updateOwner(owner.id)
                                            showOwnerDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null)
                                        }
                                    )
                                }
                            }
                        }

                        if (uiState.ownerError != null) {
                            Text(
                                text = uiState.ownerError!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.Danger
                            )
                        }
                    }
                }
            }

            // Notas
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Notas Adicionales",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        VetCareTextField(
                            value = uiState.notes,
                            onValueChange = viewModel::updateNotes,
                            label = "Notas (opcional)",
                            singleLine = false,
                            enabled = !uiState.isSaving,
                            modifier = Modifier.heightIn(min = 100.dp)
                        )
                    }
                }
            }

            // Error message
            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = VetCareColors.Danger,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Botones
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                ) {
                    PrimaryButton(
                        text = if (uiState.isEditing) "Guardar Cambios" else "Crear Mascota",
                        onClick = { viewModel.save() },
                        isLoading = uiState.isSaving,
                        enabled = !uiState.isSaving
                    )

                    SecondaryButton(
                        text = "Cancelar",
                        onClick = onBack,
                        enabled = !uiState.isSaving
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(VetCareSpacing.xxl))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetFormScreenPreview() {
    VetCareTheme {
        PetFormScreen()
    }
}

