package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Tab Bar estilizado de VetCare
 * Componente avanzado con animaciones y soporte de accesibilidad completo
 */
@Composable
fun VetCareTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.SurfaceVariant,
    selectedColor: Color = VetCareColors.Primary,
    unselectedColor: Color = VetCareColors.MutedText
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTabIndex

                VetCareTab(
                    text = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(index) },
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    modifier = Modifier.weight(1f),
                    tabIndex = index,
                    totalTabs = tabs.size
                )
            }
        }
    }
}

/**
 * Tab individual con animación
 */
@Composable
private fun VetCareTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
    tabIndex: Int,
    totalTabs: Int
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) VetCareColors.Surface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabBackground"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabTextColor"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .semantics {
                contentDescription = "$text, pestaña ${tabIndex + 1} de $totalTabs${if (isSelected) ", seleccionada" else ""}"
                role = Role.Tab
                selected = isSelected
            },
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius - 4.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Tab Row con íconos
 */
@Composable
fun VetCareIconTabRow(
    tabs: List<TabItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.SurfaceVariant
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTabIndex

                VetCareIconTab(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f),
                    tabIndex = index,
                    totalTabs = tabs.size
                )
            }
        }
    }
}

/**
 * Tab con ícono individual
 */
@Composable
private fun VetCareIconTab(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tabIndex: Int,
    totalTabs: Int
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) VetCareColors.Surface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) VetCareColors.Primary else VetCareColors.MutedText,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabContentColor"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .semantics {
                contentDescription = "${tab.label}, pestaña ${tabIndex + 1} de $totalTabs${if (isSelected) ", seleccionada" else ""}"
                role = Role.Tab
                selected = isSelected
            },
        shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius - 4.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected && tab.selectedIcon != null) tab.selectedIcon else tab.icon,
                contentDescription = null, // Ya está en la semántica del Surface
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Data class para tabs con ícono
 */
data class TabItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badge: Int? = null
)

/**
 * Scrollable Tab Row para muchas pestañas
 */
@Composable
fun VetCareScrollableTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = VetCareColors.Background,
        contentColor = VetCareColors.Primary,
        edgePadding = VetCareSpacing.md,
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomStart)
                        .offset(x = tabPositions[selectedTabIndex].left)
                        .width(tabPositions[selectedTabIndex].width),
                    height = 3.dp,
                    color = VetCareColors.Primary
                )
            }
        },
        divider = {
            HorizontalDivider(color = VetCareColors.Divider)
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedTabIndex
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier.semantics {
                    contentDescription = "$tab, pestaña ${index + 1} de ${tabs.size}${if (isSelected) ", seleccionada" else ""}"
                    role = Role.Tab
                    selected = isSelected
                },
                text = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                },
                selectedContentColor = VetCareColors.Primary,
                unselectedContentColor = VetCareColors.MutedText
            )
        }
    }
}

/**
 * Segmented button style tabs (Material 3)
 */
@Composable
fun VetCareSegmentedTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VetCareShapeTokens.ButtonRadius))
            .background(VetCareColors.Divider)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedTabIndex
            val isFirst = index == 0
            val isLast = index == tabs.lastIndex

            val shape = when {
                isFirst -> RoundedCornerShape(
                    topStart = VetCareShapeTokens.ButtonRadius - 2.dp,
                    bottomStart = VetCareShapeTokens.ButtonRadius - 2.dp,
                    topEnd = 4.dp,
                    bottomEnd = 4.dp
                )
                isLast -> RoundedCornerShape(
                    topStart = 4.dp,
                    bottomStart = 4.dp,
                    topEnd = VetCareShapeTokens.ButtonRadius - 2.dp,
                    bottomEnd = VetCareShapeTokens.ButtonRadius - 2.dp
                )
                else -> RoundedCornerShape(4.dp)
            }

            Surface(
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .semantics {
                        contentDescription = "$tab${if (isSelected) ", seleccionado" else ""}"
                        role = Role.Tab
                        selected = isSelected
                    },
                shape = shape,
                color = if (isSelected) VetCareColors.Surface else Color.Transparent
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) VetCareColors.Primary else VetCareColors.MutedText
                    )
                }
            }
        }
    }
}

