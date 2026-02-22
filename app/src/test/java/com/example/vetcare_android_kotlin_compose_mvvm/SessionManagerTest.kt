package com.example.vetcare_android_kotlin_compose_mvvm

import com.example.vetcare_android_kotlin_compose_mvvm.data.model.User
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import com.example.vetcare_android_kotlin_compose_mvvm.data.session.SessionManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para SessionManager
 *
 * Verifica la lógica de gestión de sesión del usuario:
 * - Login y logout
 * - Verificación de roles (Admin/Owner)
 * - Estado de autenticación
 * - Obtención de datos del usuario en sesión
 *
 * Principio testeado: Gestión de estado de autenticación (lógica de negocio)
 *
 * @author Rodrigo Sánchez
 */
class SessionManagerTest {

    // Usuarios de prueba
    private val adminUser = User(
        id = "user-admin",
        name = "Admin VetCare",
        email = "admin@vet.cl",
        passwordHash = "123456",
        role = UserRole.ADMIN,
        ownerId = null
    )

    private val ownerUser = User(
        id = "user-owner",
        name = "Juan Pérez",
        email = "owner@vet.cl",
        passwordHash = "123456",
        role = UserRole.OWNER,
        ownerId = "owner-001"
    )

    @Before
    fun setUp() {
        // Limpiar sesión antes de cada test
        SessionManager.logout()
    }

    @After
    fun tearDown() {
        // Limpiar sesión después de cada test
        SessionManager.logout()
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE LOGIN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `login sets current user correctly`() {
        SessionManager.login(adminUser)

        val currentUser = SessionManager.getCurrentUser()

        assertNotNull(currentUser)
        assertEquals("user-admin", currentUser!!.id)
        assertEquals("Admin VetCare", currentUser.name)
        assertEquals("admin@vet.cl", currentUser.email)
    }

    @Test
    fun `login sets isLoggedIn to true`() {
        assertFalse(SessionManager.isLoggedIn.value)

        SessionManager.login(adminUser)

        assertTrue(SessionManager.isLoggedIn.value)
    }

    @Test
    fun `login updates currentUser StateFlow`() {
        assertNull(SessionManager.currentUser.value)

        SessionManager.login(ownerUser)

        assertNotNull(SessionManager.currentUser.value)
        assertEquals("Juan Pérez", SessionManager.currentUser.value?.name)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE LOGOUT
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `logout clears current user`() {
        SessionManager.login(adminUser)
        assertNotNull(SessionManager.getCurrentUser())

        SessionManager.logout()

        assertNull(SessionManager.getCurrentUser())
    }

    @Test
    fun `logout sets isLoggedIn to false`() {
        SessionManager.login(adminUser)
        assertTrue(SessionManager.isLoggedIn.value)

        SessionManager.logout()

        assertFalse(SessionManager.isLoggedIn.value)
    }

    @Test
    fun `logout clears currentUser StateFlow`() {
        SessionManager.login(ownerUser)

        SessionManager.logout()

        assertNull(SessionManager.currentUser.value)
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE ROLES
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `isAdmin returns true for admin user`() {
        SessionManager.login(adminUser)

        assertTrue(SessionManager.isAdmin())
        assertFalse(SessionManager.isOwner())
    }

    @Test
    fun `isOwner returns true for owner user`() {
        SessionManager.login(ownerUser)

        assertTrue(SessionManager.isOwner())
        assertFalse(SessionManager.isAdmin())
    }

    @Test
    fun `isAdmin returns false when no user is logged in`() {
        assertFalse(SessionManager.isAdmin())
    }

    @Test
    fun `isOwner returns false when no user is logged in`() {
        assertFalse(SessionManager.isOwner())
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE DATOS DE SESIÓN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `getOwnerId returns ownerId for owner user`() {
        SessionManager.login(ownerUser)

        assertEquals("owner-001", SessionManager.getOwnerId())
    }

    @Test
    fun `getOwnerId returns null for admin user`() {
        SessionManager.login(adminUser)

        assertNull(SessionManager.getOwnerId())
    }

    @Test
    fun `getOwnerId returns null when no user is logged in`() {
        assertNull(SessionManager.getOwnerId())
    }

    @Test
    fun `getUserId returns correct id for logged in user`() {
        SessionManager.login(adminUser)
        assertEquals("user-admin", SessionManager.getUserId())

        SessionManager.login(ownerUser)
        assertEquals("user-owner", SessionManager.getUserId())
    }

    @Test
    fun `getUserId returns null when no user is logged in`() {
        assertNull(SessionManager.getUserId())
    }

    // ════════════════════════════════════════════════════════════════
    // TESTS DE CAMBIO DE SESIÓN
    // ════════════════════════════════════════════════════════════════

    @Test
    fun `switching users updates session correctly`() {
        // Login como admin
        SessionManager.login(adminUser)
        assertTrue(SessionManager.isAdmin())
        assertEquals("user-admin", SessionManager.getUserId())

        // Cambiar a owner sin logout
        SessionManager.login(ownerUser)
        assertTrue(SessionManager.isOwner())
        assertFalse(SessionManager.isAdmin())
        assertEquals("user-owner", SessionManager.getUserId())
        assertEquals("owner-001", SessionManager.getOwnerId())
    }

    @Test
    fun `multiple logout calls do not throw`() {
        SessionManager.logout()
        SessionManager.logout()
        SessionManager.logout()

        assertNull(SessionManager.getCurrentUser())
        assertFalse(SessionManager.isLoggedIn.value)
    }
}

