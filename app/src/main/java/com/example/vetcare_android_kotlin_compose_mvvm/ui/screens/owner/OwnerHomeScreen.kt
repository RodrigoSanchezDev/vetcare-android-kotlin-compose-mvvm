package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Dashboard del Dueño de Mascota - Estilo Premium VetCare
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    viewModel: OwnerHomeViewModel = viewModel(),
    onNavigateToPets: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToPetDetail: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val ownerName by viewModel.ownerName.collectAsState()
    val myPets by viewModel.myPets.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val upcomingVaccines by viewModel.upcomingVaccines.collectAsState()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background)
    ) {
        // Header con saludo y settings
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { -50 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Petcare",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = VetCareColors.OnBackground
                        )
                    }

                    // Settings icon
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = VetCareColors.OnBackground
                        )
                    }
                }
            }
        }

        // Banner principal - Find Veterinarians
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { -30 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VetCareSpacing.md)
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = VetCareColors.OnSurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(VetCareSpacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(0.6f)
                        ) {
                            Text(
                                text = "Find Nearest\nVeterinarians for\nYour Pet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                ),
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VetCareColors.Primary.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sección Services
        item {
            Column(
                modifier = Modifier.padding(
                    horizontal = VetCareSpacing.md,
                    vertical = VetCareSpacing.md
                )
            ) {
                Text(
                    text = "Services",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = VetCareColors.OnBackground
                )

                Spacer(modifier = Modifier.height(VetCareSpacing.md))

                // Grid de servicios 2x2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                ) {
                    ServiceCardCompact(
                        icon = Icons.Default.MedicalServices,
                        label = "Veterinary",
                        backgroundColor = Color(0xFFE8F5E9),
                        onClick = onNavigateToAppointments,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCardCompact(
                        icon = Icons.Default.ContentCut,
                        label = "Grooming",
                        backgroundColor = Color(0xFFE0F7FA),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(VetCareSpacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                ) {
                    ServiceCardCompact(
                        icon = Icons.Default.Vaccines,
                        label = "Vaccines",
                        backgroundColor = Color(0xFFFCE4EC),
                        onClick = onNavigateToPets,
                        modifier = Modifier.weight(1f)
                    )
                    ServiceCardCompact(
                        icon = Icons.Default.Restaurant,
                        label = "Foods",
                        backgroundColor = Color(0xFFFFF3E0),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Mis Mascotas
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VetCareSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Pets",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = VetCareColors.OnBackground
                )
                TextButton(onClick = onNavigateToPets) {
                    Text("See all", color = VetCareColors.Primary)
                }
            }
        }

        item {
            if (myPets.isEmpty()) {
                EmptyPetsCard(onAddPet = onNavigateToPets)
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = VetCareSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    items(myPets) { pet ->
                        PetProfileCard(
                            pet = pet,
                            onClick = { onNavigateToPetDetail(pet.id) }
                        )
                    }
                }
            }
        }

        // Próximas Citas
        if (upcomingAppointments.isNotEmpty()) {
            item {
                Text(
                    text = "Upcoming Appointments",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = VetCareColors.OnBackground,
                    modifier = Modifier.padding(
                        horizontal = VetCareSpacing.md,
                        vertical = VetCareSpacing.sm
                    )
                )
            }

            items(upcomingAppointments) { appointment ->
                AppointmentCardCompact(
                    appointment = appointment,
                    modifier = Modifier.padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.xxs)
                )
            }
        }

        // Espacio final
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ServiceCardCompact(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(VetCareSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VetCareColors.OnSurface.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(VetCareSpacing.xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = VetCareColors.OnSurface
            )
        }
    }
}

@Composable
private fun PetProfileCard(
    pet: Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(VetCareSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar de mascota con imagen real
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when (pet.species) {
                            PetSpecies.DOG -> VetCareColors.Primary.copy(alpha = 0.1f)
                            PetSpecies.CAT -> VetCareColors.Accent.copy(alpha = 0.2f)
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
                        modifier = Modifier.size(40.dp),
                        tint = when (pet.species) {
                            PetSpecies.DOG -> VetCareColors.Primary
                            PetSpecies.CAT -> VetCareColors.Accent
                            else -> VetCareColors.MutedText
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = VetCareColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = pet.species.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = VetCareColors.MutedText
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Métricas
            Row(
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
            ) {
                // Peso
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Scale,
                        contentDescription = null,
                        tint = VetCareColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = pet.weightKg?.let { "${it}kg" } ?: "N/A",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.OnSurface
                    )
                }

                // Edad
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Cake,
                        contentDescription = null,
                        tint = VetCareColors.Accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${pet.ageYears}y",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.OnSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPetsCard(
    onAddPet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VetCareSpacing.md),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VetCareSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = VetCareColors.MutedText
            )
            Spacer(modifier = Modifier.height(VetCareSpacing.sm))
            Text(
                text = "No pets registered",
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.MutedText
            )
            Spacer(modifier = Modifier.height(VetCareSpacing.md))
            PrimaryButton(
                text = "Add Pet",
                onClick = onAddPet,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}

@Composable
private fun AppointmentCardCompact(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    val pet = MockDataRepository.getPetById(appointment.petId)
    val vet = MockDataRepository.getVetById(appointment.vetId)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VetCareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Fecha
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(VetCareColors.Primary.copy(alpha = 0.1f))
                    .padding(VetCareSpacing.sm)
            ) {
                Text(
                    text = appointment.dateTime.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = VetCareColors.Primary
                )
                Text(
                    text = appointment.dateTime.month.name.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.Primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet?.name ?: "Pet",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = VetCareColors.OnSurface
                )
                Text(
                    text = appointment.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = VetCareColors.MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${appointment.dateTime.format(timeFormatter)} • ${vet?.name ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = VetCareColors.MutedText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OwnerHomeScreenPreview() {
    VetCareTheme {
        OwnerHomeScreen()
    }
}

