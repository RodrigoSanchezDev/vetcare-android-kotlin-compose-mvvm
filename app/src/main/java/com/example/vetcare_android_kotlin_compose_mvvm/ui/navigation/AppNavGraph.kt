package com.example.vetcare_android_kotlin_compose_mvvm.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.AuthViewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.LoginScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.OnboardingScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.ResetPasswordScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.admin.AdminMainScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.owner.OwnerMainScreen

/**
 * NavHost principal de la aplicación VetCare
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Onboarding.route
) {
    // ViewModel compartido para auth
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        }
    ) {
        // ========================================
        // AUTH FLOW
        // ========================================
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onContinue = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        UserRole.ADMIN -> NavRoutes.AdminHome.route
                        UserRole.OWNER -> NavRoutes.OwnerHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(NavRoutes.ResetPassword.route)
                }
            )
        }

        composable(NavRoutes.ResetPassword.route) {
            ResetPasswordScreen(
                viewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ========================================
        // ADMIN FLOW (con Bottom Navigation interna)
        // ========================================
        composable(NavRoutes.AdminHome.route) {
            AdminMainScreen(
                rootNavController = navController,
                onLogout = {
                    SessionManager.logout()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ========================================
        // OWNER FLOW (con Bottom Navigation interna)
        // ========================================
        composable(NavRoutes.OwnerHome.route) {
            OwnerMainScreen(
                rootNavController = navController,
                onLogout = {
                    SessionManager.logout()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

