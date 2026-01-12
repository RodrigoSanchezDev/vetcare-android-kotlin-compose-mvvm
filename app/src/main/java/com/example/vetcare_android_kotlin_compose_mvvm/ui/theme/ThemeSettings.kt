package com.example.vetcare_android_kotlin_compose_mvvm.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * VetCare Theme Settings
 * Gestiona preferencias de tema: modo oscuro, alto contraste, etc.
 * Usa SharedPreferences para persistencia simple.
 */

private const val PREFS_NAME = "vetcare_theme_prefs"
private const val KEY_THEME_MODE = "theme_mode"
private const val KEY_HIGH_CONTRAST = "high_contrast"
private const val KEY_REDUCE_MOTION = "reduce_motion"
private const val KEY_LARGE_TEXT = "large_text"

/**
 * Modos de tema disponibles
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM  // Sigue la configuración del sistema
}

/**
 * Estado del tema actual
 */
data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val largeText: Boolean = false
)

/**
 * Repositorio para gestionar preferencias de tema usando SharedPreferences
 * Implementado como singleton para compartir estado entre componentes
 */
class ThemeSettingsRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeSettings = MutableStateFlow(loadSettings())
    val themeSettings: StateFlow<ThemeSettings> = _themeSettings.asStateFlow()

    private fun loadSettings(): ThemeSettings {
        return ThemeSettings(
            themeMode = try {
                ThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            },
            highContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false),
            reduceMotion = prefs.getBoolean(KEY_REDUCE_MOTION, false),
            largeText = prefs.getBoolean(KEY_LARGE_TEXT, false)
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeSettings.value = _themeSettings.value.copy(themeMode = mode)
    }

    fun setHighContrast(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
        _themeSettings.value = _themeSettings.value.copy(highContrast = enabled)
    }

    fun setReduceMotion(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REDUCE_MOTION, enabled).apply()
        _themeSettings.value = _themeSettings.value.copy(reduceMotion = enabled)
    }

    fun setLargeText(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LARGE_TEXT, enabled).apply()
        _themeSettings.value = _themeSettings.value.copy(largeText = enabled)
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemeSettingsRepository? = null

        fun getInstance(context: Context): ThemeSettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeSettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

/**
 * CompositionLocal para acceder al estado del tema
 */
val LocalThemeSettings = compositionLocalOf { ThemeSettings() }

/**
 * CompositionLocal para controlar animaciones reducidas
 */
val LocalReduceMotion = compositionLocalOf { false }

// ============================================
// PALETA DE COLORES - MODO CLARO
// ============================================
object VetCareColorsLight {
    val Primary = Color(0xFF5B2CFF)
    val PrimaryVariant = Color(0xFF3F1AD6)
    val PrimaryDark = Color(0xFF3D1FAD)

    val Background = Color(0xFFEEF1F7)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF6F7FB)

    val OnPrimary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF12131A)
    val OnSurface = Color(0xFF14151C)
    val MutedText = Color(0xFF7A7F8C)

    val BottomBar = Color(0xFF141725)
    val BottomBarItemInactive = Color(0xFF8D93A7)

    val Accent = Color(0xFFF6B84C)
    val Danger = Color(0xFFE5484D)
    val Success = Color(0xFF2DA44E)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)
    val Divider = Color(0xFFE5E7EF)
}

// ============================================
// PALETA DE COLORES - MODO OSCURO
// ============================================
object VetCareColorsDark {
    val Primary = Color(0xFF9D7FFF)
    val PrimaryVariant = Color(0xFF7B5EE0)
    val PrimaryDark = Color(0xFF5B2CFF)

    val Background = Color(0xFF0F1117)
    val Surface = Color(0xFF1A1D27)
    val SurfaceVariant = Color(0xFF252934)

    val OnPrimary = Color(0xFF0F1117)
    val OnBackground = Color(0xFFE8E9ED)
    val OnSurface = Color(0xFFF0F1F5)
    val MutedText = Color(0xFF9CA3B4)

    val BottomBar = Color(0xFF0A0C12)
    val BottomBarItemInactive = Color(0xFF6B7280)

    val Accent = Color(0xFFFBBF24)
    val Danger = Color(0xFFF87171)
    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFFBBF24)
    val Info = Color(0xFF60A5FA)
    val Divider = Color(0xFF2D3748)
}

