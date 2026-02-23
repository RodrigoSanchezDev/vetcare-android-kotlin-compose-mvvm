package com.example.vetcare_android_kotlin_compose_mvvm

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentados del Flujo de Login/Autenticación
 *
 * Verifica las interacciones de usuario en el flujo de autenticación:
 * - Navegación desde Onboarding hacia Login
 * - Validaciones del formulario de Login (email, contraseña)
 * - Login exitoso con credenciales de Admin
 * - Navegación hacia la pantalla de recuperar contraseña
 *
 * Compatible con emulador Pixel 4 API 36.1
 */
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    /**
     * Limpia el estado de sesión antes de cada test
     * para evitar interferencias entre pruebas
     */
    @Before
    fun setUp() {
        SessionManager.logout()
    }

    // ============================================
    // HELPERS
    // ============================================

    /**
     * Navega desde Onboarding hasta la pantalla de Login
     * Hace click en el botón circular de continuar y espera a que aparezca Login
     */
    private fun navigateToLogin() {
        // Esperar a que el onboarding esté cargado
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("onboarding_continue_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click en el botón de continuar del onboarding
        composeRule.onNodeWithTag("onboarding_continue_button").performClick()

        // Esperar a que la pantalla de login aparezca (campo Email visible)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Email", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Esperar a que Room termine de insertar los datos seed (populateDatabase es async)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("admin@vet.cl", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        Thread.sleep(1500)
    }

    // ============================================
    // TESTS
    // ============================================

    /**
     * Test 1: Verifica que al presionar el botón de continuar en Onboarding
     * se navega correctamente a la pantalla de Login
     */
    @Test
    fun test_onboardingToLoginNavigation() {
        // GIVEN: La app inicia en la pantalla de Onboarding
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("onboarding_continue_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // WHEN: El usuario presiona el botón de continuar
        composeRule.onNodeWithTag("onboarding_continue_button").performClick()

        // THEN: Se muestra la pantalla de Login con los elementos esperados
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("VetCare")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("VetCare").assertIsDisplayed()
        composeRule.onNodeWithText("Email", substring = true).assertExists()
    }

    /**
     * Test 2: Verifica que al intentar login con campos vacíos
     * se muestra error de validación en email
     */
    @Test
    fun test_emptyFieldsShowEmailError() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Presiona el botón de login sin llenar ningún campo
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // THEN: Se muestra el error de email requerido
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("El email es requerido")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("El email es requerido").assertIsDisplayed()
    }

    /**
     * Test 3: Verifica que al ingresar un email con formato inválido
     * se muestra el error de formato
     */
    @Test
    fun test_invalidEmailFormatShowsError() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Ingresa un email con formato inválido y presiona login
        composeRule.onNodeWithText("Email", substring = true).performTextInput("abc")
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // THEN: Se muestra el error de formato de email inválido
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("Formato de email", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Formato de email", substring = true).assertIsDisplayed()
    }

    /**
     * Test 4: Verifica que al ingresar un email válido pero dejar la contraseña vacía
     * se muestra error de contraseña requerida
     */
    @Test
    fun test_emptyPasswordShowsError() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Ingresa email válido pero no contraseña y presiona login
        composeRule.onNodeWithText("Email", substring = true).performTextInput("admin@vet.cl")
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // THEN: Se muestra error de contraseña requerida
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("es requerida", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("es requerida", substring = true).assertIsDisplayed()
    }

    /**
     * Test 5: Verifica que al ingresar una contraseña menor a 6 caracteres
     * se muestra error de longitud mínima
     */
    @Test
    fun test_shortPasswordShowsError() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Ingresa email válido y contraseña corta
        composeRule.onNodeWithText("Email", substring = true).performTextInput("admin@vet.cl")
        composeRule.onNodeWithText("Contrase", substring = true).performTextInput("123")
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // THEN: Se muestra error de longitud mínima
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("al menos 6 caracteres", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("al menos 6 caracteres", substring = true).assertIsDisplayed()
    }

    /**
     * Test 6: Verifica que el login exitoso como Admin navega al dashboard
     * con el mensaje de bienvenida "¡Hola, Admin!"
     */
    @Test
    fun test_successfulAdminLoginNavigatesToDashboard() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Ingresa credenciales válidas de admin y presiona login
        composeRule.onNodeWithText("Email", substring = true).performTextInput("admin@vet.cl")
        composeRule.onNodeWithText("Contrase", substring = true).performTextInput("123456")
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // THEN: Se navega al dashboard de Admin (tiene delay de 1s simulado)
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithText("Hola, Admin", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Hola, Admin", substring = true).assertIsDisplayed()
    }

    /**
     * Test 7: Verifica que al presionar "¿Olvidaste tu contraseña?"
     * se navega a la pantalla de recuperar contraseña
     */
    @Test
    fun test_forgotPasswordNavigation() {
        // GIVEN: El usuario está en la pantalla de Login
        navigateToLogin()

        // WHEN: Presiona el enlace de "¿Olvidaste tu contraseña?"
        composeRule.onNodeWithText("Olvidaste tu contrase", substring = true).performClick()

        // THEN: Se navega a la pantalla de recuperar contraseña
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Recuperar Contrase", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Recuperar Contrase", substring = true).assertIsDisplayed()
    }
}

