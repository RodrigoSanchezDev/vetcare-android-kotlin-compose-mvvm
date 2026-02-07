package com.example.vetcare_android_kotlin_compose_mvvm.debug

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

// Colores específicos para la pantalla de Debug
private val InfoColor = Color(0xFF2196F3)
private val WarningColor = Color(0xFFFF9800)

/**
 * Pantalla de Debug & Profiling - Herramientas de Diagnóstico
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugProfilingScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DebugPetDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPetId by remember { mutableStateOf("pet-001") }
    var expandedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = null,
                            tint = VetCareColors.Primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Debug & Profiling",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Performance Monitor",
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.MutedText
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = VetCareColors.OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Surface
                )
            )
        },
        containerColor = VetCareColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // QUICK ACTIONS
            item {
                QuickActionsSection(
                    petId = selectedPetId,
                    onPetIdChange = { selectedPetId = it },
                    isLoading = uiState.isLoading,
                    onRunTest = { viewModel.loadPetWithDebug(selectedPetId) },
                    onRefresh = { viewModel.refresh(selectedPetId) },
                    canRefresh = uiState.pet != null && !uiState.isRefreshing
                )
            }

            // ERROR DISPLAY
            if (uiState.error != null) {
                item {
                    ErrorCard(
                        error = uiState.error!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }

            // SUCCESS RESULT
            if (uiState.pet != null) {
                item {
                    SuccessResultCard(
                        pet = uiState.pet!!,
                        owner = uiState.owner,
                        consultationsCount = uiState.consultations.size,
                        appointmentsCount = uiState.appointments.size,
                        vaccinesCount = uiState.vaccines.size
                    )
                }
            }

            // PERFORMANCE METRICS
            if (uiState.debugInfo != null) {
                item {
                    PerformanceCard(debugInfo = uiState.debugInfo!!)
                }
            }

            // ERROR SIMULATION
            item {
                ExpandableSection(
                    title = "Simulación de Errores",
                    subtitle = "Probar manejo de excepciones",
                    icon = Icons.Default.Warning,
                    iconTint = WarningColor,
                    isExpanded = expandedSection == "errors",
                    onToggle = { expandedSection = if (expandedSection == "errors") null else "errors" }
                ) {
                    ErrorSimulationContent(
                        onSimulateError = { viewModel.simulateError(it) }
                    )
                }
            }

            // LOGCAT GUIDE
            item {
                ExpandableSection(
                    title = "Guía de Logcat",
                    subtitle = "Tags y filtros disponibles",
                    icon = Icons.Default.Terminal,
                    iconTint = InfoColor,
                    isExpanded = expandedSection == "logcat",
                    onToggle = { expandedSection = if (expandedSection == "logcat") null else "logcat" }
                ) {
                    LogcatGuideContent()
                }
            }

            // PROFILER GUIDE
            item {
                ExpandableSection(
                    title = "Guía del Profiler",
                    subtitle = "CPU y Memory Profiler",
                    icon = Icons.Default.Analytics,
                    iconTint = VetCareColors.Accent,
                    isExpanded = expandedSection == "profiler",
                    onToggle = { expandedSection = if (expandedSection == "profiler") null else "profiler" }
                ) {
                    ProfilerGuideContent()
                }
            }

            item { Spacer(modifier = Modifier.height(VetCareSpacing.xl)) }
        }
    }
}

@Composable
private fun QuickActionsSection(
    petId: String,
    onPetIdChange: (String) -> Unit,
    isLoading: Boolean,
    onRunTest: () -> Unit,
    onRefresh: () -> Unit,
    canRefresh: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = VetCareElevation.medium,
                shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
                ambientColor = VetCareColors.Primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Column(
            modifier = Modifier.padding(VetCareSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VetCareColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = VetCareColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        "Ejecutar Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = VetCareColors.OnSurface
                    )
                    Text(
                        "Analiza el flujo de carga de mascota",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetCareColors.MutedText
                    )
                }
            }

            HorizontalDivider(color = VetCareColors.Divider)

            OutlinedTextField(
                value = petId,
                onValueChange = onPetIdChange,
                label = { Text("ID de Mascota") },
                placeholder = { Text("ej: pet-001") },
                leadingIcon = {
                    Icon(Icons.Outlined.Pets, null, tint = VetCareColors.MutedText)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetCareColors.Primary,
                    unfocusedBorderColor = VetCareColors.Divider
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
            ) {
                Button(
                    onClick = onRunTest,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && petId.isNotBlank(),
                    shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
                    colors = ButtonDefaults.buttonColors(containerColor = VetCareColors.Primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = VetCareColors.OnPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isLoading) "Ejecutando..." else "Ejecutar Test")
                }

                OutlinedButton(
                    onClick = onRefresh,
                    enabled = canRefresh,
                    shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VetCareColors.Primary)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(error: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Danger.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, VetCareColors.Danger.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            Icon(Icons.Default.ErrorOutline, null, tint = VetCareColors.Danger, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Error Capturado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = VetCareColors.Danger)
                Spacer(modifier = Modifier.height(4.dp))
                Text(error, style = MaterialTheme.typography.bodySmall, color = VetCareColors.OnSurface)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, "Cerrar", tint = VetCareColors.MutedText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SuccessResultCard(
    pet: com.example.vetcare_android_kotlin_compose_mvvm.data.model.Pet,
    owner: com.example.vetcare_android_kotlin_compose_mvvm.data.model.Owner?,
    consultationsCount: Int,
    appointmentsCount: Int,
    vaccinesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Success.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, VetCareColors.Success.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(VetCareSpacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
                Icon(Icons.Default.CheckCircle, null, tint = VetCareColors.Success, modifier = Modifier.size(24.dp))
                Text("Carga Exitosa", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = VetCareColors.Success)
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.md))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
                InfoChip(Icons.Outlined.Pets, pet.name, Modifier.weight(1f))
                InfoChip(Icons.Outlined.Category, pet.species.displayName, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))
            InfoChip(Icons.Outlined.Person, owner?.fullName ?: "Cargando...", Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(VetCareSpacing.md))
            HorizontalDivider(color = VetCareColors.Success.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(VetCareSpacing.md))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("$consultationsCount", "Consultas")
                StatItem("$appointmentsCount", "Citas")
                StatItem("$vaccinesCount", "Vacunas")
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
        color = VetCareColors.Surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = VetCareSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = VetCareColors.MutedText)
            Text(label, style = MaterialTheme.typography.bodySmall, color = VetCareColors.OnSurface)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VetCareColors.Success)
        Text(label, style = MaterialTheme.typography.labelSmall, color = VetCareColors.MutedText)
    }
}

@Composable
private fun PerformanceCard(debugInfo: DebugInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Column(modifier = Modifier.padding(VetCareSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(InfoColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Speed, null, tint = InfoColor, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Métricas de Rendimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Tiempo total: ${debugInfo.totalLoadTimeMs}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (debugInfo.totalLoadTimeMs > 500) WarningColor else VetCareColors.Success
                    )
                }
                Surface(
                    shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
                    color = if (debugInfo.totalLoadTimeMs > 500) WarningColor.copy(alpha = 0.1f) else VetCareColors.Success.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (debugInfo.totalLoadTimeMs > 500) "Lento" else "Óptimo",
                        modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = VetCareSpacing.xs),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (debugInfo.totalLoadTimeMs > 500) WarningColor else VetCareColors.Success
                    )
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.lg))

            Column(verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
                MetricBar("Pet", debugInfo.petLoadTimeMs, debugInfo.totalLoadTimeMs)
                MetricBar("Owner", debugInfo.ownerLoadTimeMs, debugInfo.totalLoadTimeMs)
                MetricBar("Consultas", debugInfo.consultationsLoadTimeMs, debugInfo.totalLoadTimeMs)
                MetricBar("Citas", debugInfo.appointmentsLoadTimeMs, debugInfo.totalLoadTimeMs)
                MetricBar("Vacunas", debugInfo.vaccinesLoadTimeMs, debugInfo.totalLoadTimeMs)
                MetricBar("Filtrado", debugInfo.filterTimeMs, debugInfo.totalLoadTimeMs)
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.md))
            HorizontalDivider(color = VetCareColors.Divider)
            Spacer(modifier = Modifier.height(VetCareSpacing.md))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Fuente", style = MaterialTheme.typography.labelSmall, color = VetCareColors.MutedText)
                    Text(debugInfo.dataSource, style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Memoria", style = MaterialTheme.typography.labelSmall, color = VetCareColors.MutedText)
                    Text("${debugInfo.memoryUsageMB} MB", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun MetricBar(label: String, timeMs: Long, totalMs: Long) {
    val progress = if (totalMs > 0) (timeMs.toFloat() / totalMs).coerceIn(0f, 1f) else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText, modifier = Modifier.width(70.dp))
        Box(
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(VetCareColors.SurfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(VetCareColors.Primary, VetCareColors.Accent)))
            )
        }
        Text("${timeMs}ms", style = MaterialTheme.typography.labelSmall, color = VetCareColors.OnSurface, modifier = Modifier.width(45.dp), textAlign = TextAlign.End)
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = VetCareColors.Surface)
    ) {
        Column {
            Surface(onClick = onToggle, color = Color.Transparent) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(VetCareSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
                    }
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = VetCareColors.MutedText
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Column {
                    HorizontalDivider(color = VetCareColors.Divider)
                    Box(modifier = Modifier.padding(VetCareSpacing.md)) { content() }
                }
            }
        }
    }
}

@Composable
private fun ErrorSimulationContent(onSimulateError: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)) {
        Text("Simula diferentes errores para probar try-catch:", style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
            ErrorButton("DB Error", Icons.Outlined.Storage, { onSimulateError("database") }, Modifier.weight(1f))
            ErrorButton("Not Found", Icons.Outlined.SearchOff, { onSimulateError("notfound") }, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
            ErrorButton("Timeout", Icons.Outlined.Timer, { onSimulateError("timeout") }, Modifier.weight(1f))
            ErrorButton("Network", Icons.Outlined.WifiOff, { onSimulateError("network") }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ErrorButton(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarningColor.copy(alpha = 0.5f))
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun LogcatGuideContent() {
    Column(verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
        Text("Filtros disponibles:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        TagChip("VETCARE_DEBUG", "Todos los logs", VetCareColors.Primary)
        TagChip("VETCARE_PERF", "Métricas", InfoColor)
        TagChip("VETCARE_ERROR", "Errores", VetCareColors.Danger)
        TagChip("VETCARE_DB", "Base de datos", VetCareColors.Accent)
        Spacer(modifier = Modifier.height(VetCareSpacing.xs))
        Surface(shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius), color = VetCareColors.SurfaceVariant) {
            Text("💡 Filtrar por: tag:VETCARE_DEBUG", modifier = Modifier.padding(VetCareSpacing.sm), style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
        }
    }
}

@Composable
private fun TagChip(tag: String, description: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
        Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.1f)) {
            Text(tag, modifier = Modifier.padding(horizontal = VetCareSpacing.sm, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = color)
        }
        Text(description, style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
    }
}

@Composable
private fun ProfilerGuideContent() {
    Column(verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)) {
        ProfilerStep("1", "Abrir Profiler", "View → Tool Windows → Profiler")
        ProfilerStep("2", "Seleccionar proceso", "Elegir VetCare")
        ProfilerStep("3", "CPU Profiler", "Ver threads y operaciones")
        ProfilerStep("4", "Memory Profiler", "Detectar memory leaks")
    }
}

@Composable
private fun ProfilerStep(number: String, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(VetCareColors.Accent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Text(number, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = VetCareColors.Accent)
        }
        Column {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
        }
    }
}

