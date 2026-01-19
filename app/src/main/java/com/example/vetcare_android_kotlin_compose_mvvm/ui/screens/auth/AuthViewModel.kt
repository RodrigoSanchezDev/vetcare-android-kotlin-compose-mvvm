package com.example.vetcare_android_kotlin_compose_mvvm.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetcare_android_kotlin_compose_mvvm.VetCareApplication
import com.example.vetcare_android_kotlin_compose_mvvm.data.logging.ActivityLogger
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.User
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import com.example.vetcare_android_kotlin_compose_mvvm.data.repository.VetCareRepository
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles de la UI de autenticación
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * Estados para la pantalla de reset password
 */
sealed class ResetPasswordUiState {
    object Idle : ResetPasswordUiState()
    object Loading : ResetPasswordUiState()
    data class Success(val temporaryPassword: String) : ResetPasswordUiState()
    data class Error(val message: String) : ResetPasswordUiState()
}

/**
 * ViewModel para autenticación
 * Usa VetCareRepository con Room Database para persistencia local
 */
class AuthViewModel : ViewModel() {

    // Repositorio con persistencia Room (SQLite)
    private val repository: VetCareRepository = VetCareApplication.getRepository()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _resetState = MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.Idle)
    val resetState: StateFlow<ResetPasswordUiState> = _resetState.asStateFlow()

    // Campos de formulario
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _passwordError.value = null
    }

    /**
     * Validar formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validar campos del formulario
     */
    private fun validateLoginForm(): Boolean {
        var isValid = true

        if (_email.value.isBlank()) {
            _emailError.value = "El email es requerido"
            isValid = false
        } else if (!isValidEmail(_email.value)) {
            _emailError.value = "Formato de email inválido"
            isValid = false
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "La contraseña es requerida"
            isValid = false
        } else if (_password.value.length < 6) {
            _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        return isValid
    }

    /**
     * Intentar login con credenciales
     * Busca en Room Database (SQLite)
     */
    fun login() {
        if (!validateLoginForm()) return

        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading

            // Simular delay de red
            delay(1000)

            // Buscar usuario en Room Database
            val user = repository.authenticateUser(_email.value, _password.value)

            if (user != null) {
                SessionManager.login(user)
                // Log de login exitoso
                ActivityLogger.log(
                    screen = ActivityLogger.Screens.LOGIN,
                    action = ActivityLogger.Actions.LOGIN,
                    metadata = mapOf("role" to user.role.name)
                )
                _loginState.value = AuthUiState.Success(user)
            } else {
                _loginState.value = AuthUiState.Error("Credenciales inválidas")
            }
        }
    }

    /**
     * Reset password - simula envío de contraseña temporal
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _resetState.value = ResetPasswordUiState.Error("Ingrese un email válido")
                return@launch
            }

            if (!isValidEmail(email)) {
                _resetState.value = ResetPasswordUiState.Error("Formato de email inválido")
                return@launch
            }

            _resetState.value = ResetPasswordUiState.Loading

            // Simular delay de red
            delay(1500)

            // Verificar si el usuario existe en Room Database
            val user = repository.findUserByEmail(email)

            if (user != null) {
                // Generar contraseña temporal simulada
                val tempPassword = "temp${(1000..9999).random()}"
                _resetState.value = ResetPasswordUiState.Success(tempPassword)
            } else {
                _resetState.value = ResetPasswordUiState.Error("No existe una cuenta con este email")
            }
        }
    }

    /**
     * Resetear estado de login
     */
    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }

    /**
     * Resetear estado de reset password
     */
    fun resetResetState() {
        _resetState.value = ResetPasswordUiState.Idle
    }

    /**
     * Limpiar formulario
     */
    fun clearForm() {
        _email.value = ""
        _password.value = ""
        _emailError.value = null
        _passwordError.value = null
    }
}

