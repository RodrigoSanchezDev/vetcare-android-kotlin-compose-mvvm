package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.consultations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de formulario para crear/editar consulta médica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationFormScreen(
    petId: String,
    consultationId: String? = null,
    viewModel: ConsultationFormViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showVetDropdown by remember { mutableStateOf(false) }

    val pet = MockDataRepository.getPetById(petId)

    LaunchedEffect(consultationId, petId) {
        viewModel.loadConsultation(consultationId, petId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar Consulta" else "Nueva Consulta") },
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
            // Info de la mascota
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            tint = VetCareColors.Primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Paciente",
                                style = MaterialTheme.typography.labelMedium,
                                color = VetCareColors.MutedText
                            )
                            Text(
                                text = pet?.name ?: "Mascota",
                                style = MaterialTheme.typography.titleMedium,
                                color = VetCareColors.OnSurface
                            )
                            if (pet != null) {
                                Text(
                                    text = "${pet.species.displayName} • ${pet.breed ?: "Sin raza"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VetCareColors.MutedText
                                )
                            }
                        }
                    }
                }
            }

            // Selección de veterinario
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Veterinario",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        ExposedDropdownMenuBox(
                            expanded = showVetDropdown,
                            onExpandedChange = { showVetDropdown = it }
                        ) {
                            val selectedVet = viewModel.veterinarians.find { it.id == uiState.vetId }

                            OutlinedTextField(
                                value = selectedVet?.let { "${it.name} - ${it.specialty ?: "General"}" } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Seleccionar veterinario *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showVetDropdown)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                isError = uiState.vetError != null,
                                shape = VetCareShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VetCareColors.Primary,
                                    unfocusedBorderColor = VetCareColors.Divider
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = showVetDropdown,
                                onDismissRequest = { showVetDropdown = false }
                            ) {
                                viewModel.veterinarians.forEach { vet ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(vet.name)
                                                Text(
                                                    vet.specialty ?: "Medicina General",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = VetCareColors.MutedText
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.updateVet(vet.id)
                                            showVetDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.MedicalServices, contentDescription = null)
                                        }
                                    )
                                }
                            }
                        }

                        if (uiState.vetError != null) {
                            Text(
                                text = uiState.vetError!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.Danger
                            )
                        }
                    }
                }
            }

            // Diagnóstico
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                        ) {
                            Icon(
                                Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = VetCareColors.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Diagnóstico",
                                style = MaterialTheme.typography.titleMedium,
                                color = VetCareColors.OnSurface
                            )
                        }

                        VetCareTextField(
                            value = uiState.diagnosis,
                            onValueChange = viewModel::updateDiagnosis,
                            label = "Diagnóstico *",
                            singleLine = false,
                            isError = uiState.diagnosisError != null,
                            errorMessage = uiState.diagnosisError,
                            enabled = !uiState.isSaving,
                            modifier = Modifier.heightIn(min = 100.dp)
                        )
                    }
                }
            }

            // Tratamiento
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                        ) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                tint = VetCareColors.Success,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Tratamiento",
                                style = MaterialTheme.typography.titleMedium,
                                color = VetCareColors.OnSurface
                            )
                        }

                        VetCareTextField(
                            value = uiState.treatment,
                            onValueChange = viewModel::updateTreatment,
                            label = "Tratamiento indicado *",
                            singleLine = false,
                            isError = uiState.treatmentError != null,
                            errorMessage = uiState.treatmentError,
                            enabled = !uiState.isSaving,
                            modifier = Modifier.heightIn(min = 100.dp)
                        )
                    }
                }
            }

            // Notas adicionales
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
                            modifier = Modifier.heightIn(min = 80.dp)
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
                        text = if (uiState.isEditing) "Guardar Cambios" else "Registrar Consulta",
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
private fun ConsultationFormScreenPreview() {
    VetCareTheme {
        ConsultationFormScreen(petId = "pet-001")
    }
}

