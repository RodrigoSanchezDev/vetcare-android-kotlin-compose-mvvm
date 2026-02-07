package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.owner

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
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.ownerBottomNavItems
import com.example.vetcare_android_kotlin_compose_mvvm.ui.navigation.NavRoutes
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.appointments.AppointmentsListScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.discover.DiscoverScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.PetDetailScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.pets.PetsListScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.settings.SettingsScreen
import com.example.vetcare_android_kotlin_compose_mvvm.debug.DebugProfilingScreen

/**
 * Contenedor principal para el flujo de Owner con Bottom Navigation
 */
@Composable
fun OwnerMainScreen(
    rootNavController: NavHostController,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.OwnerHome.route
    val context = LocalContext.current

    // Determinar si mostrar bottom bar
    val showBottomBar = currentRoute in listOf(
        NavRoutes.OwnerHome.route,
        NavRoutes.OwnerPets.route,
        NavRoutes.OwnerAppointments.route,
        NavRoutes.OwnerDiscover.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                VetCareBottomBar(
                    items = ownerBottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        Log.d("OwnerNav", "OWNER onItemClick: label=${item.label} route=${item.route} currentRoute=$currentRoute")
                        if (item.route != currentRoute) {
                            navController.navigate(item.route) {
                                popUpTo(NavRoutes.OwnerHome.route) {
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
            startDestination = NavRoutes.OwnerHome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ========================================
            // HOME
            // ========================================
            composable(NavRoutes.OwnerHome.route) {
                OwnerHomeScreen(
                    onNavigateToPets = { navController.navigate(NavRoutes.OwnerPets.route) },
                    onNavigateToAppointments = { navController.navigate(NavRoutes.OwnerAppointments.route) },
                    onNavigateToPetDetail = { petId ->
                        navController.navigate("pet/$petId")
                    },
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
                    },
                    onNavigateToDebug = {
                        navController.navigate(NavRoutes.DebugProfiling.route)
                    }
                )
            }

            // ========================================
            // DEBUG & PROFILING
            // ========================================
            composable(NavRoutes.DebugProfiling.route) {
                DebugProfilingScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ========================================
            // MASCOTAS
            // ========================================
            composable(NavRoutes.OwnerPets.route) {
                PetsListScreen(
                    isAdmin = false,
                    onPetClick = { petId ->
                        navController.navigate("pet/$petId")
                    },
                    onAddPet = { /* Owner no puede agregar */ }
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
                    onEdit = { /* Owner no puede editar */ },
                    onAddConsultation = { /* Owner no puede */ },
                    onAddAppointment = { /* Owner no puede desde aquí */ },
                    onAddVaccine = { /* Owner no puede */ }
                )
            }

            // ========================================
            // CITAS
            // ========================================
            composable(NavRoutes.OwnerAppointments.route) {
                AppointmentsListScreen(
                    isAdmin = false,
                    onAddAppointment = { /* Owner no crea citas directamente */ },
                    onAppointmentClick = { /* Ver detalle */ },
                    onBack = {
                        navController.navigate(NavRoutes.OwnerHome.route) {
                            popUpTo(NavRoutes.OwnerHome.route) { inclusive = false }
                        }
                    }
                )
            }

            // ========================================
            // DISCOVER
            // ========================================
            composable(NavRoutes.OwnerDiscover.route) {
                DiscoverScreen()
            }
        }
    }
}

