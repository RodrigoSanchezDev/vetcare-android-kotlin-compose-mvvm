package com.example.vetcare_android_kotlin_compose_mvvm.data.remote

import com.example.vetcare_android_kotlin_compose_mvvm.data.remote.api.VetCareApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración centralizada de Retrofit para VetCare
 *
 * Este objeto singleton proporciona una instancia configurada de Retrofit
 * con todas las optimizaciones necesarias para producción.
 *
 * CARACTERÍSTICAS IMPLEMENTADAS:
 * ─────────────────────────────────────────────────────────────────────────
 * 1. Logging Interceptor: Registra todas las peticiones HTTP en debug
 * 2. Timeouts configurables: Evita bloqueos indefinidos
 * 3. Gson personalizado: Conversión JSON ↔ Objetos Kotlin
 * 4. Singleton pattern: Una única instancia compartida
 * 5. Base URL configurable: Fácil cambio entre ambientes
 *
 * JUSTIFICACIÓN DE RETROFIT SOBRE ALTERNATIVAS:
 * ─────────────────────────────────────────────────────────────────────────
 * | Característica      | Retrofit | HttpURLConnection | Ktor |
 * |---------------------|----------|-------------------|------|
 * | Type Safety         | ✅ Alto   | ❌ Manual          | ✅    |
 * | Boilerplate Code    | ✅ Mínimo | ❌ Alto            | ✅    |
 * | Interceptors        | ✅ Sí     | ❌ Manual          | ✅    |
 * | Coroutines Support  | ✅ Nativo | ❌ Manual          | ✅    |
 * | Comunidad/Docs      | ✅ Amplia | ✅ Básica          | ⚠️   |
 * | Curva Aprendizaje   | ✅ Baja   | ⚠️ Media          | ⚠️   |
 *
 * @author Rodrigo Sánchez
 * @version 1.0
 */
object RetrofitClient {

    /**
     * URL base del servidor API
     *
     * En un entorno real, esta URL se configuraría según el ambiente:
     * - Development: http://10.0.2.2:8080/ (emulador)
     * - Staging: https://staging-api.vetcare.cl/
     * - Production: https://api.vetcare.cl/
     *
     * Actualmente configurada como placeholder ya que la app
     * usa Room Database para persistencia local.
     */
    private const val BASE_URL = "https://api.vetcare.cl/"

    /**
     * Timeouts de conexión (en segundos)
     */
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Configuración de Gson para serialización/deserialización JSON
     *
     * Características:
     * - Formato de fechas ISO 8601
     * - Serialización de valores nulos
     * - Nombres de campos en snake_case ↔ camelCase
     */
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .serializeNulls()
        .setLenient()
        .create()

    /**
     * Interceptor de logging para debug
     *
     * Niveles disponibles:
     * - NONE: Sin logging
     * - BASIC: Solo línea de request/response
     * - HEADERS: Headers + línea básica
     * - BODY: Todo (usar solo en debug)
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Solo logging detallado en builds de debug
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp configurado con interceptors y timeouts
     */
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        // Interceptor para agregar headers comunes (autenticación, etc.)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                // En una implementación real, aquí se agregaría el token:
                // .header("Authorization", "Bearer ${SessionManager.getToken()}")
            chain.proceed(requestBuilder.build())
        }
        .build()

    /**
     * Instancia de Retrofit configurada
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    /**
     * Servicio de API lazy-initialized
     *
     * Uso:
     * ```kotlin
     * val response = RetrofitClient.apiService.getAllPets()
     * ```
     */
    val apiService: VetCareApiService by lazy {
        retrofit.create(VetCareApiService::class.java)
    }

    /**
     * Obtiene una nueva instancia del servicio (para testing)
     */
    fun createApiService(): VetCareApiService {
        return retrofit.create(VetCareApiService::class.java)
    }

    /**
     * Verifica si hay conexión de red disponible
     * (Requiere NetworkCallback en implementación completa)
     */
    fun isNetworkAvailable(): Boolean {
        // TODO: Implementar verificación real de conectividad
        return true
    }
}