// ============================================
// PALETA DE COLORES - ALTO CONTRASTE (LIGHT)
// ============================================
object VetCareColorsHighContrastLight {
    val Primary = Color(0xFF3300CC)
    val PrimaryVariant = Color(0xFF220088)
    val PrimaryDark = Color(0xFF110044)

    val Background = Color(0xFFFFFFFF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF0F0F0)

    val OnPrimary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF000000)
    val OnSurface = Color(0xFF000000)
    val MutedText = Color(0xFF4A4A4A)

    val BottomBar = Color(0xFF000000)
    val BottomBarItemInactive = Color(0xFF666666)

    val Accent = Color(0xFFCC8800)
    val Danger = Color(0xFFCC0000)
    val Success = Color(0xFF006600)
    val Warning = Color(0xFFCC6600)
    val Info = Color(0xFF0044CC)
    val Divider = Color(0xFF000000)
}

// ============================================
// PALETA DE COLORES - ALTO CONTRASTE (DARK)
// ============================================
object VetCareColorsHighContrastDark {
    val Primary = Color(0xFFBB99FF)
    val PrimaryVariant = Color(0xFFAA88EE)
    val PrimaryDark = Color(0xFF9977DD)

    val Background = Color(0xFF000000)
    val Surface = Color(0xFF0A0A0A)
    val SurfaceVariant = Color(0xFF1A1A1A)

    val OnPrimary = Color(0xFF000000)
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFFFFFFF)
    val MutedText = Color(0xFFCCCCCC)

    val BottomBar = Color(0xFF000000)
    val BottomBarItemInactive = Color(0xFFAAAAAA)

    val Accent = Color(0xFFFFDD00)
    val Danger = Color(0xFFFF6666)
    val Success = Color(0xFF66FF66)
    val Warning = Color(0xFFFFAA00)
    val Info = Color(0xFF66BBFF)
    val Divider = Color(0xFFFFFFFF)
}

/**
 * Color scheme unificado para VetCare
 */
data class VetCareColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val primaryDark: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val mutedText: Color,
    val bottomBar: Color,
    val bottomBarItemInactive: Color,
    val accent: Color,
    val danger: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val divider: Color,
    val isDark: Boolean,
    val isHighContrast: Boolean
) {
    val gradientStart: Color get() = primary
    val gradientEnd: Color get() = primaryVariant
}

/**
 * Obtiene los colores según el modo de tema actual
 */
