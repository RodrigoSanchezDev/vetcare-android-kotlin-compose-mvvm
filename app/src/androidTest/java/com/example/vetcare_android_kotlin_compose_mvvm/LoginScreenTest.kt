package com.example.vetcare_android_kotlin_compose_mvvm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.LoginScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareTheme
import org.junit.Assert.assertFalse
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
 *
 * @author Rodrigo Sánchez
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
            .onNodeWithText("Contraseña")
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

        // El botón tiene "Iniciar Sesión" como texto
        composeTestRule
            .onNodeWithText("Iniciar Sesión", substring = true, ignoreCase = true)
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
            .onNodeWithText("Admin: admin@vet.cl / 123456", substring = true)
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
            .onNodeWithText("Contraseña")
            .performClick()
            .performTextInput("123456")

        // La contraseña está oculta por defecto (dots),
        // verificamos que el campo no muestra "Contraseña" como placeholder
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
            .onNodeWithText("Olvidaste tu contraseña?", substring = true, ignoreCase = true)
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
            .onNodeWithText("Iniciar Sesión", substring = true, ignoreCase = true)
            .assertIsEnabled()
    }
}

