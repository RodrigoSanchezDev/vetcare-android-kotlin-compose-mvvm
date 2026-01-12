package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Pantalla de Onboarding - Estilo Premium VetCare
 * Diseño basado en referencia: fondo morado, ilustración central, botón circular dorado
 */
@Composable
fun OnboardingScreen(
    onContinue: () -> Unit = {}
) {
    // Animación de pulso para el botón
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VetCareColors.Primary,
                        VetCareColors.PrimaryDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(VetCareSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            // Ilustración central con fondo
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(VetCareShapes.extraLarge)
                    .background(
                        color = Color.White.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Icono de mascota grande
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto de Max (mascota)
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pet_max),
                            contentDescription = "Max - Mascota",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(VetCareSpacing.md))

                    // Iconos decorativos
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.lg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                        Icon(
                            imageVector = Icons.Default.Vaccines,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Textos en español
            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.md))

            Text(
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(0.2f))

            // Botón circular dorado con animación
            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .scale(scale),
                shape = CircleShape,
                color = VetCareColors.Accent,
                shadowElevation = 8.dp,
                onClick = onContinue
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Flechas dobles
                    Row {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.onboarding_continue),
                            tint = VetCareColors.OnSurface,
                            modifier = Modifier.size(28.dp)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = VetCareColors.OnSurface,
                            modifier = Modifier
                                .size(28.dp)
                                .offset(x = (-12).dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.xxl))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    VetCareTheme {
        OnboardingScreen()
    }
}

