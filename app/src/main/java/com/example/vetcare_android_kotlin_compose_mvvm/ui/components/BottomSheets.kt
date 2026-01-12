package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Modal Bottom Sheet reutilizable de VetCare
 * Componente avanzado para mostrar opciones o contenido desde abajo
 *
 * Incluye soporte completo de accesibilidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetCareBottomSheet(
    onDismiss: () -> Unit,
    title: String? = null,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = VetCareShapeTokens.BottomSheetRadius,
            topEnd = VetCareShapeTokens.BottomSheetRadius
        ),
        containerColor = VetCareColors.Surface,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle visual
                Surface(
                    modifier = Modifier
                        .padding(vertical = VetCareSpacing.sm)
                        .semantics { contentDescription = "Arrastrar para cerrar" },
                    color = VetCareColors.Divider,
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Spacer(modifier = Modifier.size(width = 32.dp, height = 4.dp))
                }

                // Título opcional
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = VetCareColors.OnSurface,
                        modifier = Modifier
                            .padding(bottom = VetCareSpacing.sm)
                            .semantics { contentDescription = "Título: $title" }
                    )
                    HorizontalDivider(color = VetCareColors.Divider)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VetCareSpacing.md)
                .padding(bottom = VetCareSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs),
            content = content
        )
    }
}

/**
 * Item de acción para BottomSheet
 */
@Composable
fun BottomSheetAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: androidx.compose.ui.graphics.Color = VetCareColors.Primary,
    enabled: Boolean = true,
    destructive: Boolean = false
) {
    val contentColor = when {
        !enabled -> VetCareColors.MutedText
        destructive -> VetCareColors.Danger
        else -> VetCareColors.OnSurface
    }
    val iconColor = when {
        !enabled -> VetCareColors.MutedText
        destructive -> VetCareColors.Danger
        else -> iconTint
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = label
                role = Role.Button
            },
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
        color = VetCareColors.Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VetCareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // El texto del label ya describe la acción
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * BottomSheet de confirmación con título, mensaje y acciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar",
    isDestructive: Boolean = false,
    icon: ImageVector? = null
) {
    VetCareBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VetCareSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Icono opcional
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) VetCareColors.Danger else VetCareColors.Primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Título
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = VetCareColors.OnSurface,
                textAlign = TextAlign.Center
            )

            // Mensaje
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.MutedText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.sm))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
            ) {
                // Botón cancelar
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(VetCareSizes.minTouchTarget),
                    shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius)
                ) {
                    Text(dismissText)
                }

                // Botón confirmar
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(VetCareSizes.minTouchTarget),
                    shape = RoundedCornerShape(VetCareShapeTokens.ButtonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDestructive) VetCareColors.Danger else VetCareColors.Primary
                    )
                ) {
                    Text(
                        text = confirmText,
                        color = VetCareColors.OnPrimary
                    )
                }
            }
        }
    }
}

/**
 * BottomSheet de selección múltiple (ej: filtros, ordenamiento)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectionBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    itemIcon: ((T) -> ImageVector)? = null
) {
    VetCareBottomSheet(
        onDismiss = onDismiss,
        title = title
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            Surface(
                onClick = {
                    onItemSelected(item)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${itemLabel(item)}${if (isSelected) ", seleccionado" else ""}"
                        role = Role.RadioButton
                    },
                shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
                color = if (isSelected) VetCareColors.Primary.copy(alpha = 0.1f) else VetCareColors.Surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(VetCareSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    // Icono opcional
                    if (itemIcon != null) {
                        Icon(
                            imageVector = itemIcon(item),
                            contentDescription = null,
                            tint = if (isSelected) VetCareColors.Primary else VetCareColors.MutedText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Label
                    Text(
                        text = itemLabel(item),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) VetCareColors.Primary else VetCareColors.OnSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Indicador de selección
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = VetCareColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * BottomSheet de acciones rápidas para un elemento (ej: mascota, cita)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    actions: List<QuickAction>
) {
    VetCareBottomSheet(
        onDismiss = onDismiss,
        title = title
    ) {
        actions.forEach { action ->
            BottomSheetAction(
                icon = action.icon,
                label = action.label,
                onClick = {
                    action.onClick()
                    onDismiss()
                },
                iconTint = action.iconTint ?: VetCareColors.Primary,
                destructive = action.isDestructive,
                enabled = action.enabled
            )
        }
    }
}

/**
 * Data class para acciones rápidas
 */
data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val iconTint: androidx.compose.ui.graphics.Color? = null,
    val isDestructive: Boolean = false,
    val enabled: Boolean = true
)

