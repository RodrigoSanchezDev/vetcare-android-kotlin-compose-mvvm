package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.Appointment
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Pantalla de lista de citas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsListScreen(
    viewModel: AppointmentsViewModel = viewModel(),
    onAddAppointment: () -> Unit = {},
    onAppointmentClick: (String) -> Unit = {},
    isAdmin: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
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
                    onClick = onAddAppointment,
                    containerColor = VetCareColors.Accent,
                    contentColor = VetCareColors.OnSurface
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva cita")
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
                    text = if (isAdmin) "Gestión de Citas" else "Mis Citas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VetCareColors.OnBackground
                )
                Text(
                    text = "${uiState.appointments.size} ${if (uiState.appointments.size == 1) "cita" else "citas"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.MutedText
                )
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VetCareSpacing.md),
                placeholder = { Text("Buscar por mascota o veterinario...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = VetCareColors.MutedText
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Filter chips
            LazyRow(
                modifier = Modifier.padding(vertical = VetCareSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
                contentPadding = PaddingValues(horizontal = VetCareSpacing.md)
            ) {
                items(AppointmentFilter.entries) { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter && uiState.selectedDate == null,
                        onClick = { viewModel.updateFilter(filter) },
                        label = { Text(filter.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                            selectedLabelColor = VetCareColors.Primary
                        )
                    )
                }
            }

            // Lista de citas
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState.appointments.isEmpty()) {
                    EmptyAppointmentsState(
                        filter = uiState.selectedFilter,
                        onAddAppointment = onAddAppointment,
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
                            items = uiState.appointments,
                            key = { it.id }
                        ) { appointment ->
                            AppointmentListItem(
                                appointment = appointment,
                                onClick = { onAppointmentClick(appointment.id) },
                                onStatusChange = { newStatus ->
                                    viewModel.updateAppointmentStatus(appointment.id, newStatus)
                                },
                                isAdmin = isAdmin
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(VetCareSpacing.xxl * 2))
                        }
                    }
                }
            }

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
private fun AppointmentListItem(
    appointment: Appointment,
    onClick: () -> Unit,
    onStatusChange: (AppointmentStatus) -> Unit,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    val pet = MockDataRepository.getPetById(appointment.petId)
    val vet = MockDataRepository.getVetById(appointment.vetId)
    val owner = pet?.let { MockDataRepository.getOwnerById(it.ownerId) }
    val dateFormatter = DateTimeFormatter.ofPattern("EEE dd MMM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var showStatusMenu by remember { mutableStateOf(false) }

    PremiumCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Fecha y hora
                Column {
                    Text(
                        text = appointment.dateTime.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        color = VetCareColors.OnSurface
                    )
                    Text(
                        text = appointment.dateTime.format(timeFormatter),
                        style = MaterialTheme.typography.headlineSmall,
                        color = VetCareColors.Primary
                    )
                }

                // Status chip con menú
                Box {
                    Surface(
                        onClick = { if (isAdmin) showStatusMenu = true },
                        color = getStatusColor(appointment.status).copy(alpha = 0.1f),
                        shape = VetCareShapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = VetCareSpacing.xxs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs)
                        ) {
                            Text(
                                text = appointment.status.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = getStatusColor(appointment.status)
                            )
                            if (isAdmin) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = getStatusColor(appointment.status)
                                )
                            }
                        }
                    }

                    if (isAdmin) {
                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false }
                        ) {
                            AppointmentStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.displayName) },
                                    onClick = {
                                        onStatusChange(status)
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        if (appointment.status == status) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = VetCareColors.Divider)

            // Info de mascota
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
            ) {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = VetCareColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pet?.name ?: "Mascota",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VetCareColors.OnSurface
                    )
                    if (isAdmin && owner != null) {
                        Text(
                            text = "Dueño: ${owner.fullName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = VetCareColors.MutedText
                        )
                    }
                }
            }

            // Motivo
            Text(
                text = appointment.reason,
                style = MaterialTheme.typography.bodySmall,
                color = VetCareColors.MutedText
            )

            // Veterinario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
            ) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = VetCareColors.MutedText,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = vet?.name ?: "Sin asignar",
                    style = MaterialTheme.typography.labelMedium,
                    color = VetCareColors.MutedText
                )
            }
        }
    }
}

@Composable
private fun getStatusColor(status: AppointmentStatus) = when (status) {
    AppointmentStatus.SCHEDULED -> VetCareColors.MutedText
    AppointmentStatus.CONFIRMED -> VetCareColors.Success
    AppointmentStatus.IN_PROGRESS -> VetCareColors.Accent
    AppointmentStatus.COMPLETED -> VetCareColors.Primary
    AppointmentStatus.CANCELLED -> VetCareColors.Danger
    AppointmentStatus.NO_SHOW -> VetCareColors.Danger
}

@Composable
private fun EmptyAppointmentsState(
    filter: AppointmentFilter,
    onAddAppointment: () -> Unit,
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
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = VetCareColors.MutedText
            )

            Text(
                text = when (filter) {
                    AppointmentFilter.UPCOMING -> "No hay citas próximas"
                    AppointmentFilter.TODAY -> "No hay citas para hoy"
                    AppointmentFilter.COMPLETED -> "No hay citas completadas"
                    AppointmentFilter.CANCELLED -> "No hay citas canceladas"
                    AppointmentFilter.ALL -> "No hay citas registradas"
                },
                style = MaterialTheme.typography.titleMedium,
                color = VetCareColors.OnBackground
            )

            if (isAdmin && filter == AppointmentFilter.UPCOMING) {
                PrimaryButton(
                    text = "Crear Nueva Cita",
                    onClick = onAddAppointment,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppointmentsListScreenPreview() {
    VetCareTheme {
        AppointmentsListScreen(isAdmin = true)
    }
}

