package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.*
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de Login
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: (UserRole) -> Unit,
    onForgotPassword: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    // Efecto para navegar cuando el login sea exitoso
    LaunchedEffect(loginState) {
        if (loginState is AuthUiState.Success) {
            val user = (loginState as AuthUiState.Success).user
            onLoginSuccess(user.role)
            viewModel.resetLoginState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VetCareColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(VetCareSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(VetCareSpacing.xxl * 2))

            // Logo / Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = VetCareColors.Primary.copy(alpha = 0.1f),
                        shape = VetCareShapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "VetCare Logo",
                    modifier = Modifier.size(56.dp),
                    tint = VetCareColors.Primary
                )
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.lg))

            Text(
                text = "VetCare",
                style = MaterialTheme.typography.headlineLarge,
                color = VetCareColors.OnBackground
            )

            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = VetCareColors.MutedText
            )

            Spacer(modifier = Modifier.height(VetCareSpacing.xxl))

            // Formulario
            PremiumCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                ) {
                    EmailField(
                        value = email,
                        onValueChange = viewModel::updateEmail,
                        isError = emailError != null,
                        errorMessage = emailError,
                        enabled = loginState !is AuthUiState.Loading
                    )

                    PasswordField(
                        value = password,
                        onValueChange = viewModel::updatePassword,
                        label = "Contraseña",
                        isError = passwordError != null,
                        errorMessage = passwordError,
                        enabled = loginState !is AuthUiState.Loading,
                        onImeAction = { viewModel.login() }
                    )

                    // Error general
                    AnimatedVisibility(
                        visible = loginState is AuthUiState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (loginState is AuthUiState.Error) {
                            Text(
                                text = (loginState as AuthUiState.Error).message,
                                style = MaterialTheme.typography.bodySmall,
                                color = VetCareColors.Danger,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(VetCareSpacing.xs))

                    PrimaryButton(
                        text = stringResource(R.string.login_button),
                        onClick = { viewModel.login() },
                        isLoading = loginState is AuthUiState.Loading,
                        enabled = loginState !is AuthUiState.Loading
                    )

                    TextLinkButton(
                        text = stringResource(R.string.login_forgot_password),
                        onClick = onForgotPassword,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Credenciales de prueba (solo para desarrollo)
            SoftCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(VetCareSpacing.sm)
                ) {
                    Text(
                        text = "Credenciales de prueba:",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.MutedText
                    )
                    Text(
                        text = "Admin: admin@vet.cl / 123456",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.MutedText
                    )
                    Text(
                        text = "Owner: owner@vet.cl / 123456",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetCareColors.MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(VetCareSpacing.md))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    VetCareTheme {
        LoginScreen(
            onLoginSuccess = {},
            onForgotPassword = {}
        )
    }
}

