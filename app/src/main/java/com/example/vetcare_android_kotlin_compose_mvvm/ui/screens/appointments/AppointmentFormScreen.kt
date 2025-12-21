package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Pantalla de formulario para crear/editar cita
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    appointmentId: String? = null,
    petId: String? = null,
    viewModel: AppointmentFormViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPetDropdown by remember { mutableStateOf(false) }
    var showVetDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    LaunchedEffect(appointmentId, petId) {
        if (appointmentId != null) {
            viewModel.loadAppointment(appointmentId)
        } else if (petId != null) {
            viewModel.loadForPet(petId)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.updateDate(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.time.hour,
            initialMinute = uiState.time.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar hora") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateTime(
                        java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar Cita" else "Nueva Cita") },
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
            // Selección de mascota
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Mascota",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        ExposedDropdownMenuBox(
                            expanded = showPetDropdown,
                            onExpandedChange = { showPetDropdown = it }
                        ) {
                            val selectedPet = viewModel.pets.find { it.id == uiState.petId }

                            OutlinedTextField(
                                value = selectedPet?.let { "${it.name} (${it.species.displayName})" } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Seleccionar mascota *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPetDropdown)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                isError = uiState.petError != null,
                                shape = VetCareShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VetCareColors.Primary,
                                    unfocusedBorderColor = VetCareColors.Divider
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = showPetDropdown,
                                onDismissRequest = { showPetDropdown = false }
                            ) {
                                viewModel.pets.forEach { pet ->
                                    val owner = com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository.getOwnerById(pet.ownerId)
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("${pet.name} (${pet.species.displayName})")
                                                Text(
                                                    "Dueño: ${owner?.fullName ?: "N/A"}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = VetCareColors.MutedText
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.updatePet(pet.id)
                                            showPetDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Pets, contentDescription = null)
                                        }
                                    )
                                }
                            }
                        }

                        if (uiState.petError != null) {
                            Text(
                                text = uiState.petError!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.Danger
                            )
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

            // Fecha y Hora
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Fecha y Hora",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                        ) {
                            // Fecha
                            OutlinedTextField(
                                value = uiState.date.format(dateFormatter),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fecha") },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showDatePicker = true },
                                enabled = false,
                                shape = VetCareShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = VetCareColors.Divider,
                                    disabledTextColor = VetCareColors.OnSurface,
                                    disabledLabelColor = VetCareColors.MutedText,
                                    disabledLeadingIconColor = VetCareColors.Primary
                                )
                            )

                            // Hora
                            OutlinedTextField(
                                value = uiState.time.format(timeFormatter),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hora") },
                                leadingIcon = {
                                    Icon(Icons.Default.Schedule, contentDescription = null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showTimePicker = true },
                                enabled = false,
                                shape = VetCareShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = VetCareColors.Divider,
                                    disabledTextColor = VetCareColors.OnSurface,
                                    disabledLabelColor = VetCareColors.MutedText,
                                    disabledLeadingIconColor = VetCareColors.Primary
                                )
                            )
                        }

                        // Botones para seleccionar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                        ) {
                            SecondaryButton(
                                text = "Cambiar fecha",
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f)
                            )
                            SecondaryButton(
                                text = "Cambiar hora",
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Motivo y notas
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Detalles",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        VetCareTextField(
                            value = uiState.reason,
                            onValueChange = viewModel::updateReason,
                            label = "Motivo de la cita *",
                            isError = uiState.reasonError != null,
                            errorMessage = uiState.reasonError,
                            enabled = !uiState.isSaving
                        )

                        VetCareTextField(
                            value = uiState.notes,
                            onValueChange = viewModel::updateNotes,
                            label = "Notas adicionales (opcional)",
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
                        text = if (uiState.isEditing) "Guardar Cambios" else "Crear Cita",
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
private fun AppointmentFormScreenPreview() {
    VetCareTheme {
        AppointmentFormScreen()
    }
}

