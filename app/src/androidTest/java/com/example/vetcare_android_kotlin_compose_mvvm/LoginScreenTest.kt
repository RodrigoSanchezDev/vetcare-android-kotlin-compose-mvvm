package com.example.vetcare_android_kotlin_compose_mvvm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.LoginScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas funcionales para la pantalla de Login
 *
 * Verifica las interacciones del usuario con el formulario de autenticación:
 * - Renderizado correcto de componentes UI
 * - Ingreso de datos en campos de texto
 * - Validación visual de formularios
 * - Navegación hacia recuperación de contraseña
 *
 * Framework: Compose UI Testing (equivalente a Espresso para Jetpack Compose)
 * Usa createAndroidComposeRule<MainActivity> para inicializar VetCareApplication
 * y el repositorio Room que requiere el AuthViewModel.
 *
 * @author Rodrigo Sánchez
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ════════════════════════════════════════════════════════════════
    // TESTS DE RENDERIZADO DE COMPONENTES
    // ════════════════════════════════════════════════════════════════

    @Test
    fun loginScreen_displaysVetCareLogo() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("VetCare")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysSubtitle() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ingresa a tu cuenta VetCare")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysEmailField() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Email")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysPasswordField() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Contraseña", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysLoginButton() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Iniciar Sesi", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysTestCredentials() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Credenciales de prueba:", substring = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("admin@vet.cl", substring = true)
            .assertIsDisplayed()
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE INGRESO DE DATOS
    // ════════════════════════════════════════════════════════════════

    @Test
    fun loginScreen_canTypeEmail() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Email")
            .performClick()
            .performTextInput("admin@vet.cl")

        composeTestRule
            .onNodeWithText("admin@vet.cl")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_canTypePassword() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Contraseña", substring = true, ignoreCase = true)
            .performClick()
            .performTextInput("123456")

        // La contraseña está oculta por defecto (dots),
        // verificamos que el campo aceptó el input sin crash
        composeTestRule.waitForIdle()
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE NAVEGACIÓN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun loginScreen_forgotPasswordCallbackTriggered() {
        var forgotPasswordClicked = false

        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = { forgotPasswordClicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Olvidaste tu contrase", substring = true, ignoreCase = true)
            .performClick()

        assertTrue("Forgot password callback should be triggered", forgotPasswordClicked)
    }

    @Test
    fun loginScreen_loginButtonIsClickable() {
        composeTestRule.setContent {
            VetCareTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Iniciar Sesi", substring = true, ignoreCase = true)
            .assertIsEnabled()
    }
}
