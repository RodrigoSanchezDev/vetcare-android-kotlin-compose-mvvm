package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.admin

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
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Dashboard de Administrador
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = viewModel(),
    onNavigateToPets: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToVets: () -> Unit = {},
    onNavigateToActivityLog: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsState()
    val recentAppointments by viewModel.recentAppointments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background),
        contentPadding = PaddingValues(VetCareSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
    ) {
        // Header con botón settings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
                ) {
                    Text(
                        text = "¡Hola, Admin!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = VetCareColors.OnBackground
                    )
                    Text(
                        text = "Panel de control de VetCare",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VetCareColors.MutedText
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = VetCareColors.OnBackground
                    )
                }
            }
        }

        // Stats Cards Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm),
                contentPadding = PaddingValues(vertical = VetCareSpacing.xs)
            ) {
                item {
                    StatCard(
                        icon = Icons.Default.Pets,
                        label = "Mascotas",
                        value = stats.totalPets.toString(),
                        containerColor = VetCareColors.Primary.copy(alpha = 0.1f),
                        iconTint = VetCareColors.Primary
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.People,
                        label = "Dueños",
                        value = stats.totalOwners.toString(),
                        containerColor = VetCareColors.Success.copy(alpha = 0.1f),
                        iconTint = VetCareColors.Success
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.CalendarToday,
                        label = "Citas Hoy",
                        value = stats.todayAppointments.toString(),
                        containerColor = VetCareColors.Accent.copy(alpha = 0.2f),
                        iconTint = VetCareColors.Accent
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.Vaccines,
                        label = "Vacunas",
                        value = stats.upcomingVaccines.toString(),
                        containerColor = VetCareColors.Danger.copy(alpha = 0.1f),
                        iconTint = VetCareColors.Danger
                    )
                }
            }
        }

        // Acciones Rápidas
        item {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium,
                color = VetCareColors.OnBackground,
                modifier = Modifier.padding(top = VetCareSpacing.sm)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
            ) {
                QuickActionCard(
                    icon = Icons.Default.Pets,
                    label = "Mascotas",
                    onClick = onNavigateToPets,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.CalendarMonth,
                    label = "Citas",
                    onClick = onNavigateToAppointments,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.MedicalServices,
                    label = "Staff",
                    onClick = onNavigateToVets,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Próximas Citas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Próximas Citas",
                    style = MaterialTheme.typography.titleMedium,
                    color = VetCareColors.OnBackground
                )
                TextButton(onClick = onNavigateToAppointments) {
                    Text("Ver todas", color = VetCareColors.Primary)
                }
            }
        }

        if (recentAppointments.isEmpty()) {
            item {
                SoftCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No hay citas programadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VetCareColors.MutedText,
                        modifier = Modifier.padding(VetCareSpacing.md)
                    )
                }
            }
        } else {
            items(recentAppointments) { appointment ->
                AppointmentCard(appointment = appointment)
            }
        }

        // Espacio final
        item {
            Spacer(modifier = Modifier.height(VetCareSpacing.xxl))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    containerColor: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier.width(140.dp),
        containerColor = containerColor
    ) {
        Column(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(VetCareSizes.iconLarge)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = VetCareColors.OnSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = VetCareColors.MutedText
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = VetCareShapes.medium,
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VetCareSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        VetCareColors.Primary.copy(alpha = 0.1f),
                        shape = VetCareShapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = VetCareColors.Primary,
                    modifier = Modifier.size(VetCareSizes.iconMedium)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = VetCareColors.OnSurface
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    val pet = MockDataRepository.getPetById(appointment.petId)
    val vet = MockDataRepository.getVetById(appointment.vetId)
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")

    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs)
            ) {
                Text(
                    text = pet?.name ?: "Mascota",
                    style = MaterialTheme.typography.titleSmall,
                    color = VetCareColors.OnSurface
                )
                Text(
                    text = appointment.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = VetCareColors.MutedText
                )
                Text(
                    text = vet?.name ?: "Veterinario",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.Primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xxs)
            ) {
                Text(
                    text = appointment.dateTime.format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = VetCareColors.OnSurface
                )
                AppointmentStatusChip(status = appointment.status)
            }
        }
    }
}

@Composable
private fun AppointmentStatusChip(status: AppointmentStatus) {
    val (color, text) = when (status) {
        AppointmentStatus.SCHEDULED -> VetCareColors.MutedText to "Programada"
        AppointmentStatus.CONFIRMED -> VetCareColors.Success to "Confirmada"
        AppointmentStatus.IN_PROGRESS -> VetCareColors.Accent to "En curso"
        AppointmentStatus.COMPLETED -> VetCareColors.Primary to "Completada"
        AppointmentStatus.CANCELLED -> VetCareColors.Danger to "Cancelada"
        AppointmentStatus.NO_SHOW -> VetCareColors.Danger to "No asistió"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = VetCareShapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = VetCareSpacing.xs, vertical = VetCareSpacing.xxs)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AdminHomeScreenPreview() {
    VetCareTheme {
        AdminHomeScreen()
    }
}

