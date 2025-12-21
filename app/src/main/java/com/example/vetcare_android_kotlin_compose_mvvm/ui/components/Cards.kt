package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Card premium de VetCare (radius 28dp, sombra sutil)
 */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.Surface,
    contentPadding: PaddingValues = PaddingValues(VetCareSpacing.md),
    elevation: Dp = VetCareElevation.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(
            elevation = elevation,
            shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
            ambientColor = Color.Black.copy(alpha = 0.08f),
            spotColor = Color.Black.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(VetCareShapeTokens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            content = content
        )
    }
}

/**
 * Card suave de VetCare (radius 18dp, surfaceVariant)
 */
@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.SurfaceVariant,
    contentPadding: PaddingValues = PaddingValues(VetCareSpacing.sm),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

/**
 * Card de servicio con icono y label
 */
@Composable
fun ServiceTile(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.Surface
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(VetCareShapeTokens.ServiceTileRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = VetCareElevation.low)
    ) {
        Column(
            modifier = Modifier
                .padding(VetCareSpacing.md)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Box(
                modifier = Modifier.size(VetCareSizes.iconLarge),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = VetCareColors.OnSurface
            )
        }
    }
}

/**
 * Card de métrica pequeña (peso, vacunas, etc.)
 */
@Composable
fun MetricChipCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = VetCareColors.SurfaceVariant
) {
    SoftCard(
        modifier = modifier,
        containerColor = containerColor,
        contentPadding = PaddingValues(VetCareSpacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.xs)
        ) {
            Box(
                modifier = Modifier.size(VetCareSizes.iconMedium),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = VetCareColors.MutedText
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = VetCareColors.OnSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun PremiumCardPreview() {
    VetCareTheme {
        PremiumCard(
            modifier = Modifier
                .padding(VetCareSpacing.md)
                .fillMaxWidth()
        ) {
            Text("Premium Card Content", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(VetCareSpacing.xs))
            Text("Descripción del contenido", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun SoftCardPreview() {
    VetCareTheme {
        SoftCard(
            modifier = Modifier.padding(VetCareSpacing.md)
        ) {
            Text("Soft Card", style = MaterialTheme.typography.labelMedium)
        }
    }
}

