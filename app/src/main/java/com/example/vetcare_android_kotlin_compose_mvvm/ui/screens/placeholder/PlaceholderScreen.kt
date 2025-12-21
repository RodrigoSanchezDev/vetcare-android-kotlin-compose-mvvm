package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareColors
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareSpacing
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareTheme

/**
 * Pantalla placeholder para desarrollo
 * Muestra título y subtítulo con un botón opcional de navegación
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    onNavigate: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = VetCareColors.OnBackground
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = VetCareColors.MutedText,
                textAlign = TextAlign.Center
            )

            if (onNavigate != null) {
                Spacer(modifier = Modifier.height(VetCareSpacing.lg))

                FilledIconButton(
                    onClick = onNavigate,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = VetCareColors.Accent,
                        contentColor = VetCareColors.OnSurface
                    ),
                    modifier = Modifier.size(VetCareSpacing.xxl * 2)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continuar"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    VetCareTheme {
        PlaceholderScreen(
            title = "VetCare",
            subtitle = "Pantalla en desarrollo",
            onNavigate = {}
        )
    }
}

