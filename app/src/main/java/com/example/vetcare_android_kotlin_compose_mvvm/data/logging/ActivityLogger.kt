package com.example.vetcare_android_kotlin_compose_mvvm.data.logging

import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.ActivityEvent
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Servicio para registrar actividades de usuario con persistencia Room
 *
 * Implementación de procesamiento asincrónico:
 * - Usa CoroutineScope con SupervisorJob para operaciones fire-and-forget
 * - Dispatchers.IO para escritura en Room Database
 * - No bloquea el hilo principal al registrar eventos
 * - Manejo de errores silencioso para no afectar UX
 */
object ActivityLogger {

    // Scope dedicado para logging asincrónico
    // SupervisorJob permite que fallos individuales no cancelen otros logs
    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Repositorio lazy para evitar inicialización temprana
    private val repository: VetCareRepository
        get() = VetCareApplication.getRepository()

    /**
     * Registrar un evento de actividad de forma asincrónica
     *
     * Ejecuta la escritura en Room Database sin bloquear la UI
     * El evento se persiste incluso si la app se cierra
     */
    fun log(
        screen: String,
        action: String,
        metadata: Map<String, String>? = null
    ) {
        val userId = SessionManager.getUserId() ?: "anonymous"
        val userRole = SessionManager.getCurrentUser()?.role?.name ?: "UNKNOWN"

        // Enriquecer metadata con rol de usuario
        val enrichedMetadata = mutableMapOf<String, String>()
        enrichedMetadata["role"] = userRole
        metadata?.let { enrichedMetadata.putAll(it) }

        val event = ActivityEvent(
            id = repository.generateId("event"),
            timestamp = LocalDateTime.now(),
            userId = userId,
            screen = screen,
            action = action,
            metadata = enrichedMetadata.ifEmpty { null }
        )

        // Fire-and-forget: Log asincrónico sin bloquear UI
        loggerScope.launch {
            try {
                repository.logActivity(event)
            } catch (e: Exception) {
                // Silenciar errores de logging para no afectar UX
                // En producción, podría enviarse a un sistema de monitoreo
            }
        }
    }

    /**
     * Log de navegación a pantalla
     */
    fun logNavigation(screen: String) {
        log(screen = screen, action = Actions.NAVIGATE)
    }

    /**
     * Log de CRUD con entityId
     */
    fun logCrud(
        screen: String,
        action: String,
        entityType: String,
        entityId: String,
        entityName: String? = null
    ) {
        val metadata = mutableMapOf(
            "entityType" to entityType,
            "entityId" to entityId
        )
        entityName?.let { metadata["entityName"] = it }
        log(screen = screen, action = action, metadata = metadata)
    }

    /**
     * Log de Login exitoso
     */
    fun logLogin(userEmail: String) {
        log(
            screen = Screens.LOGIN,
            action = Actions.LOGIN,
            metadata = mapOf("email" to userEmail)
        )
    }

    /**
     * Log de Logout
     */
    fun logLogout() {
        log(
            screen = Screens.SETTINGS,
            action = Actions.LOGOUT
        )
    }

    /**
     * Acciones predefinidas
     */
    object Actions {
        const val VIEW = "VIEW"
        const val CREATE = "CREATE"
        const val UPDATE = "UPDATE"
        const val DELETE = "DELETE"
        const val CANCEL = "CANCEL"
        const val CLICK = "CLICK"
        const val SEARCH = "SEARCH"
        const val FILTER = "FILTER"
        const val LOGIN = "LOGIN"
        const val LOGOUT = "LOGOUT"
        const val NAVIGATE = "NAVIGATE"
        const val EXPORT = "EXPORT"
        const val CLEAR = "CLEAR"
    }

    /**
     * Pantallas predefinidas
     */
    object Screens {
        const val LOGIN = "Login"
        const val ONBOARDING = "Onboarding"
        const val SETTINGS = "Settings"
        const val HOME_ADMIN = "HomeAdmin"
        const val HOME_OWNER = "HomeOwner"
        const val PETS_LIST = "PetsList"
        const val PET_DETAIL = "PetDetail"
        const val PET_FORM = "PetForm"
        const val APPOINTMENTS_LIST = "AppointmentsList"
        const val APPOINTMENT_FORM = "AppointmentForm"
        const val VETS_LIST = "VetsList"
        const val VET_FORM = "VetForm"
        const val VETERINARIANS = "Veterinarians"
        const val CONSULTATION_FORM = "ConsultationForm"
        const val VACCINE_FORM = "VaccineForm"
        const val DISCOVER = "Discover"
        const val ACTIVITY_LOG = "ActivityLog"
    }

    /**
     * Tipos de entidad para metadata
     */
    object EntityTypes {
        const val PET = "Pet"
        const val APPOINTMENT = "Appointment"
        const val CONSULTATION = "Consultation"
        const val VACCINE = "Vaccine"
        const val VETERINARIAN = "Veterinarian"
    }
}
