package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Botón primario de VetCare (CTA principal con color Accent)
 * Incluye soporte de accesibilidad con semantics
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentDescriptionOverride: String? = null
) {
    val accessibilityDescription = contentDescriptionOverride ?: text
    val stateDesc = when {
        isLoading -> "Cargando"
        !enabled -> "Deshabilitado"
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(VetCareSizes.minTouchTarget)
            .semantics {
                contentDescription = accessibilityDescription
                role = Role.Button
                stateDesc?.let { stateDescription = it }
            },
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = VetCareColors.Accent,
            contentColor = VetCareColors.OnSurface,
            disabledContainerColor = VetCareColors.Accent.copy(alpha = 0.5f),
            disabledContentColor = VetCareColors.OnSurface.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.height(24.dp),
                color = VetCareColors.OnSurface,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * Botón secundario de VetCare (outline con borde primario)
 * Incluye soporte de accesibilidad con semantics
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescriptionOverride: String? = null
) {
    val accessibilityDescription = contentDescriptionOverride ?: text

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(VetCareSizes.minTouchTarget)
            .semantics {
                contentDescription = accessibilityDescription
                role = Role.Button
                if (!enabled) stateDescription = "Deshabilitado"
            },
        enabled = enabled,
        shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = VetCareColors.Primary,
            disabledContentColor = VetCareColors.Primary.copy(alpha = 0.5f)
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled).copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (enabled) VetCareColors.Primary else VetCareColors.Primary.copy(alpha = 0.5f)
            )
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Botón circular de VetCare para CTAs especiales (onboarding)
 */
@Composable
fun CircleCTAButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.Accent,
    contentColor: Color = VetCareColors.OnSurface,
    content: @Composable () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        content()
    }
}

/**
 * Botón de texto (para links secundarios)
 */
@Composable
fun TextLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = VetCareColors.Primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun PrimaryButtonPreview() {
    VetCareTheme {
        PrimaryButton(
            text = "Continuar",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun SecondaryButtonPreview() {
    VetCareTheme {
        SecondaryButton(
            text = "Cancelar",
            onClick = {}
        )
    }
}

