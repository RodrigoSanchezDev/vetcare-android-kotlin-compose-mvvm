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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import com.example.vetcare_android_kotlin_compose_mvvm.ui.components.PremiumCard
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Pantalla de Ajustes con opciones de accesibilidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { ThemeSettingsRepository.getInstance(context) }

    // Estado del tema - usando collectAsState sin initial ya que el singleton ya tiene el valor
    val themeSettings by repository.themeSettings.collectAsState()

    val currentUser = SessionManager.getCurrentUser()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Diálogo de selección de tema
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            icon = {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = VetCareColors.Primary
                )
            },
            title = {
                Text(
                    text = "Seleccionar tema",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeOption(
                        title = "Claro",
                        icon = Icons.Default.LightMode,
                        isSelected = themeSettings.themeMode == ThemeMode.LIGHT,
                        onClick = {
                            repository.setThemeMode(ThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "Oscuro",
                        icon = Icons.Default.DarkMode,
                        isSelected = themeSettings.themeMode == ThemeMode.DARK,
                        onClick = {
                            repository.setThemeMode(ThemeMode.DARK)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "Automático (Sistema)",
                        icon = Icons.Default.BrightnessAuto,
                        isSelected = themeSettings.themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            repository.setThemeMode(ThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

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

            // Sección Apariencia
            item {
                SettingsSectionHeader(title = "Apariencia")
            }

            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.DarkMode,
                            title = "Tema",
                            subtitle = when (themeSettings.themeMode) {
                                ThemeMode.LIGHT -> "Claro"
                                ThemeMode.DARK -> "Oscuro"
                                ThemeMode.SYSTEM -> "Automático"
                            },
                            onClick = { showThemeDialog = true }
                        )
                    }
                }
            }

            // Sección Accesibilidad
            item {
                SettingsSectionHeader(title = "Accesibilidad")
            }

            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Alto Contraste
                        SettingsToggleItem(
                            icon = Icons.Default.Contrast,
                            title = "Alto contraste",
                            subtitle = "Mejora la legibilidad con colores más intensos",
                            isChecked = themeSettings.highContrast,
                            onCheckedChange = { enabled ->
                                repository.setHighContrast(enabled)
                            }
                        )

                        HorizontalDivider(color = VetCareColors.Divider)

                        // Reducir movimiento
                        SettingsToggleItem(
                            icon = Icons.Default.Animation,
                            title = "Reducir animaciones",
                            subtitle = "Minimiza las animaciones de la interfaz",
                            isChecked = themeSettings.reduceMotion,
                            onCheckedChange = { enabled ->
                                repository.setReduceMotion(enabled)
                            }
                        )
                    }
                }
            }

            // Info sobre TalkBack
            item {
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = VetCareColors.Primary.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(VetCareSpacing.sm)
                    ) {
                        Icon(
                            Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = VetCareColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Lectura en voz alta",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = VetCareColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Esta app es compatible con TalkBack, el lector de pantalla de Android. " +
                                      "Actívalo desde Configuración > Accesibilidad > TalkBack en tu dispositivo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = VetCareColors.MutedText
                            )
                        }
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
private fun ThemeOption(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title${if (isSelected) ", seleccionado" else ""}"
            },
        shape = VetCareShapes.small,
        color = if (isSelected) VetCareColors.Primary.copy(alpha = 0.1f) else VetCareColors.Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) VetCareColors.Primary else VetCareColors.MutedText
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) VetCareColors.Primary else VetCareColors.OnSurface,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = VetCareColors.Primary
                )
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
            .padding(VetCareSpacing.md)
            .semantics { contentDescription = "$title${subtitle?.let { ", $it" } ?: ""}" },
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

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(VetCareSpacing.md)
            .semantics {
                contentDescription = "$title${subtitle?.let { ", $it" } ?: ""}, ${if (isChecked) "activado" else "desactivado"}"
            },
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

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VetCareColors.Surface,
                checkedTrackColor = VetCareColors.Primary,
                uncheckedThumbColor = VetCareColors.Surface,
                uncheckedTrackColor = VetCareColors.MutedText.copy(alpha = 0.3f)
            )
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

