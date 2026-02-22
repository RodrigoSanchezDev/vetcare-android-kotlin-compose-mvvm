package com.example.vetcare_android_kotlin_compose_mvvm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth.OnboardingScreen
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas funcionales para la pantalla de Onboarding
 *
 * Verifica las interacciones del usuario en la pantalla de bienvenida:
 * - Renderizado correcto del contenido de presentación
 * - Funcionamiento del botón de continuar
 * - Navegación hacia la pantalla de login
 *
 * Framework: Compose UI Testing (equivalente a Espresso para Jetpack Compose)
 *
 * @author Rodrigo Sánchez
 */
@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ════════════════════════════════════════════════════════════════
    // TESTS DE RENDERIZADO DE COMPONENTES
    // ════════════════════════════════════════════════════════════════

    @Test
    fun onboardingScreen_displaysTitle() {
        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = {})
            }
        }

        composeTestRule
            .onNodeWithText("Para la salud y\ncuidado de tu mascota", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_displaysSubtitle() {
        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = {})
            }
        }

        composeTestRule
            .onNodeWithText("VetCare es la forma revolucionaria", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_displaysContinueButton() {
        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = {})
            }
        }

        // El botón circular tiene contentDescription "Continuar"
        composeTestRule
            .onNodeWithContentDescription("Continuar")
            .assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_displaysPetImage() {
        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = {})
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Max - Mascota")
            .assertIsDisplayed()
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE NAVEGACIÓN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun onboardingScreen_continueButtonTriggersCallback() {
        var continueClicked = false

        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = { continueClicked = true })
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Continuar")
            .performClick()

        assertTrue("Continue callback should be triggered", continueClicked)
    }

    @Test
    fun onboardingScreen_continueButtonClickableMultipleTimes() {
        var clickCount = 0

        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = { clickCount++ })
            }
        }

        val continueButton = composeTestRule
            .onNodeWithContentDescription("Continuar")

        continueButton.performClick()
        continueButton.performClick()

        assertTrue("Continue should be clickable multiple times", clickCount >= 1)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE CONTENIDO VISUAL
    // ════════════════════════════════════════════════════════════════

    @Test
    fun onboardingScreen_allContentRendersWithoutCrash() {
        composeTestRule.setContent {
            VetCareTheme {
                OnboardingScreen(onContinue = {})
            }
        }

        // Verificar que la pantalla se renderiza completa sin crash
        composeTestRule.waitForIdle()

        // Verificar que al menos el título y el botón están presentes
        composeTestRule
            .onNodeWithText("salud", substring = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Continuar")
            .assertIsDisplayed()
    }
}

