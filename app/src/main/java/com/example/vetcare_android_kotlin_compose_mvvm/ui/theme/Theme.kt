package com.example.vetcare_android_kotlin_compose_mvvm.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// VetCare Light Color Scheme - No dynamic colors to maintain brand consistency
private val VetCareLightColorScheme = lightColorScheme(
    primary = VetCareColors.Primary,
    onPrimary = VetCareColors.OnPrimary,
    primaryContainer = VetCareColors.PrimaryVariant,
    onPrimaryContainer = VetCareColors.OnPrimary,
    secondary = VetCareColors.Accent,
    onSecondary = VetCareColors.OnSurface,
    secondaryContainer = VetCareColors.SurfaceVariant,
    onSecondaryContainer = VetCareColors.OnSurface,
    tertiary = VetCareColors.Success,
    onTertiary = VetCareColors.OnPrimary,
    error = VetCareColors.Danger,
    onError = VetCareColors.OnPrimary,
    background = VetCareColors.Background,
    onBackground = VetCareColors.OnBackground,
    surface = VetCareColors.Surface,
    onSurface = VetCareColors.OnSurface,
    surfaceVariant = VetCareColors.SurfaceVariant,
    onSurfaceVariant = VetCareColors.MutedText,
    outline = VetCareColors.Divider,
    outlineVariant = VetCareColors.Divider
)

@Composable
fun VetCareTheme(
    content: @Composable () -> Unit
) {
    // Always use light theme for brand consistency (per design spec)
    val colorScheme = VetCareLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = VetCareColors.Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VetCareTypography,
        shapes = VetCareShapes,
        content = content
    )
}

