package com.example.vetcare_android_kotlin_compose_mvvm.ui.theme

import androidx.compose.ui.graphics.Color

// VetCare Design Tokens - Colors
object VetCareColors {
    // Primary
    val Primary = Color(0xFF5B2CFF)           // Morado vibrante
    val PrimaryVariant = Color(0xFF3F1AD6)    // Morado más oscuro (DeepPurple)
    val PrimaryDark = Color(0xFF3D1FAD)       // Morado aún más oscuro para gradientes

    // Backgrounds
    val Background = Color(0xFFEEF1F7)        // Gris-azulado muy claro
    val Surface = Color(0xFFFFFFFF)           // Blanco
    val SurfaceVariant = Color(0xFFF6F7FB)    // Tarjetas secundarias

    // On Colors (texto sobre superficies)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF12131A)
    val OnSurface = Color(0xFF14151C)
    val MutedText = Color(0xFF7A7F8C)

    // Bottom Bar
    val BottomBar = Color(0xFF141725)
    val BottomBarItemInactive = Color(0xFF8D93A7)

    // Accent & Status
    val Accent = Color(0xFFF6B84C)             // Gold/Orange CTA
    val Danger = Color(0xFFE5484D)
    val Success = Color(0xFF2DA44E)
    val Divider = Color(0xFFE5E7EF)

    // Gradients (para onboarding)
    val GradientStart = Primary
    val GradientEnd = PrimaryVariant
}
