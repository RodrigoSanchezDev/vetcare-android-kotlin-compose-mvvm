package com.example.vetcare_android_kotlin_compose_mvvm.ui.accessibility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp

/**
 * VetCare Accessibility Utilities
 *
 * Este archivo contiene utilidades para mejorar la accesibilidad de la app
 * siguiendo las guías WCAG 2.1 y Material Design Accessibility.
 */

// ============================================
// TAMAÑOS MÍNIMOS DE TOQUE (WCAG 2.5.5)
// ============================================

/**
 * Tamaño mínimo de área táctil recomendado por Material Design
 * WCAG 2.1 recomienda al menos 44x44dp, Material usa 48x48dp
 */
object AccessibleTouchTarget {
    val MinSize = 48.dp
    val RecommendedSize = 56.dp
    val MinSpacing = 8.dp
}

// ============================================
// EXTENSIONES DE MODIFIER PARA SEMÁNTICA
// ============================================

/**
 * Añade semántica para un botón con descripción accesible
 */
fun Modifier.accessibleButton(
    label: String,
    hint: String? = null,
    onClick: () -> Unit
): Modifier = this
    .semantics {
        contentDescription = label
        hint?.let { stateDescription = it }
        role = Role.Button
    }
    .clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )

/**
 * Añade semántica para elementos de lista
 */
fun Modifier.accessibleListItem(
    label: String,
    position: Int? = null,
    totalItems: Int? = null
): Modifier = this.semantics {
    contentDescription = buildString {
        append(label)
        if (position != null && totalItems != null) {
            append(". Elemento $position de $totalItems")
        }
    }
}

/**
 * Añade semántica para imágenes
 */
fun Modifier.accessibleImage(
    description: String
): Modifier = this.semantics {
    contentDescription = description
    role = Role.Image
}

/**
 * Añade semántica para encabezados (mejora navegación por lectores de pantalla)
 */
fun Modifier.accessibleHeading(): Modifier = this.semantics {
    heading()
}


/**
 * Añade semántica para campos de texto
 */
fun Modifier.accessibleTextField(
    label: String,
    errorMessage: String? = null
): Modifier = this.semantics {
    contentDescription = buildString {
        append("Campo de texto: $label")
        errorMessage?.let { append(". Error: $it") }
    }
}

/**
 * Añade semántica para estado de carga
 */
fun Modifier.accessibleLoading(
    isLoading: Boolean,
    loadingMessage: String = "Cargando..."
): Modifier = if (isLoading) {
    this.semantics {
        contentDescription = loadingMessage
        liveRegion = LiveRegionMode.Polite
    }
} else this

/**
 * Añade semántica para checkboxes/switches
 */
fun Modifier.accessibleToggle(
    label: String,
    isChecked: Boolean
): Modifier = this.semantics {
    contentDescription = "$label, ${if (isChecked) "activado" else "desactivado"}"
    role = Role.Checkbox
    toggleableState = if (isChecked) ToggleableState.On else ToggleableState.Off
}

/**
 * Añade semántica para tabs
 */
fun Modifier.accessibleTab(
    label: String,
    isSelected: Boolean,
    tabIndex: Int,
    totalTabs: Int
): Modifier = this.semantics {
    contentDescription = "$label, pestaña ${tabIndex + 1} de $totalTabs${if (isSelected) ", seleccionada" else ""}"
    role = Role.Tab
    selected = isSelected
}

/**
 * Combina múltiples nodos semánticos en uno (para cards complejas)
 */
fun Modifier.mergeAccessibility(): Modifier = this.semantics(mergeDescendants = true) {}

// ============================================
// RATIOS DE CONTRASTE (WCAG 2.1)
// ============================================

/**
 * Ratios de contraste según WCAG 2.1
 * - AA Normal: 4.5:1
 * - AA Large (18pt+ o 14pt bold): 3:1
 * - AAA Normal: 7:1
 * - AAA Large: 4.5:1
 */
object ContrastRatios {
    const val AA_NORMAL = 4.5
    const val AA_LARGE = 3.0
    const val AAA_NORMAL = 7.0
    const val AAA_LARGE = 4.5
}

// ============================================
// STRINGS DE ACCESIBILIDAD COMUNES
// ============================================

/**
 * Descripciones comunes para elementos de la app
 */
object AccessibilityStrings {
    // Navegación
    const val BACK = "Volver a la pantalla anterior"
    const val CLOSE = "Cerrar"
    const val MENU = "Abrir menú"

    // Acciones
    const val ADD = "Agregar nuevo elemento"
    const val EDIT = "Editar"
    const val DELETE = "Eliminar"
    const val SAVE = "Guardar cambios"
    const val CANCEL = "Cancelar"
    const val SEARCH = "Buscar"
    const val FILTER = "Filtrar resultados"
    const val SORT = "Ordenar lista"
    const val REFRESH = "Actualizar"

    // Estados
    const val LOADING = "Cargando contenido, por favor espera"
    const val EMPTY_LIST = "Lista vacía"
    const val ERROR = "Ha ocurrido un error"

    // Formularios
    const val REQUIRED_FIELD = "Campo requerido"
    const val SHOW_PASSWORD = "Mostrar contraseña"
    const val HIDE_PASSWORD = "Ocultar contraseña"

    // VetCare específicos
    const val PET_PHOTO = "Foto de la mascota"
    const val VET_PHOTO = "Foto del veterinario"
    const val ADD_PET = "Agregar nueva mascota"
    const val ADD_APPOINTMENT = "Agendar nueva cita"
    const val ADD_VACCINE = "Registrar nueva vacuna"
    const val VIEW_DETAILS = "Ver detalles"
}

// ============================================
// COMPOSABLES HELPER
// ============================================

/**
 * Proporciona un MutableInteractionSource recordado para clicks accesibles
 */
@Composable
fun rememberAccessibleInteraction(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}

