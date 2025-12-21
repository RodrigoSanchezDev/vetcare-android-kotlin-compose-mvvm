package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.PremiumCard
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de Ajustes con opción de Logout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val currentUser = SessionManager.getCurrentUser()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = VetCareColors.Danger
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.settings_logout_confirm_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_logout_confirm_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Log de logout antes de cerrar sesión
                        ActivityLogger.logLogout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VetCareColors.Danger
                    )
                ) {
                    Text(stringResource(R.string.settings_logout_confirm_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.settings_logout_confirm_no))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VetCareColors.Background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(VetCareColors.Background)
                .padding(paddingValues),
            contentPadding = PaddingValues(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            // Perfil del usuario
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(VetCareColors.Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.take(2)?.uppercase() ?: "US",
                                style = MaterialTheme.typography.titleLarge,
                                color = VetCareColors.Primary
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.name ?: "Usuario",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = VetCareColors.OnSurface
                            )
                            Text(
                                text = currentUser?.email ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = VetCareColors.MutedText
                            )
                            Text(
                                text = if (currentUser?.role?.name == "ADMIN") "Administrador" else "Dueño de mascota",
                                style = MaterialTheme.typography.labelSmall,
                                color = VetCareColors.Primary
                            )
                        }
                    }
                }
            }

            // Sección Cuenta
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_account))
            }

            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = stringResource(R.string.settings_profile),
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = VetCareColors.Divider)
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = stringResource(R.string.settings_notifications),
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }

            // Sección Aplicación
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_app))
            }

            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Language,
                            title = stringResource(R.string.settings_language),
                            subtitle = stringResource(R.string.settings_language_es),
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(color = VetCareColors.Divider)
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = stringResource(R.string.settings_about),
                            subtitle = stringResource(R.string.settings_version),
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }

            // Botón Logout
            item {
                Spacer(modifier = Modifier.height(VetCareSpacing.md))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = VetCareShapes.medium,
                    color = VetCareColors.Danger.copy(alpha = 0.1f),
                    onClick = { showLogoutDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(VetCareSpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = VetCareColors.Danger,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(VetCareSpacing.sm))
                        Text(
                            text = stringResource(R.string.settings_logout),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = VetCareColors.Danger
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(VetCareSpacing.xxl))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = VetCareColors.MutedText,
        modifier = Modifier.padding(vertical = VetCareSpacing.xs)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(VetCareSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VetCareColors.Primary,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = VetCareColors.OnSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = VetCareColors.MutedText
                )
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = VetCareColors.MutedText,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    VetCareTheme {
        SettingsScreen()
    }
}

