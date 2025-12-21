package com.example.vetcare_android_kotlin_compose_mvvm.data.session

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.User
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor de sesión in-memory para VetCare
 * Singleton que mantiene el estado de autenticación del usuario
 */
object SessionManager {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    /**
     * Inicia sesión con el usuario proporcionado
     */
    fun login(user: User) {
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    /**
     * Cierra la sesión actual
     */
    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    /**
     * Obtiene el usuario actual de forma síncrona
     */
    fun getCurrentUser(): User? = _currentUser.value

    /**
     * Verifica si el usuario actual es administrador
     */
    fun isAdmin(): Boolean = _currentUser.value?.role == UserRole.ADMIN

    /**
     * Verifica si el usuario actual es dueño de mascota
     */
    fun isOwner(): Boolean = _currentUser.value?.role == UserRole.OWNER

    /**
     * Obtiene el ID del owner asociado (para usuarios OWNER)
     */
    fun getOwnerId(): String? = _currentUser.value?.ownerId

    /**
     * Obtiene el ID del usuario actual
     */
    fun getUserId(): String? = _currentUser.value?.id
}

