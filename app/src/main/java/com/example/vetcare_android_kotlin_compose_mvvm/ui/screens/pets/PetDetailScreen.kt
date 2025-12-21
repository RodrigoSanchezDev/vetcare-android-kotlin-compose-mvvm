package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Pantalla de detalle de mascota estilo perfil premium
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    viewModel: PetDetailViewModel = viewModel(),
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onAddConsultation: (String) -> Unit = {},
    onAddAppointment: (String) -> Unit = {},
    onAddVaccine: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin = SessionManager.isAdmin()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar mascota") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${uiState.pet?.name}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePet()
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("Eliminar", color = VetCareColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.pet?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { uiState.pet?.id?.let { onEdit(it) } }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = VetCareColors.Danger)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VetCareColors.Primary)
            }
        } else if (uiState.pet != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VetCareColors.Background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(VetCareSpacing.md),
                verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
            ) {
                // Profile Header
                item {
                    PetProfileHeader(pet = uiState.pet!!, owner = uiState.owner)
                }

                // Metrics Row
                item {
                    MetricsRow(pet = uiState.pet!!, vaccines = uiState.vaccines)
                }

                // Quick Actions (Admin only)
                if (isAdmin) {
                    item {
                        QuickActionsRow(
                            onAddConsultation = { onAddConsultation(petId) },
                            onAddAppointment = { onAddAppointment(petId) },
                            onAddVaccine = { onAddVaccine(petId) }
                        )
                    }
                }

                // Upcoming Appointments
                if (uiState.appointments.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Próximas Citas", icon = Icons.Default.CalendarMonth)
                    }
                    items(uiState.appointments.take(3)) { appointment ->
                        AppointmentItem(appointment = appointment)
                    }
                }

                // Vaccines
                if (uiState.vaccines.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Vacunas", icon = Icons.Default.Vaccines)
                    }
                    items(uiState.vaccines) { vaccine ->
                        VaccineItem(vaccine = vaccine)
                    }
                }

                // Medical History
                item {
                    SectionHeader(title = "Historial Médico", icon = Icons.Default.History)
                }

                if (uiState.consultations.isEmpty()) {
                    item {
                        SoftCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Sin consultas registradas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = VetCareColors.MutedText,
                                modifier = Modifier.padding(VetCareSpacing.md)
                            )
                        }
                    }
                } else {
                    items(uiState.consultations) { consultation ->
                        ConsultationItem(consultation = consultation)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(VetCareSpacing.xxl))
                }
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodyLarge,
                    color = VetCareColors.Danger
                )
            }
        }
    }
}

