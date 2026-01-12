package com.example.vetcare_android_kotlin_compose_mvvm.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * VetCare Theme con soporte para:
 * - Modo Oscuro
 * - Alto Contraste
 * - Reducción de movimiento
 * - Texto grande
 */

// ============================================
// MATERIAL 3 COLOR SCHEMES
// ============================================

private fun vetCareLightColorScheme(colors: VetCareColorScheme) = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryVariant,
    onPrimaryContainer = colors.onPrimary,
    secondary = colors.accent,
    onSecondary = colors.onSurface,
    secondaryContainer = colors.surfaceVariant,
    onSecondaryContainer = colors.onSurface,
    tertiary = colors.success,
    onTertiary = colors.onPrimary,
    error = colors.danger,
    onError = colors.onPrimary,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.mutedText,
    outline = colors.divider,
    outlineVariant = colors.divider
)

private fun vetCareDarkColorScheme(colors: VetCareColorScheme) = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryVariant,
    onPrimaryContainer = colors.onPrimary,
    secondary = colors.accent,
    onSecondary = colors.onSurface,
    secondaryContainer = colors.surfaceVariant,
    onSecondaryContainer = colors.onSurface,
    tertiary = colors.success,
    onTertiary = colors.onPrimary,
    error = colors.danger,
    onError = colors.onPrimary,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.mutedText,
    outline = colors.divider,
    outlineVariant = colors.divider
)

/**
 * Tema principal de VetCare con soporte completo de accesibilidad
 */
@Composable
fun VetCareTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    highContrast: Boolean = false,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    // Determinar si usar modo oscuro
    val isDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Obtener colores según configuración
    val vetCareColors = getVetCareColors(isDarkTheme, highContrast)

    // Color scheme de Material 3
    val colorScheme = if (isDarkTheme) {
        vetCareDarkColorScheme(vetCareColors)
    } else {
        vetCareLightColorScheme(vetCareColors)
    }

    // Configurar barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = vetCareColors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    // Proveer los colores y configuraciones a través de CompositionLocals
    CompositionLocalProvider(
        LocalVetCareColors provides vetCareColors,
        LocalReduceMotion provides reduceMotion,
        LocalThemeSettings provides ThemeSettings(
            themeMode = themeMode,
            highContrast = highContrast,
            reduceMotion = reduceMotion
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VetCareTypography,
            shapes = VetCareShapes,
            content = content
        )
    }
}

/**
 * Tema con configuración desde DataStore (persistida)
 */
@Composable
fun VetCareThemeWithSettings(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ThemeSettingsRepository.getInstance(context) }

    // Cargar configuración inicial
    val settings by repository.themeSettings.collectAsState()

    VetCareTheme(
        themeMode = settings.themeMode,
        highContrast = settings.highContrast,
        reduceMotion = settings.reduceMotion,
        content = content
    )
}

/**
 * Objeto helper para acceder a los colores del tema actual
 */
object VetCareTheme {
    val colors: VetCareColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current

    val reduceMotion: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalReduceMotion.current

    val settings: ThemeSettings
        @Composable
        @ReadOnlyComposable
        get() = LocalThemeSettings.current
}

// ============================================
// BACKWARD COMPATIBILITY
// Mantener VetCareColors para no romper código existente
// ============================================

/**
 * Objeto de colores para compatibilidad con código existente.
 * Usa LocalVetCareColors internamente para obtener colores según el tema.
 *
 * NOTA: Para código nuevo, preferir usar VetCareTheme.colors
 */
object VetCareColors {
    // Primary
    val Primary: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.primary

    val PrimaryVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.primaryVariant

    val PrimaryDark: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.primaryDark

    // Backgrounds
    val Background: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.background

    val Surface: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.surface

    val SurfaceVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.surfaceVariant

    // On Colors
    val OnPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.onPrimary

    val OnBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.onBackground

    val OnSurface: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.onSurface

    val MutedText: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.mutedText

    // Bottom Bar
    val BottomBar: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.bottomBar

    val BottomBarItemInactive: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.bottomBarItemInactive

    // Accent & Status
    val Accent: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.accent

    val Danger: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.danger

    val Success: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.success

    val Divider: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.divider

    // Gradients
    val GradientStart: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.gradientStart

    val GradientEnd: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.gradientEnd

    // Theme state
    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalVetCareColors.current.isDark
}

