package com.example.vetcare_android_kotlin_compose_mvvm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.vetcare_android_kotlin_compose_mvvm.notifications.NotificationHelper
import com.example.vetcare_android_kotlin_compose_mvvm.ui.navigation.AppNavGraph
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareColors
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.VetCareTheme

class MainActivity : ComponentActivity() {

    // Launcher para solicitar permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permiso concedido o denegado - no hacemos nada especial aquí
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canales de notificación
        NotificationHelper.createNotificationChannels(this)

        // Solicitar permiso de notificaciones en Android 13+
        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            VetCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VetCareColors.Background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        // Solo necesario en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

