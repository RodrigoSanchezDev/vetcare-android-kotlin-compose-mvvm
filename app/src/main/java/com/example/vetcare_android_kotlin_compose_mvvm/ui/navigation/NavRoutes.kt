package com.example.vetcare_android_kotlin_compose_mvvm.ui.navigation

/**
 * Rutas de navegación de la aplicación VetCare
 */
sealed class NavRoutes(val route: String) {
    // Auth Flow
    object Onboarding : NavRoutes("onboarding")
    object Login : NavRoutes("login")
    object ResetPassword : NavRoutes("reset_password")

    // Admin Flow
    object AdminHome : NavRoutes("admin/home")
    object AdminPets : NavRoutes("admin/pets")
    object AdminAppointments : NavRoutes("admin/appointments")
    object AdminVeterinarians : NavRoutes("admin/veterinarians")
    object AdminActivityLog : NavRoutes("admin/activity_log")

    // Owner Flow
    object OwnerHome : NavRoutes("owner/home")
    object OwnerPets : NavRoutes("owner/pets")
    object OwnerAppointments : NavRoutes("owner/appointments")
    object OwnerDiscover : NavRoutes("owner/discover")

    // Shared Screens (with arguments)
    object PetDetail : NavRoutes("pet/{petId}") {
        fun createRoute(petId: String) = "pet/$petId"
    }
    object PetForm : NavRoutes("pet/form?petId={petId}") {
        fun createRoute(petId: String? = null) =
            if (petId != null) "pet/form?petId=$petId" else "pet/form"
    }
    object ConsultationForm : NavRoutes("consultation/form/{petId}?consultationId={consultationId}") {
        fun createRoute(petId: String, consultationId: String? = null) =
            if (consultationId != null) "consultation/form/$petId?consultationId=$consultationId"
            else "consultation/form/$petId"
    }
    object AppointmentForm : NavRoutes("appointment/form?appointmentId={appointmentId}") {
        fun createRoute(appointmentId: String? = null) =
            if (appointmentId != null) "appointment/form?appointmentId=$appointmentId"
            else "appointment/form"
    }
    object VetAgenda : NavRoutes("vet/agenda/{vetId}") {
        fun createRoute(vetId: String) = "vet/agenda/$vetId"
    }
}

/**
 * Grupos de navegación para bottom bar
 */
object NavGroups {
    val adminTabs = listOf(
        NavRoutes.AdminHome,
        NavRoutes.AdminPets,
        NavRoutes.AdminAppointments,
        NavRoutes.AdminVeterinarians
    )

    val ownerTabs = listOf(
        NavRoutes.OwnerHome,
        NavRoutes.OwnerPets,
        NavRoutes.OwnerAppointments,
        NavRoutes.OwnerDiscover
    )
}

