package com.example.vetcare_android_kotlin_compose_mvvm.data.remote

/**
 * Clase sellada para representar resultados de operaciones de red
 *
 * Este patrón permite manejar de forma type-safe los tres estados
 * posibles de una operación de red: éxito, error y cargando.
 *
 * Uso recomendado:
 * ```kotlin
 * when (result) {
 *     is NetworkResult.Success -> handleSuccess(result.data)
 *     is NetworkResult.Error -> handleError(result.message)
 *     is NetworkResult.Loading -> showLoading()
 * }
 * ```
 *
 * @param T Tipo de datos en caso de éxito
 * @author Rodrigo Sánchez
 * @version 1.0
 */
sealed class NetworkResult<out T> {

    /**
     * Estado de éxito con datos
     * @param data Datos recibidos del servidor
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * Estado de error con mensaje descriptivo
     * @param message Mensaje de error para mostrar al usuario
     * @param code Código HTTP del error (opcional)
     * @param exception Excepción original (para logging)
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : NetworkResult<Nothing>()

    /**
     * Estado de carga en progreso
     */
    data object Loading : NetworkResult<Nothing>()

    /**
     * Verifica si el resultado es exitoso
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Verifica si el resultado es un error
     */
    val isError: Boolean get() = this is Error

    /**
     * Verifica si está cargando
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Obtiene los datos si es exitoso, null en otro caso
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Obtiene los datos si es exitoso, o lanza excepción
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: Exception(message)
        is Loading -> throw IllegalStateException("Cannot get data while loading")
    }

    /**
     * Transforma los datos si es exitoso
     */
    inline fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    /**
     * Ejecuta una acción si es exitoso
     */
    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Ejecuta una acción si es error
     */
    inline fun onError(action: (String, Int?, Throwable?) -> Unit): NetworkResult<T> {
        if (this is Error) action(message, code, exception)
        return this
    }

    /**
     * Ejecuta una acción si está cargando
     */
    inline fun onLoading(action: () -> Unit): NetworkResult<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        /**
         * Crea un resultado exitoso
         */
        fun <T> success(data: T): NetworkResult<T> = Success(data)

        /**
         * Crea un resultado de error
         */
        fun error(
            message: String,
            code: Int? = null,
            exception: Throwable? = null
        ): NetworkResult<Nothing> = Error(message, code, exception)

        /**
         * Crea un estado de carga
         */
        fun loading(): NetworkResult<Nothing> = Loading
    }
}