fun getVetCareColors(isDark: Boolean, highContrast: Boolean): VetCareColorScheme {
    return when {
        highContrast && isDark -> VetCareColorScheme(
            primary = VetCareColorsHighContrastDark.Primary,
            primaryVariant = VetCareColorsHighContrastDark.PrimaryVariant,
            primaryDark = VetCareColorsHighContrastDark.PrimaryDark,
            background = VetCareColorsHighContrastDark.Background,
            surface = VetCareColorsHighContrastDark.Surface,
            surfaceVariant = VetCareColorsHighContrastDark.SurfaceVariant,
            onPrimary = VetCareColorsHighContrastDark.OnPrimary,
            onBackground = VetCareColorsHighContrastDark.OnBackground,
            onSurface = VetCareColorsHighContrastDark.OnSurface,
            mutedText = VetCareColorsHighContrastDark.MutedText,
            bottomBar = VetCareColorsHighContrastDark.BottomBar,
            bottomBarItemInactive = VetCareColorsHighContrastDark.BottomBarItemInactive,
            accent = VetCareColorsHighContrastDark.Accent,
            danger = VetCareColorsHighContrastDark.Danger,
            success = VetCareColorsHighContrastDark.Success,
            warning = VetCareColorsHighContrastDark.Warning,
            info = VetCareColorsHighContrastDark.Info,
            divider = VetCareColorsHighContrastDark.Divider,
            isDark = true,
            isHighContrast = true
        )
        highContrast && !isDark -> VetCareColorScheme(
            primary = VetCareColorsHighContrastLight.Primary,
            primaryVariant = VetCareColorsHighContrastLight.PrimaryVariant,
            primaryDark = VetCareColorsHighContrastLight.PrimaryDark,
            background = VetCareColorsHighContrastLight.Background,
            surface = VetCareColorsHighContrastLight.Surface,
            surfaceVariant = VetCareColorsHighContrastLight.SurfaceVariant,
            onPrimary = VetCareColorsHighContrastLight.OnPrimary,
            onBackground = VetCareColorsHighContrastLight.OnBackground,
            onSurface = VetCareColorsHighContrastLight.OnSurface,
            mutedText = VetCareColorsHighContrastLight.MutedText,
            bottomBar = VetCareColorsHighContrastLight.BottomBar,
            bottomBarItemInactive = VetCareColorsHighContrastLight.BottomBarItemInactive,
            accent = VetCareColorsHighContrastLight.Accent,
            danger = VetCareColorsHighContrastLight.Danger,
            success = VetCareColorsHighContrastLight.Success,
            warning = VetCareColorsHighContrastLight.Warning,
            info = VetCareColorsHighContrastLight.Info,
            divider = VetCareColorsHighContrastLight.Divider,
            isDark = false,
            isHighContrast = true
        )
        isDark -> VetCareColorScheme(
            primary = VetCareColorsDark.Primary,
            primaryVariant = VetCareColorsDark.PrimaryVariant,
            primaryDark = VetCareColorsDark.PrimaryDark,
            background = VetCareColorsDark.Background,
            surface = VetCareColorsDark.Surface,
            surfaceVariant = VetCareColorsDark.SurfaceVariant,
            onPrimary = VetCareColorsDark.OnPrimary,
            onBackground = Color.White,
            onSurface = VetCareColorsDark.OnSurface,
            mutedText = VetCareColorsDark.MutedText,
            bottomBar = VetCareColorsDark.BottomBar,
            bottomBarItemInactive = VetCareColorsDark.BottomBarItemInactive,
            accent = VetCareColorsDark.Accent,
            danger = VetCareColorsDark.Danger,
            success = VetCareColorsDark.Success,
            warning = VetCareColorsDark.Warning,
            info = VetCareColorsDark.Info,
            divider = VetCareColorsDark.Divider,
            isDark = true,
            isHighContrast = false
        )
        else -> VetCareColorScheme(
            primary = VetCareColorsLight.Primary,
            primaryVariant = VetCareColorsLight.PrimaryVariant,
            primaryDark = VetCareColorsLight.PrimaryDark,
            background = VetCareColorsLight.Background,
            surface = VetCareColorsLight.Surface,
            surfaceVariant = VetCareColorsLight.SurfaceVariant,
            onPrimary = VetCareColorsLight.OnPrimary,
            onBackground = Color.Black,
            onSurface = VetCareColorsLight.OnSurface,
            mutedText = VetCareColorsLight.MutedText,
            bottomBar = VetCareColorsLight.BottomBar,
            bottomBarItemInactive = VetCareColorsLight.BottomBarItemInactive,
            accent = VetCareColorsLight.Accent,
            danger = VetCareColorsLight.Danger,
            success = VetCareColorsLight.Success,
            warning = VetCareColorsLight.Warning,
            info = VetCareColorsLight.Info,
            divider = VetCareColorsLight.Divider,
            isDark = false,
            isHighContrast = false
        )
    }
}

/**
 * CompositionLocal para acceder a los colores del tema
 */
val LocalVetCareColors = staticCompositionLocalOf {
    VetCareColorScheme(
        primary = VetCareColorsLight.Primary,
        primaryVariant = VetCareColorsLight.PrimaryVariant,
        primaryDark = VetCareColorsLight.PrimaryDark,
        background = VetCareColorsLight.Background,
        surface = VetCareColorsLight.Surface,
        surfaceVariant = VetCareColorsLight.SurfaceVariant,
        onPrimary = VetCareColorsLight.OnPrimary,
        onBackground = Color.Black,
        onSurface = VetCareColorsLight.OnSurface,
        mutedText = VetCareColorsLight.MutedText,
        bottomBar = VetCareColorsLight.BottomBar,
        bottomBarItemInactive = VetCareColorsLight.BottomBarItemInactive,
        accent = VetCareColorsLight.Accent,
        danger = VetCareColorsLight.Danger,
        success = VetCareColorsLight.Success,
        warning = VetCareColorsLight.Warning,
        info = VetCareColorsLight.Info,
        divider = VetCareColorsLight.Divider,
        isDark = false,
        isHighContrast = false
    )
}
