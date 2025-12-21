package com.example.vetcare_android_kotlin_compose_mvvm.data.logging

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.ActivityEvent
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.MockDataRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import java.time.LocalDateTime

/**
 * Servicio para registrar actividades de usuario
 * Implementa logging detallado con metadata para seguimiento en tiempo real
 */
object ActivityLogger {

    /**
     * Registrar un evento de actividad
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
            id = MockDataRepository.generateId("event"),
            timestamp = LocalDateTime.now(),
            userId = userId,
            screen = screen,
            action = action,
            metadata = enrichedMetadata.ifEmpty { null }
        )

        MockDataRepository.logActivity(event)
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
