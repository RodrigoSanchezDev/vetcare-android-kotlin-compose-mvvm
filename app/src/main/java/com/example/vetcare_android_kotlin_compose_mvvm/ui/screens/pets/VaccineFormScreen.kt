package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Pantalla de formulario para crear/editar vacuna
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineFormScreen(
    petId: String,
    vaccineId: String? = null,
    viewModel: VaccineFormViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLastDatePicker by remember { mutableStateOf(false) }
    var showNextDatePicker by remember { mutableStateOf(false) }
    var showVaccineDropdown by remember { mutableStateOf(false) }

    val pet = MockDataRepository.getPetById(petId)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(vaccineId, petId) {
        viewModel.loadVaccine(vaccineId, petId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    // Date Picker para última fecha
    if (showLastDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.lastDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showLastDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.updateLastDate(date)
                    }
                    showLastDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLastDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Date Picker para próxima fecha
    if (showNextDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.nextDueDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showNextDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.updateNextDueDate(date)
                    }
                    showNextDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNextDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar Vacuna" else "Nueva Vacuna") },
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
                        }
                    }
                }
            }

            // Nombre de vacuna
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                        ) {
                            Icon(
                                Icons.Default.Vaccines,
                                contentDescription = null,
                                tint = VetCareColors.Success,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Vacuna",
                                style = MaterialTheme.typography.titleMedium,
                                color = VetCareColors.OnSurface
                            )
                        }

                        // Chips de vacunas comunes
                        Text(
                            text = "Vacunas comunes:",
                            style = MaterialTheme.typography.labelMedium,
                            color = VetCareColors.MutedText
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                        ) {
                            items(viewModel.commonVaccines) { vaccine ->
                                FilterChip(
                                    selected = uiState.vaccineName == vaccine,
                                    onClick = { viewModel.updateVaccineName(vaccine) },
                                    label = { Text(vaccine) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VetCareColors.Success.copy(alpha = 0.2f),
                                        selectedLabelColor = VetCareColors.Success
                                    )
                                )
                            }
                        }

                        VetCareTextField(
                            value = uiState.vaccineName,
                            onValueChange = viewModel::updateVaccineName,
                            label = "Nombre de vacuna *",
                            isError = uiState.nameError != null,
                            errorMessage = uiState.nameError,
                            enabled = !uiState.isSaving
                        )
                    }
                }
            }

            // Fechas
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "Fechas",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnSurface
                        )

                        // Última aplicación
                        OutlinedTextField(
                            value = uiState.lastDate.format(dateFormatter),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Última aplicación") },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLastDatePicker = true },
                            enabled = false,
                            shape = VetCareShapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = VetCareColors.Divider,
                                disabledTextColor = VetCareColors.OnSurface,
                                disabledLabelColor = VetCareColors.MutedText,
                                disabledLeadingIconColor = VetCareColors.Primary
                            )
                        )

                        SecondaryButton(
                            text = "Cambiar fecha de aplicación",
                            onClick = { showLastDatePicker = true }
                        )

                        Spacer(modifier = Modifier.height(VetCareSpacing.xs))

                        // Próxima aplicación
                        OutlinedTextField(
                            value = uiState.nextDueDate.format(dateFormatter),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Próxima aplicación") },
                            leadingIcon = {
                                Icon(Icons.Default.Event, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showNextDatePicker = true },
                            enabled = false,
                            shape = VetCareShapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = VetCareColors.Divider,
                                disabledTextColor = VetCareColors.OnSurface,
                                disabledLabelColor = VetCareColors.MutedText,
                                disabledLeadingIconColor = VetCareColors.Accent
                            )
                        )

                        SecondaryButton(
                            text = "Cambiar próxima fecha",
                            onClick = { showNextDatePicker = true }
                        )
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
                            text = "Notas",
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
                        text = if (uiState.isEditing) "Guardar Cambios" else "Registrar Vacuna",
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
private fun VaccineFormScreenPreview() {
    VetCareTheme {
        VaccineFormScreen(petId = "pet-001")
    }
}

