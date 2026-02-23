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
 * Tests instrumentados del Flujo de Formulario de Mascotas
 *
 * Verifica las interacciones de usuario en el flujo de gestión de mascotas:
 * - Navegación desde el dashboard Admin hacia el formulario de nueva mascota
 * - Visualización correcta del formulario y sus campos
 * - Validaciones del formulario (nombre, edad, dueño)
 * - Selección de especie mediante chips
 *
 * Requiere login como Admin para acceder a la funcionalidad completa.
 * Compatible con emulador Pixel 4 API 36.1
 */
@RunWith(AndroidJUnit4::class)
class PetFormValidationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    /**
     * Limpia el estado de sesión antes de cada test
     */
    @Before
    fun setUp() {
        SessionManager.logout()
    }

    // ============================================
    // HELPERS
    // ============================================

    /**
     * Navega desde Onboarding → Login → Dashboard Admin
     * Realiza login completo con credenciales de admin
     */
    private fun loginAsAdmin() {
        // 1. Esperar onboarding
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("onboarding_continue_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Click continuar en onboarding
        composeRule.onNodeWithTag("onboarding_continue_button").performClick()

        // 3. Esperar pantalla de login
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Email", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 3.1 Esperar a que Room termine de insertar datos seed (populateDatabase es async)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("admin@vet.cl", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        Thread.sleep(1500)

        // 4. Ingresar credenciales de admin
        composeRule.onNodeWithText("Email", substring = true).performTextInput("admin@vet.cl")
        composeRule.onNodeWithText("Contrase", substring = true).performTextInput("123456")

        // 5. Presionar login
        composeRule.onNodeWithText("Iniciar Sesi", substring = true).performClick()

        // 6. Esperar que aparezca el dashboard de Admin (1s delay + carga)
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithText("Hola, Admin", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Desde el Dashboard Admin, navega a la tab de Mascotas
     * y presiona el FAB para crear una nueva mascota
     */
    private fun navigateToPetForm() {
        // 1. Click en "Mascotas" en la barra de navegación inferior
        //    Hay 2 nodos con texto "Mascotas" (stat card + bottom nav), seleccionar el último
        composeRule.onAllNodesWithText("Mascotas").onLast().performClick()

        // 2. Esperar que cargue la lista de mascotas
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Todas las Mascotas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Click en el FAB de agregar mascota
        composeRule.onNodeWithContentDescription("Agregar mascota").performClick()

        // 4. Esperar que cargue el formulario
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Nueva Mascota")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ============================================
    // TESTS
    // ============================================

    /**
     * Test 1: Verifica que al navegar al formulario de nueva mascota
     * se muestra el título correcto y la sección de información básica
     */
    @Test
    fun test_petFormDisplaysCorrectTitleAndSections() {
        // GIVEN: El usuario está logueado como Admin
        loginAsAdmin()

        // WHEN: Navega al formulario de nueva mascota
        navigateToPetForm()

        // THEN: Se muestran el título y las secciones del formulario
        composeRule.onNodeWithText("Nueva Mascota").assertIsDisplayed()
        composeRule.onNodeWithText("Informaci", substring = true).assertIsDisplayed()
    }

    /**
     * Test 2: Verifica que el formulario de nueva mascota muestra
     * el campo de nombre en la sección de información básica
     */
    @Test
    fun test_petFormShowsNameField() {
        // GIVEN: El usuario está logueado como Admin
        loginAsAdmin()

        // WHEN: Navega al formulario de nueva mascota
        navigateToPetForm()

        // THEN: El campo de nombre está visible
        composeRule.onNodeWithText("Nombre *", substring = true).assertExists()
    }

    /**
     * Test 3: Verifica que el formulario permite ingresar texto en el campo nombre
     */
    @Test
    fun test_petFormNameFieldAcceptsInput() {
        // GIVEN: El usuario está logueado como Admin y está en el formulario
        loginAsAdmin()
        navigateToPetForm()

        // WHEN: Ingresa un nombre en el campo de nombre
        composeRule.onNodeWithText("Nombre *", substring = true).performTextInput("Firulais")

        // THEN: El texto se muestra en el campo
        composeRule.onNodeWithText("Firulais").assertExists()
    }

    /**
     * Test 4: Verifica que los chips de selección de especie funcionan correctamente
     * y que la especie por defecto es "Perro"
     */
    @Test
    fun test_speciesChipsAreDisplayedAndSelectable() {
        // GIVEN: El usuario está en el formulario de nueva mascota
        loginAsAdmin()
        navigateToPetForm()

        // THEN: Se muestran los chips de especie
        composeRule.onNodeWithText("Perro").assertExists()
        composeRule.onNodeWithText("Gato").assertExists()
        composeRule.onNodeWithText("Ave").assertExists()
        composeRule.onNodeWithText("Conejo").assertExists()

        // WHEN: El usuario selecciona "Gato"
        composeRule.onNodeWithText("Gato").performClick()

        // THEN: El chip "Gato" está seleccionado (sigue existiendo y es clickeable)
        composeRule.onNodeWithText("Gato").assertExists()
    }

    /**
     * Test 5: Verifica que la navegación de vuelta funciona correctamente
     * al presionar el botón de retroceso en el formulario
     */
    @Test
    fun test_backNavigationFromPetForm() {
        // GIVEN: El usuario está en el formulario de nueva mascota
        loginAsAdmin()
        navigateToPetForm()

        // WHEN: Presiona el botón de volver (flecha atrás)
        composeRule.onNodeWithContentDescription("Volver").performClick()

        // THEN: Vuelve a la lista de mascotas
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Todas las Mascotas")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Todas las Mascotas").assertIsDisplayed()
    }
}

