package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.ActivityEvent
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Rango de fechas para filtro
 */
enum class DateRangeFilter(val label: String, val days: Int?) {
    ALL("Todas", null),
    TODAY("Hoy", 0),
    LAST_7_DAYS("Últimos 7 días", 7),
    LAST_30_DAYS("Últimos 30 días", 30)
}

/**
 * Filtros para Activity Log
 */
data class ActivityFilters(
    val screenFilter: String? = null,
    val actionFilter: String? = null,
    val userFilter: String? = null,
    val dateRangeFilter: DateRangeFilter = DateRangeFilter.ALL,
    val searchQuery: String = ""
)

/**
 * Estado UI para Activity Log
 */
data class ActivityLogUiState(
    val events: List<ActivityEvent> = emptyList(),
    val filteredEvents: List<ActivityEvent> = emptyList(),
    val isLoading: Boolean = false,
    val filters: ActivityFilters = ActivityFilters(),
    val availableScreens: List<String> = emptyList(),
    val availableActions: List<String> = emptyList(),
    val availableUsers: List<Pair<String, String>> = emptyList(), // userId to userName
    val exportText: String? = null,
    val showClearConfirmation: Boolean = false,
    val snackbarMessage: String? = null
)

/**
 * ViewModel para Activity Log con procesamiento asincrónico optimizado
 *
 * Implementación de Kotlin Coroutines:
 * - Flow reactivo desde Room Database para actualizaciones en tiempo real
 * - Dispatchers.Default para filtrado y procesamiento de listas grandes
 * - Dispatchers.IO para operaciones de limpieza de BD
 * - Debounce en búsqueda para evitar consultas excesivas
 */
class ActivityLogViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _uiState = MutableStateFlow(ActivityLogUiState())
    val uiState: StateFlow<ActivityLogUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    // Cache de usuarios para evitar consultas repetidas
    private var usersCache: Map<String, String> = emptyMap()

    init {
        loadInitialData()
        // Log de navegación a esta pantalla
        ActivityLogger.logNavigation(ActivityLogger.Screens.ACTIVITY_LOG)
    }

    /**
     * Carga inicial de datos con procesamiento paralelo
     */
    private fun loadInitialData() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Cargar usuarios primero para el cache
                withContext(Dispatchers.IO) {
                    repository.getAllUsersFlow().collect { users ->
                        usersCache = users.associate { it.id to it.name }
                    }
                }

                // Suscribirse al Flow de eventos para actualizaciones en tiempo real
                repository.getAllActivityEventsFlow().collect { events ->
                    updateEvents(events)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    snackbarMessage = "Error al cargar eventos: ${e.message}"
                )
            }
        }
    }

    /**
     * Datos procesados para el estado UI
     */
    private data class ProcessedEventData(
        val sortedEvents: List<ActivityEvent>,
        val screens: List<String>,
        val actions: List<String>,
        val users: List<Pair<String, String>>
    )

    /**
     * Procesa los eventos recibidos del Flow de Room
     * El ordenamiento y extracción de filtros se realiza en Default dispatcher
     */
    private suspend fun updateEvents(events: List<ActivityEvent>) {
        val processedData: ProcessedEventData = withContext(Dispatchers.Default) {
            val sortedEvents = events.sortedByDescending { it.timestamp }

            // Extraer valores únicos para filtros
            val screens: List<String> = sortedEvents.map { it.screen }.distinct().sorted()
            val actions: List<String> = sortedEvents.map { it.action }.distinct().sorted()
            val users: List<Pair<String, String>> = sortedEvents.map { it.userId }.distinct().mapNotNull { userId ->
                usersCache[userId]?.let { userId to it }
            }.distinctBy { it.first }

            ProcessedEventData(sortedEvents, screens, actions, users)
        }

        _uiState.value = _uiState.value.copy(
            events = processedData.sortedEvents,
            availableScreens = processedData.screens,
            availableActions = processedData.actions,
            availableUsers = processedData.users,
            isLoading = false
        )
        applyFilters()
    }

    fun updateScreenFilter(screen: String?) {
        val newFilters = _uiState.value.filters.copy(screenFilter = screen)
        _uiState.value = _uiState.value.copy(filters = newFilters)
        applyFilters()
        if (screen != null) {
            ActivityLogger.log(
                screen = ActivityLogger.Screens.ACTIVITY_LOG,
                action = ActivityLogger.Actions.FILTER,
                metadata = mapOf("filterType" to "screen", "value" to screen)
            )
        }
    }

    fun updateActionFilter(action: String?) {
        val newFilters = _uiState.value.filters.copy(actionFilter = action)
        _uiState.value = _uiState.value.copy(filters = newFilters)
        applyFilters()
        if (action != null) {
            ActivityLogger.log(
                screen = ActivityLogger.Screens.ACTIVITY_LOG,
                action = ActivityLogger.Actions.FILTER,
                metadata = mapOf("filterType" to "action", "value" to action)
            )
        }
    }

    fun updateUserFilter(userId: String?) {
        val newFilters = _uiState.value.filters.copy(userFilter = userId)
        _uiState.value = _uiState.value.copy(filters = newFilters)
        applyFilters()
    }

    fun updateDateRangeFilter(dateRange: DateRangeFilter) {
        val newFilters = _uiState.value.filters.copy(dateRangeFilter = dateRange)
        _uiState.value = _uiState.value.copy(filters = newFilters)
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        val newFilters = _uiState.value.filters.copy(searchQuery = query)
        _uiState.value = _uiState.value.copy(filters = newFilters)

        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            applyFilters()
            if (query.isNotBlank()) {
                ActivityLogger.log(
                    screen = ActivityLogger.Screens.ACTIVITY_LOG,
                    action = ActivityLogger.Actions.SEARCH,
                    metadata = mapOf("query" to query)
                )
            }
        }
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            filters = ActivityFilters(),
            filteredEvents = _uiState.value.events
        )
    }

    private fun applyFilters() {
        val filters = _uiState.value.filters
        val filtered = _uiState.value.events.filter { event ->
            val matchesScreen = filters.screenFilter == null || event.screen == filters.screenFilter
            val matchesAction = filters.actionFilter == null || event.action == filters.actionFilter
            val matchesUser = filters.userFilter == null || event.userId == filters.userFilter
            val matchesDateRange = matchesDateRange(event.timestamp, filters.dateRangeFilter)
            val matchesSearch = filters.searchQuery.isBlank() || matchesSearchQuery(event, filters.searchQuery)

            matchesScreen && matchesAction && matchesUser && matchesDateRange && matchesSearch
        }
        _uiState.value = _uiState.value.copy(filteredEvents = filtered)
    }

    private fun matchesDateRange(timestamp: LocalDateTime, dateRange: DateRangeFilter): Boolean {
        return when (dateRange) {
            DateRangeFilter.ALL -> true
            DateRangeFilter.TODAY -> timestamp.toLocalDate() == LocalDate.now()
            DateRangeFilter.LAST_7_DAYS -> timestamp.isAfter(LocalDateTime.now().minusDays(7))
            DateRangeFilter.LAST_30_DAYS -> timestamp.isAfter(LocalDateTime.now().minusDays(30))
        }
    }

    private fun matchesSearchQuery(event: ActivityEvent, query: String): Boolean {
        val lowerQuery = query.lowercase()
        val userName = usersCache[event.userId] ?: ""

        return event.screen.lowercase().contains(lowerQuery) ||
                event.action.lowercase().contains(lowerQuery) ||
                userName.lowercase().contains(lowerQuery) ||
                event.metadata?.values?.any { it.lowercase().contains(lowerQuery) } == true
    }

    fun exportLog(): String {
        val events = _uiState.value.filteredEvents
        val sb = StringBuilder()
        sb.appendLine("=== VetCare Activity Log ===")
        sb.appendLine("Exportado: ${LocalDateTime.now().format(dateFormatter)}")
        sb.appendLine("Total eventos: ${events.size}")
        sb.appendLine("----------------------------")
        sb.appendLine()

        events.forEach { event ->
            val user = usersCache[event.userId] ?: "Desconocido"
            sb.appendLine("[${event.timestamp.format(dateFormatter)}]")
            sb.appendLine("  Usuario: $user")
            sb.appendLine("  Pantalla: ${event.screen}")
            sb.appendLine("  Acción: ${event.action}")
            if (!event.metadata.isNullOrEmpty()) {
                sb.appendLine("  Detalles: ${event.metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }}")
            }
            sb.appendLine()
        }

        val exportText = sb.toString()
        _uiState.value = _uiState.value.copy(exportText = exportText)

        ActivityLogger.log(
            screen = ActivityLogger.Screens.ACTIVITY_LOG,
            action = ActivityLogger.Actions.EXPORT,
            metadata = mapOf("eventCount" to events.size.toString())
        )

        return exportText
    }

    fun clearExportText() {
        _uiState.value = _uiState.value.copy(exportText = null)
    }

    fun showClearConfirmation() {
        _uiState.value = _uiState.value.copy(showClearConfirmation = true)
    }

    fun hideClearConfirmation() {
        _uiState.value = _uiState.value.copy(showClearConfirmation = false)
    }

    /**
     * Limpia el registro de actividad usando coroutines
     * La operación se ejecuta en IO dispatcher para no bloquear UI
     */
    fun clearLog() {
        ActivityLogger.log(
            screen = ActivityLogger.Screens.ACTIVITY_LOG,
            action = ActivityLogger.Actions.CLEAR,
            metadata = mapOf("clearedCount" to _uiState.value.events.size.toString())
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.clearActivityLog()
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    showClearConfirmation = false,
                    snackbarMessage = "Registro de actividad limpiado"
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    /**
     * Refresca los datos desde Room Database
     */
    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadInitialData()
    }
}
