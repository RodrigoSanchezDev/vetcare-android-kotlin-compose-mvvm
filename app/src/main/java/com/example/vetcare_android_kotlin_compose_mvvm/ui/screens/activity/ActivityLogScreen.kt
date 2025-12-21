package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.ActivityEvent
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import java.time.format.DateTimeFormatter

/**
 * Pantalla de registro de actividades con filtros avanzados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogScreen(
    viewModel: ActivityLogViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showScreenFilter by remember { mutableStateOf(false) }
    var showActionFilter by remember { mutableStateOf(false) }
    var showUserFilter by remember { mutableStateOf(false) }
    var showDateFilter by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    // Mostrar snackbar
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Diálogo de confirmación para limpiar
    if (uiState.showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.hideClearConfirmation() },
            title = { Text("Limpiar registro") },
            text = { Text("¿Estás seguro de que deseas eliminar todos los eventos del registro? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearLog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = VetCareColors.Danger)
                ) {
                    Text("Eliminar todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideClearConfirmation() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de exportación
    if (uiState.exportText != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearExportText() },
            title = { Text("Exportar registro") },
            text = {
                Column {
                    Text("Se han preparado ${uiState.filteredEvents.size} eventos para exportar.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Puedes copiar al portapapeles o compartir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetCareColors.MutedText
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VetCare Log", uiState.exportText))
                            viewModel.clearExportText()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar")
                    }
                    TextButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, uiState.exportText)
                                putExtra(Intent.EXTRA_SUBJECT, "VetCare Activity Log")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Compartir registro"))
                            viewModel.clearExportText()
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearExportText() }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMoreOptions = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showMoreOptions,
                            onDismissRequest = { showMoreOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Exportar") },
                                leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                                onClick = {
                                    viewModel.exportLog()
                                    showMoreOptions = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Actualizar") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = {
                                    viewModel.refresh()
                                    showMoreOptions = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Limpiar todo", color = VetCareColors.Danger) },
                                leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = VetCareColors.Danger) },
                                onClick = {
                                    viewModel.showClearConfirmation()
                                    showMoreOptions = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VetCareColors.Background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(VetCareColors.Background)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = uiState.filters.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.xs),
                placeholder = { Text("Buscar en eventos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.filters.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetCareColors.Primary,
                    unfocusedBorderColor = VetCareColors.SurfaceVariant
                ),
                shape = VetCareShapes.medium
            )

            // Header con contador
            Row(
                modifier = Modifier.padding(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.filteredEvents.size} eventos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VetCareColors.MutedText
                )
                if (uiState.filteredEvents.size != uiState.events.size) {
                    Text(
                        text = " de ${uiState.events.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetCareColors.MutedText
                    )
                }
            }

            // Filtros
            LazyRow(
                modifier = Modifier.padding(vertical = VetCareSpacing.xs),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
                contentPadding = PaddingValues(horizontal = VetCareSpacing.md)
            ) {
                // Filtro por fecha
                item {
                    Box {
                        FilterChip(
                            selected = uiState.filters.dateRangeFilter != DateRangeFilter.ALL,
                            onClick = { showDateFilter = true },
                            label = { Text(uiState.filters.dateRangeFilter.label) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                                selectedLabelColor = VetCareColors.Primary
                            )
                        )
                        DropdownMenu(expanded = showDateFilter, onDismissRequest = { showDateFilter = false }) {
                            DateRangeFilter.entries.forEach { dateRange ->
                                DropdownMenuItem(
                                    text = { Text(dateRange.label) },
                                    onClick = { viewModel.updateDateRangeFilter(dateRange); showDateFilter = false },
                                    trailingIcon = {
                                        if (uiState.filters.dateRangeFilter == dateRange) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = VetCareColors.Primary)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Filtro por pantalla
                item {
                    Box {
                        FilterChip(
                            selected = uiState.filters.screenFilter != null,
                            onClick = { showScreenFilter = true },
                            label = { Text(uiState.filters.screenFilter ?: "Pantalla") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                                selectedLabelColor = VetCareColors.Primary
                            )
                        )
                        DropdownMenu(expanded = showScreenFilter, onDismissRequest = { showScreenFilter = false }) {
                            DropdownMenuItem(text = { Text("Todas") }, onClick = { viewModel.updateScreenFilter(null); showScreenFilter = false })
                            uiState.availableScreens.forEach { screen ->
                                DropdownMenuItem(text = { Text(screen) }, onClick = { viewModel.updateScreenFilter(screen); showScreenFilter = false })
                            }
                        }
                    }
                }

                // Filtro por acción
                item {
                    Box {
                        FilterChip(
                            selected = uiState.filters.actionFilter != null,
                            onClick = { showActionFilter = true },
                            label = { Text(uiState.filters.actionFilter ?: "Acción") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                                selectedLabelColor = VetCareColors.Primary
                            )
                        )
                        DropdownMenu(expanded = showActionFilter, onDismissRequest = { showActionFilter = false }) {
                            DropdownMenuItem(text = { Text("Todas") }, onClick = { viewModel.updateActionFilter(null); showActionFilter = false })
                            uiState.availableActions.forEach { action ->
                                DropdownMenuItem(text = { Text(action) }, onClick = { viewModel.updateActionFilter(action); showActionFilter = false })
                            }
                        }
                    }
                }

                // Filtro por usuario
                if (uiState.availableUsers.isNotEmpty()) {
                    item {
                        Box {
                            FilterChip(
                                selected = uiState.filters.userFilter != null,
                                onClick = { showUserFilter = true },
                                label = { Text(uiState.availableUsers.find { it.first == uiState.filters.userFilter }?.second ?: "Usuario") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VetCareColors.Primary.copy(alpha = 0.2f),
                                    selectedLabelColor = VetCareColors.Primary
                                )
                            )
                            DropdownMenu(expanded = showUserFilter, onDismissRequest = { showUserFilter = false }) {
                                DropdownMenuItem(text = { Text("Todos") }, onClick = { viewModel.updateUserFilter(null); showUserFilter = false })
                                uiState.availableUsers.forEach { (userId, userName) ->
                                    DropdownMenuItem(text = { Text(userName) }, onClick = { viewModel.updateUserFilter(userId); showUserFilter = false })
                                }
                            }
                        }
                    }
                }

                // Limpiar filtros
                if (uiState.filters.screenFilter != null || uiState.filters.actionFilter != null ||
                    uiState.filters.userFilter != null || uiState.filters.dateRangeFilter != DateRangeFilter.ALL ||
                    uiState.filters.searchQuery.isNotBlank()
                ) {
                    item {
                        AssistChip(
                            onClick = { viewModel.clearFilters() },
                            label = { Text("Limpiar") },
                            leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.xs))

            // Lista de eventos
            AnimatedVisibility(visible = !uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
                if (uiState.filteredEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = VetCareColors.MutedText)
                            Text(
                                text = if (uiState.events.isEmpty()) "No hay eventos registrados" else "No hay eventos que coincidan",
                                style = MaterialTheme.typography.titleMedium,
                                color = VetCareColors.OnBackground
                            )
                            if (uiState.events.isNotEmpty()) {
                                TextButton(onClick = { viewModel.clearFilters() }) { Text("Limpiar filtros") }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = VetCareSpacing.md, vertical = VetCareSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                    ) {
                        items(items = uiState.filteredEvents, key = { it.id }) { event ->
                            ActivityEventItem(event = event)
                        }
                        item { Spacer(modifier = Modifier.height(VetCareSpacing.xxl)) }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VetCareColors.Primary)
                }
            }
        }
    }
}

@Composable
private fun ActivityEventItem(event: ActivityEvent, modifier: Modifier = Modifier) {
    val user = MockDataRepository.users.find { it.id == event.userId }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    val actionIcon = when (event.action) {
        "VIEW" -> Icons.Default.Visibility
        "CREATE" -> Icons.Default.Add
        "UPDATE" -> Icons.Default.Edit
        "DELETE" -> Icons.Default.Delete
        "CANCEL" -> Icons.Default.Cancel
        "CLICK" -> Icons.Default.TouchApp
        "SEARCH" -> Icons.Default.Search
        "FILTER" -> Icons.Default.FilterList
        "LOGIN" -> Icons.AutoMirrored.Filled.Login
        "LOGOUT" -> Icons.AutoMirrored.Filled.Logout
        "NAVIGATE" -> Icons.Default.Navigation
        "EXPORT" -> Icons.Default.FileDownload
        "CLEAR" -> Icons.Default.DeleteSweep
        else -> Icons.Default.Info
    }

    val actionColor = when (event.action) {
        "CREATE" -> VetCareColors.Success
        "DELETE", "CANCEL", "CLEAR" -> VetCareColors.Danger
        "LOGIN" -> VetCareColors.Primary
        "UPDATE" -> VetCareColors.Accent
        "LOGOUT" -> VetCareColors.Accent
        else -> VetCareColors.MutedText
    }

    SoftCard(modifier = modifier.fillMaxWidth().animateContentSize()) {
        Row(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
        ) {
            Surface(color = actionColor.copy(alpha = 0.1f), shape = VetCareShapes.small) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = event.action,
                    tint = actionColor,
                    modifier = Modifier.padding(VetCareSpacing.xs).size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = event.action, style = MaterialTheme.typography.labelMedium, color = actionColor)
                    Text(text = "•", color = VetCareColors.MutedText)
                    Text(text = event.screen, style = MaterialTheme.typography.bodyMedium, color = VetCareColors.OnSurface)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user?.name ?: "Usuario desconocido", style = MaterialTheme.typography.bodySmall, color = VetCareColors.MutedText)
                    event.metadata?.get("role")?.let { role ->
                        Surface(
                            color = if (role == "ADMIN") VetCareColors.Primary.copy(alpha = 0.1f) else VetCareColors.Accent.copy(alpha = 0.1f),
                            shape = VetCareShapes.small
                        ) {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (role == "ADMIN") VetCareColors.Primary else VetCareColors.Accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(text = event.timestamp.format(dateFormatter), style = MaterialTheme.typography.labelSmall, color = VetCareColors.MutedText)

                val additionalMetadata = event.metadata?.filterKeys { it != "role" }
                if (!additionalMetadata.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(VetCareSpacing.xxs))
                    Surface(color = VetCareColors.SurfaceVariant, shape = VetCareShapes.small) {
                        Text(
                            text = additionalMetadata.entries.joinToString(", ") { "${it.key}: ${it.value}" },
                            style = MaterialTheme.typography.labelSmall,
                            color = VetCareColors.MutedText,
                            modifier = Modifier.padding(VetCareSpacing.xs)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityLogScreenPreview() {
    VetCareTheme {
        ActivityLogScreen()
    }
}