@Composable
private fun PetProfileHeader(
    pet: Pet,
    owner: Owner?,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar grande con imagen real
            Box(
                modifier = Modifier
                    .size(120.dp)
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
                        modifier = Modifier.size(64.dp),
                        tint = when (pet.species) {
                            PetSpecies.DOG -> VetCareColors.Primary
                            PetSpecies.CAT -> VetCareColors.Accent
                            else -> VetCareColors.MutedText
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.md))

            // Nombre
            Text(
                text = pet.name,
                style = MaterialTheme.typography.headlineMedium,
                color = VetCareColors.OnSurface
            )

            // Especie y raza
            Text(
                text = "${pet.species.displayName} • ${pet.breed ?: "Sin raza especificada"}",
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.MutedText
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Edad
            Surface(
                color = VetCareColors.Primary.copy(alpha = 0.1f),
                shape = VetCareShapes.small
            ) {
                Text(
                    text = "${pet.ageYears} ${if (pet.ageYears == 1) "año" else "años"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VetCareColors.Primary,
                    modifier = Modifier.padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.xs)
                )
            }

            // Dueño
            if (owner != null) {
                Spacer(modifier = Modifier.height(VetCareSpacing.md))
                HorizontalDivider(color = VetCareColors.Divider)
                Spacer(modifier = Modifier.height(VetCareSpacing.md))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = VetCareColors.MutedText,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Dueño",
                            style = MaterialTheme.typography.labelSmall,
                            color = VetCareColors.MutedText
                        )
                        Text(
                            text = owner.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = VetCareColors.OnSurface
                        )
                    }
                }
            }

            // Notas
            if (!pet.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(VetCareSpacing.md))
                Text(
                    text = pet.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = VetCareColors.MutedText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MetricsRow(
    pet: Pet,
    vaccines: List<VaccineRecord>,
    modifier: Modifier = Modifier
) {
    val upcomingVaccine = vaccines.minByOrNull { it.nextDueDate }
    val daysUntilVaccine = upcomingVaccine?.let {
        ChronoUnit.DAYS.between(java.time.LocalDate.now(), it.nextDueDate)
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
    ) {
        // Peso
        item {
            MetricCard(
                icon = Icons.Default.Scale,
                label = "Peso",
                value = pet.weightKg?.let { "${it} kg" } ?: "N/A",
                color = VetCareColors.Primary
            )
        }

        // Vacunas
        item {
            MetricCard(
                icon = Icons.Default.Vaccines,
                label = "Vacunas",
                value = "${vaccines.size} registros",
                color = VetCareColors.Success
            )
        }

        // Próxima vacuna
        if (daysUntilVaccine != null) {
            item {
                MetricCard(
                    icon = Icons.Default.Event,
                    label = "Próx. Vacuna",
                    value = if (daysUntilVaccine <= 0) "¡Vencida!" else "$daysUntilVaccine días",
                    color = if (daysUntilVaccine <= 7) VetCareColors.Danger else VetCareColors.Accent
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier.widthIn(min = 140.dp),
        containerColor = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(VetCareSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(VetCareSpacing.xs))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = VetCareColors.OnSurface,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = VetCareColors.MutedText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAddConsultation: () -> Unit,
    onAddAppointment: () -> Unit,
    onAddVaccine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
        ) {
            PrimaryButton(
                text = "Nueva Consulta",
                onClick = onAddConsultation,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "Agendar Cita",
                onClick = onAddAppointment,
                modifier = Modifier.weight(1f)
            )
        }
        SecondaryButton(
            text = "Registrar Vacuna",
            onClick = onAddVaccine
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VetCareSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VetCareColors.Primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = VetCareColors.OnBackground
        )
    }
}

@Composable
private fun AppointmentItem(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    val vet = MockDataRepository.getVetById(appointment.vetId)
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    SoftCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = VetCareColors.Primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.OnSurface
                )
                Text(
                    text = "${appointment.dateTime.format(dateFormatter)} • ${vet?.name ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
            }
            Surface(
                color = VetCareColors.Success.copy(alpha = 0.1f),
                shape = VetCareShapes.small
            ) {
                Text(
                    text = appointment.status.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.Success,
                    modifier = Modifier.padding(horizontal = VetCareSpacing.xs, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun VaccineItem(
    vaccine: VaccineRecord,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val daysUntil = ChronoUnit.DAYS.between(java.time.LocalDate.now(), vaccine.nextDueDate)
    val isUrgent = daysUntil <= 7

    SoftCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = if (isUrgent) VetCareColors.Danger.copy(alpha = 0.1f) else VetCareColors.SurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
        ) {
            Icon(
                Icons.Default.Vaccines,
                contentDescription = null,
                tint = if (isUrgent) VetCareColors.Danger else VetCareColors.Success
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vaccine.vaccineName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.OnSurface
                )
                Text(
                    text = "Última: ${vaccine.lastDate.format(dateFormatter)} • Próxima: ${vaccine.nextDueDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
            }
            if (isUrgent) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Urgente",
                    tint = VetCareColors.Danger,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ConsultationItem(
    consultation: Consultation,
    modifier: Modifier = Modifier
) {
    val vet = MockDataRepository.getVetById(consultation.vetId)
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = consultation.dateTime.format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = VetCareColors.Primary
                )
                Text(
                    text = vet?.name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
            }

            Text(
                text = "Diagnóstico: ${consultation.diagnosis}",
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.OnSurface
            )

            Text(
                text = "Tratamiento: ${consultation.treatment}",
                style = MaterialTheme.typography.bodySmall,
                color = VetCareColors.MutedText
            )

            if (!consultation.notes.isNullOrBlank()) {
                Text(
                    text = "Notas: ${consultation.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetDetailScreenPreview() {
    VetCareTheme {
        PetDetailScreen(petId = "pet-001")
    }
}

