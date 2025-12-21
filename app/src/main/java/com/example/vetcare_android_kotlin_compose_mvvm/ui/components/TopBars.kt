package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * TopBar personalizada de VetCare
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = VetCareColors.Background
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = VetCareColors.OnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = VetCareColors.MutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navegación",
                        tint = VetCareColors.OnBackground
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = VetCareColors.OnBackground
        )
    )
}

/**
 * TopBar con botón de retroceso
 */
@Composable
fun VetTopBarWithBack(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    VetTopBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        onNavigationClick = onBackClick,
        actions = actions
    )
}

/**
 * Empty State component
 */
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(VetCareSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
    ) {
        Box(
            modifier = Modifier.size(VetCareSizes.avatarLarge),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = VetCareColors.OnBackground
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = VetCareColors.MutedText
        )

        if (actionLabel != null) {
            Spacer(modifier = Modifier.height(VetCareSpacing.xs))
            PrimaryButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.width(VetCareSizes.avatarXLarge * 2)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun VetTopBarPreview() {
    VetCareTheme {
        VetTopBar(
            title = "VetCare",
            subtitle = "Dashboard"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun VetTopBarWithBackPreview() {
    VetCareTheme {
        VetTopBarWithBack(
            title = "Detalle de Mascota",
            onBackClick = {}
        )
    }
}

