package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Pantalla de lista de veterinarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinariansListScreen(
    viewModel: VeterinariansViewModel = viewModel(),
    onVetClick: (String) -> Unit = {},
    onAddVet: () -> Unit = {},
    onEditVet: (String) -> Unit = {},
    isAdmin: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredVets = viewModel.getFilteredVets()
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
                    onClick = onAddVet,
                    containerColor = VetCareColors.Accent,
                    contentColor = VetCareColors.OnSurface
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar veterinario")
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
                    text = "Staff Veterinario",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VetCareColors.OnBackground
                )
                Text(
                    text = "${filteredVets.size} veterinarios",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.MutedText
                )
            }

            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VetCareSpacing.md),
                placeholder = { Text("Buscar veterinario...") },
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

            // Lista de veterinarios
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VetCareColors.Primary)
                }
            } else if (filteredVets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = VetCareColors.MutedText
                        )
                        Text(
                            text = "No se encontraron veterinarios",
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.OnBackground
                        )
                        if (isAdmin) {
                            TextButton(onClick = onAddVet) {
                                Text("Agregar veterinario")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = VetCareSpacing.md,
                        vertical = VetCareSpacing.xs
                    ),
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                ) {
                    items(
                        items = filteredVets,
                        key = { it.vet.id }
                    ) { vetWithStats ->
                        VetListItem(
                            vetWithStats = vetWithStats,
                            onClick = { onVetClick(vetWithStats.vet.id) },
                            onEdit = if (isAdmin) ({ onEditVet(vetWithStats.vet.id) }) else null,
                            onDelete = if (isAdmin) ({ viewModel.deleteVet(vetWithStats.vet.id) }) else null,
                            isAdmin = isAdmin
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(VetCareSpacing.xxl * 2))
                    }
                }
            }
        }
    }
}

@Composable
private fun VetListItem(
    vetWithStats: VetWithStats,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    isAdmin: Boolean = false,
    modifier: Modifier = Modifier
) {
    val vet = vetWithStats.vet
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar veterinario") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${vet.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = VetCareColors.Danger)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Header con avatar y nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
            ) {
                // Avatar con imagen real
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(VetCareColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (vet.avatarRes != null) {
                        Image(
                            painter = painterResource(id = vet.avatarRes),
                            contentDescription = vet.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = vet.name.split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString(""),
                            style = MaterialTheme.typography.titleMedium,
                            color = VetCareColors.Primary
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vet.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = VetCareColors.OnSurface
                    )
                    Text(
                        text = vet.specialty ?: "Medicina General",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetCareColors.Primary
                    )
                    if (vet.phone != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = VetCareColors.MutedText
                            )
                            Text(
                                text = vet.phone,
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.MutedText
                            )
                        }
                    }
                }

                // Menú de opciones (solo admin)
                if (isAdmin) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = VetCareColors.MutedText
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onEdit?.invoke()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = VetCareColors.Danger) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = VetCareColors.Danger) },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirmation = true
                                }
                            )
                        }
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
            ) {
                // Citas hoy
                StatChip(
                    icon = Icons.Default.Today,
                    label = "Hoy",
                    value = vetWithStats.todayAppointments.toString(),
                    color = if (vetWithStats.todayAppointments > 0) VetCareColors.Accent else VetCareColors.MutedText
                )

                // Total citas
                StatChip(
                    icon = Icons.Default.CalendarMonth,
                    label = "Total",
                    value = vetWithStats.totalAppointments.toString(),
                    color = VetCareColors.Primary
                )
            }

            // Próximas citas
            if (vetWithStats.upcomingAppointments.isNotEmpty()) {
                HorizontalDivider(color = VetCareColors.Divider)

                Text(
                    text = "Próximas citas",
                    style = MaterialTheme.typography.labelMedium,
                    color = VetCareColors.MutedText
                )

                vetWithStats.upcomingAppointments.forEach { appointmentWithPet ->
                    val pet = appointmentWithPet.pet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Pets,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = VetCareColors.MutedText
                            )
                            Text(
                                text = pet?.name ?: "Mascota",
                                style = MaterialTheme.typography.bodySmall,
                                color = VetCareColors.OnSurface
                            )
                        }
                        Text(
                            text = appointmentWithPet.appointment.dateTime.format(dateFormatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = VetCareColors.MutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = VetCareShapes.small,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = VetCareSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = "$label: $value",
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VeterinariansListScreenPreview() {
    VetCareTheme {
        VeterinariansListScreen()
    }
}

