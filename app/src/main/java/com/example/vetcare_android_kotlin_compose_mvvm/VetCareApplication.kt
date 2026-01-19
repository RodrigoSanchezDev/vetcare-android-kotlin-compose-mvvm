package com.example.vetcare_android_kotlin_compose_mvvm

import android.app.Application
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository

/**
 * Application class para VetCare
 * Inicializa el repositorio Room al inicio de la aplicación
 */
class VetCareApplication : Application() {

    // Repositorio con persistencia Room (SQLite)
    lateinit var repository: VetCareRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Inicializar el repositorio con Room Database
        repository = VetCareRepository.getInstance(this)
    }

    companion object {
        lateinit var instance: VetCareApplication
            private set

        /**
         * Obtiene el repositorio desde cualquier lugar de la app
         */
        fun getRepository(): VetCareRepository = instance.repository
    }
}
