package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de Reseteo de Contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val resetState by viewModel.resetState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TopBar
            TopAppBar(
                title = {
                    Text(
                        "Recuperar Contraseña",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetResetState()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Background
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(VetCareSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(VetCareSpacing.xxl))

                // Icono
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = VetCareColors.Primary.copy(alpha = 0.1f),
                            shape = VetCareShapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (resetState is ResetPasswordUiState.Success)
                            Icons.Default.CheckCircle else Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = if (resetState is ResetPasswordUiState.Success)
                            VetCareColors.Success else VetCareColors.Primary
                    )
                }

                Spacer(modifier = Modifier.height(VetCareSpacing.lg))

                // Estado de éxito
                AnimatedVisibility(
                    visible = resetState is ResetPasswordUiState.Success,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "¡Contraseña enviada!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = VetCareColors.OnBackground
                        )

                        Text(
                            text = "Se ha enviado una contraseña temporal a tu correo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VetCareColors.MutedText,
                            textAlign = TextAlign.Center
                        )

                        // Mostrar contraseña temporal (solo para demo)
                        if (resetState is ResetPasswordUiState.Success) {
                            Spacer(modifier = Modifier.height(VetCareSpacing.md))

                            PremiumCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Contraseña temporal (demo):",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = VetCareColors.MutedText
                                    )
                                    Text(
                                        text = (resetState as ResetPasswordUiState.Success).temporaryPassword,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = VetCareColors.Primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(VetCareSpacing.lg))

                        PrimaryButton(
                            text = "Volver al Login",
                            onClick = {
                                viewModel.resetResetState()
                                onBack()
                            }
                        )
                    }
                }

                // Formulario de reset
                AnimatedVisibility(
                    visible = resetState !is ResetPasswordUiState.Success,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = VetCareColors.OnBackground
                        )

                        Text(
                            text = "Ingresa tu email y te enviaremos una contraseña temporal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VetCareColors.MutedText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(VetCareSpacing.md))

                        PremiumCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                            ) {
                                EmailField(
                                    value = email,
                                    onValueChange = { email = it },
                                    enabled = resetState !is ResetPasswordUiState.Loading,
                                    isError = resetState is ResetPasswordUiState.Error,
                                    errorMessage = if (resetState is ResetPasswordUiState.Error)
                                        (resetState as ResetPasswordUiState.Error).message else null
                                )

                                PrimaryButton(
                                    text = "Enviar",
                                    onClick = { viewModel.resetPassword(email) },
                                    isLoading = resetState is ResetPasswordUiState.Loading,
                                    enabled = resetState !is ResetPasswordUiState.Loading
                                )
                            }
                        }

                        TextLinkButton(
                            text = "Volver al login",
                            onClick = {
                                viewModel.resetResetState()
                                onBack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordScreenPreview() {
    VetCareTheme {
        ResetPasswordScreen(onBack = {})
    }
}

