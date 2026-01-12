package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Item para la barra de navegación inferior
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val isCenterAction: Boolean = false
)

/**
 * Bottom Navigation Bar de VetCare - Estilo Premium Flotante
 * Basado en el diseño de referencia con barra oscura y avatar central dorado
 */
@Composable
fun VetCareBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    onCenterClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VetCareSpacing.lg, vertical = VetCareSpacing.sm)
    ) {
        // Barra principal flotante
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = VetCareColors.BottomBar.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(32.dp),
            color = VetCareColors.BottomBar
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VetCareSpacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Items antes del centro (izquierda)
                val halfIndex = items.size / 2
                items.take(halfIndex).forEachIndexed { index, item ->
                    key(item.route) { // Key única por ruta
                        BottomNavItemView(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = {
                                Log.d("BottomNav", "LEFT[$index] label=${item.label} route=${item.route}")
                                onItemClick(item)
                            }
                        )
                    }
                }

                // Espacio para el botón central
                Spacer(modifier = Modifier.width(56.dp))

                // Items después del centro (derecha)
                items.drop(halfIndex).forEachIndexed { index, item ->
                    key(item.route) { // Key única por ruta
                        BottomNavItemView(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = {
                                Log.d("BottomNav", "RIGHT[${index + halfIndex}] label=${item.label} route=${item.route}")
                                onItemClick(item)
                            }
                        )
                    }
                }
            }
        }

        // Botón central flotante (avatar dorado)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-12).dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                color = VetCareColors.Accent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onCenterClick?.invoke() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Mascota",
                        tint = VetCareColors.OnSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) VetCareColors.Accent else VetCareColors.BottomBarItemInactive,
        animationSpec = spring(),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = VetCareSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Indicador de selección
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(VetCareColors.Accent)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * Items para Admin Bottom Nav (4 items para distribuir 2-2 alrededor del centro)
 */
val adminBottomNavItems = listOf(
    BottomNavItem(
        route = "admin/home",
        label = "Inicio",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = "admin/appointments",
        label = "Citas",
        icon = Icons.Outlined.CalendarMonth,
        selectedIcon = Icons.Filled.CalendarMonth
    ),
    BottomNavItem(
        route = "admin/veterinarians",
        label = "Staff",
        icon = Icons.Outlined.MedicalServices,
        selectedIcon = Icons.Filled.MedicalServices
    ),
    BottomNavItem(
        route = "admin/pets",
        label = "Mascotas",
        icon = Icons.Outlined.Pets,
        selectedIcon = Icons.Filled.Pets
    )
)

/**
 * Items para Owner Bottom Nav (4 items)
 */
val ownerBottomNavItems = listOf(
    BottomNavItem(
        route = "owner/home",
        label = "Inicio",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = "owner/appointments",
        label = "Citas",
        icon = Icons.Outlined.CalendarMonth,
        selectedIcon = Icons.Filled.CalendarMonth
    ),
    BottomNavItem(
        route = "owner/discover",
        label = "Descubrir",
        icon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore
    ),
    BottomNavItem(
        route = "owner/pets",
        label = "Mascotas",
        icon = Icons.Outlined.Pets,
        selectedIcon = Icons.Filled.Pets
    )
)

/**
 * Story Avatar para feed (círculo con borde gradiente)
 */
@Composable
fun StoryAvatar(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isViewed: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {
        Icon(
            Icons.Default.Person,
            contentDescription = name,
            tint = VetCareColors.MutedText,
            modifier = Modifier.size(32.dp)
        )
    }
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = VetCareSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar con borde gradiente
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(
                    brush = if (isViewed) {
                        Brush.linearGradient(
                            listOf(
                                VetCareColors.MutedText,
                                VetCareColors.MutedText
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                VetCareColors.Primary,
                                VetCareColors.Accent
                            )
                        )
                    },
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(VetCareColors.Surface),
                contentAlignment = Alignment.Center,
                content = content
            )
        }

        Spacer(modifier = Modifier.height(VetCareSpacing.xxs))

        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isViewed) VetCareColors.MutedText else VetCareColors.OnBackground,
            maxLines = 1
        )
    }
}

/**
 * Service Card para grid de servicios (estilo del diseño de referencia)
 */
@Composable
fun ServiceCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: androidx.compose.ui.graphics.Color = VetCareColors.SurfaceVariant
) {
    Card(
        modifier = modifier
            .size(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(VetCareSpacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VetCareColors.Primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(VetCareSpacing.xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = VetCareColors.OnSurface
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VetCareBottomBarPreview() {
    VetCareTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            VetCareBottomBar(
                items = ownerBottomNavItems,
                currentRoute = "owner/home",
                onItemClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StoryAvatarPreview() {
    VetCareTheme {
        Row {
            StoryAvatar(name = "You", onClick = {}, isViewed = false)
            StoryAvatar(name = "Jane", onClick = {}, isViewed = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceCardPreview() {
    VetCareTheme {
        ServiceCard(
            icon = Icons.Default.MedicalServices,
            label = "Veterinary",
            onClick = {}
        )
    }
}

