package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vetcare_android_kotlin_compose_mvvm.notifications.ReminderScheduler
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.VetCareBottomBar
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.adminBottomNavItems
import com.example.vetcare_android_kotlin_compose_mvvm.ui.navigation.NavRoutes
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.activity.ActivityLogScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments.AppointmentFormScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments.AppointmentsListScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.consultations.ConsultationFormScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.PetDetailScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.PetFormScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.PetsListScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.VaccineFormScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.settings.SettingsScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians.VetFormScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.veterinarians.VeterinariansListScreen

/**
 * Contenedor principal para el flujo de Admin con Bottom Navigation
 */
@Composable
fun AdminMainScreen(
    rootNavController: NavHostController,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.AdminHome.route
    val context = LocalContext.current

    // Programar recordatorios al entrar a la pantalla principal
    LaunchedEffect(Unit) {
        ReminderScheduler.scheduleAllReminders(context)
    }

    // Determinar si mostrar bottom bar (ocultarla en pantallas de detalle/formulario)
    val showBottomBar = currentRoute in listOf(
        NavRoutes.AdminHome.route,
        NavRoutes.AdminPets.route,
        NavRoutes.AdminAppointments.route,
        NavRoutes.AdminVeterinarians.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                VetCareBottomBar(
                    items = adminBottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        Log.d("AdminNav", "ADMIN onItemClick: label=${item.label} route=${item.route} currentRoute=$currentRoute")
                        if (item.route != currentRoute) {
                            navController.navigate(item.route) {
                                popUpTo(NavRoutes.AdminHome.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.AdminHome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ========================================
            // HOME
            // ========================================
            composable(NavRoutes.AdminHome.route) {
                AdminHomeScreen(
                    onNavigateToPets = { navController.navigate(NavRoutes.AdminPets.route) },
                    onNavigateToAppointments = { navController.navigate(NavRoutes.AdminAppointments.route) },
                    onNavigateToVets = { navController.navigate(NavRoutes.AdminVeterinarians.route) },
                    onNavigateToActivityLog = { navController.navigate("activity_log") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }

            // ========================================
            // SETTINGS
            // ========================================
            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        // Cancelar recordatorios programados
                        ReminderScheduler.cancelAllReminders(context)
                        // Ejecutar logout
                        onLogout()
                    }
                )
            }

            // ========================================
            // MASCOTAS
            // ========================================
            composable(NavRoutes.AdminPets.route) {
                PetsListScreen(
                    isAdmin = true,
                    onPetClick = { petId ->
                        navController.navigate("pet/$petId")
                    },
                    onAddPet = {
                        navController.navigate("pet/form")
                    }
                )
            }

            composable(
                route = "pet/{petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                PetDetailScreen(
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate("pet/form?petId=$id") },
                    onAddConsultation = { id ->
                        navController.navigate("consultation/form/$id")
                    },
                    onAddAppointment = { id ->
                        navController.navigate("appointment/form?petId=$id")
                    },
                    onAddVaccine = { id ->
                        navController.navigate("vaccine/form/$id")
                    }
                )
            }

            composable(
                route = "pet/form?petId={petId}",
                arguments = listOf(navArgument("petId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                PetFormScreen(
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ========================================
            // CONSULTAS
            // ========================================
            composable(
                route = "consultation/form/{petId}?consultationId={consultationId}",
                arguments = listOf(
                    navArgument("petId") { type = NavType.StringType },
                    navArgument("consultationId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                val consultationId = backStackEntry.arguments?.getString("consultationId")
                ConsultationFormScreen(
                    petId = petId,
                    consultationId = consultationId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ========================================
            // VACUNAS
            // ========================================
            composable(
                route = "vaccine/form/{petId}?vaccineId={vaccineId}",
                arguments = listOf(
                    navArgument("petId") { type = NavType.StringType },
                    navArgument("vaccineId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                val vaccineId = backStackEntry.arguments?.getString("vaccineId")
                VaccineFormScreen(
                    petId = petId,
                    vaccineId = vaccineId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ========================================
            // CITAS
            // ========================================
            composable(NavRoutes.AdminAppointments.route) {
                AppointmentsListScreen(
                    isAdmin = true,
                    onAddAppointment = {
                        navController.navigate("appointment/form")
                    },
                    onAppointmentClick = { appointmentId ->
                        // Ver detalle si se necesita
                    },
                    onBack = {
                        navController.navigate(NavRoutes.AdminHome.route) {
                            popUpTo(NavRoutes.AdminHome.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = "appointment/form?petId={petId}&appointmentId={appointmentId}",
                arguments = listOf(
                    navArgument("petId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("appointmentId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                val appointmentId = backStackEntry.arguments?.getString("appointmentId")
                AppointmentFormScreen(
                    appointmentId = appointmentId,
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ========================================
            // VETERINARIOS
            // ========================================
            composable(NavRoutes.AdminVeterinarians.route) {
                VeterinariansListScreen(
                    isAdmin = true,
                    onVetClick = { vetId ->
                        // Ver detalle si se necesita
                    },
                    onAddVet = {
                        navController.navigate("vet/form")
                    },
                    onEditVet = { vetId ->
                        navController.navigate("vet/form?vetId=$vetId")
                    }
                )
            }

            composable(
                route = "vet/form?vetId={vetId}",
                arguments = listOf(
                    navArgument("vetId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val vetId = backStackEntry.arguments?.getString("vetId")
                VetFormScreen(
                    vetId = vetId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ========================================
            // ACTIVITY LOG
            // ========================================
            composable("activity_log") {
                ActivityLogScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

