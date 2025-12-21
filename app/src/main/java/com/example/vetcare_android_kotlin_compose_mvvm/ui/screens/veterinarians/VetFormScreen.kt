package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.VetCareTextField
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de formulario para crear/editar veterinario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetFormScreen(
    vetId: String? = null,
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: VetFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSpecialtyMenu by remember { mutableStateOf(false) }

    // Cargar datos si es edición
    LaunchedEffect(vetId) {
        viewModel.loadVet(vetId)
    }

    // Navegar después de guardar exitosamente
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "Editar Veterinario" else "Nuevo Veterinario",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VetCareColors.Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Card de información básica
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface),
                shape = VetCareShapes.large
            ) {
                Column(
                    modifier = Modifier.padding(VetCareSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    Text(
                        text = "Información básica",
                        style = MaterialTheme.typography.titleMedium,
                        color = VetCareColors.OnSurface
                    )

                    // Nombre
                    VetCareTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = "Nombre completo",
                        placeholder = "Ej: Dr. Juan Pérez",
                        isError = uiState.nameError != null,
                        errorMessage = uiState.nameError,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    )

                    // Especialidad
                    Box {
                        VetCareTextField(
                            value = uiState.specialty,
                            onValueChange = viewModel::updateSpecialty,
                            label = "Especialidad",
                            placeholder = "Selecciona una especialidad",
                            isError = uiState.specialtyError != null,
                            errorMessage = uiState.specialtyError,
                            leadingIcon = {
                                Icon(Icons.Default.MedicalServices, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showSpecialtyMenu = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = showSpecialtyMenu,
                            onDismissRequest = { showSpecialtyMenu = false }
                        ) {
                            viewModel.specialties.forEach { specialty ->
                                DropdownMenuItem(
                                    text = { Text(specialty) },
                                    onClick = {
                                        viewModel.updateSpecialty(specialty)
                                        showSpecialtyMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Card de contacto
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface),
                shape = VetCareShapes.large
            ) {
                Column(
                    modifier = Modifier.padding(VetCareSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    Text(
                        text = "Información de contacto",
                        style = MaterialTheme.typography.titleMedium,
                        color = VetCareColors.OnSurface
                    )

                    // Teléfono
                    VetCareTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::updatePhone,
                        label = "Teléfono",
                        placeholder = "Ej: +56 9 1234 5678",
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón guardar
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VetCareColors.Primary
                ),
                shape = VetCareShapes.medium
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = VetCareColors.OnPrimary
                    )
                } else {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(VetCareSpacing.sm))
                    Text(
                        text = if (uiState.isEditing) "Guardar cambios" else "Crear veterinario",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Error message
            uiState.error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VetCareColors.Danger.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(VetCareSpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = VetCareColors.Danger
                        )
                        Text(
                            text = errorMessage,
                            color = VetCareColors.Danger,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.lg))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VetFormScreenPreview() {
    VetCareTheme {
        VetFormScreen()
    }
}

