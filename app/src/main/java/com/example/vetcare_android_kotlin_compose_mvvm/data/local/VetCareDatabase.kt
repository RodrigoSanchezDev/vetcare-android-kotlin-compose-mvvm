package com.example.vetcare_android_kotlin_compose_mvvm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vetcare_android_kotlin_compose_mvvm.R
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.AppointmentStatus
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.PetSpecies
import com.example.vetcare_android_kotlin_compose_mvvm.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * VetCare Room Database
 * Base de datos SQLite con persistencia local para la aplicación VetCare
 *
 * Incluye todas las tablas necesarias:
 * - users: Usuarios del sistema (autenticación)
 * - owners: Dueños de mascotas
 * - pets: Mascotas registradas
 * - veterinarians: Veterinarios del staff
 * - appointments: Citas agendadas
 * - consultations: Consultas médicas
 * - vaccine_records: Registros de vacunas
 * - activity_events: Log de actividad
 */
@Database(
    entities = [
        UserEntity::class,
        OwnerEntity::class,
        PetEntity::class,
        VeterinarianEntity::class,
        AppointmentEntity::class,
        ConsultationEntity::class,
        VaccineRecordEntity::class,
        ActivityEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VetCareDatabase : RoomDatabase() {

    // DAOs
    abstract fun userDao(): UserDao
    abstract fun ownerDao(): OwnerDao
    abstract fun petDao(): PetDao
    abstract fun veterinarianDao(): VeterinarianDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun consultationDao(): ConsultationDao
    abstract fun vaccineRecordDao(): VaccineRecordDao
    abstract fun activityEventDao(): ActivityEventDao

    companion object {
        @Volatile
        private var INSTANCE: VetCareDatabase? = null

        /**
         * Obtiene la instancia singleton de la base de datos
         * Crea la base de datos si no existe e inserta datos iniciales de demostración
         */
        fun getDatabase(context: Context): VetCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VetCareDatabase::class.java,
                    "vetcare_database"
                )
                    .addCallback(VetCareDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para poblar la base de datos con datos iniciales de demostración
     */
    private class VetCareDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        /**
         * Inserta datos de demostración en la base de datos
         * Estos datos se mantienen persistidos incluso al cerrar la app
         */
        private suspend fun populateDatabase(database: VetCareDatabase) {
            // ============================================
            // USUARIOS
            // ============================================
            val users = listOf(
                UserEntity(
                    id = "user-admin-001",
                    name = "Administrador VetCare",
                    email = "admin@vet.cl",
                    passwordHash = "123456",
                    role = UserRole.ADMIN,
                    ownerId = null
                ),
                UserEntity(
                    id = "user-owner-001",
                    name = "María González",
                    email = "owner@vet.cl",
                    passwordHash = "123456",
                    role = UserRole.OWNER,
                    ownerId = "owner-001"
                )
            )
            database.userDao().insertAllUsers(users)

            // ============================================
            // DUEÑOS DE MASCOTAS
            // ============================================
            val owners = listOf(
                OwnerEntity(
                    id = "owner-001",
                    fullName = "María González",
                    email = "owner@vet.cl",
                    phone = "+56 9 1234 5678",
                    address = "Av. Principal 123, Santiago"
                ),
                OwnerEntity(
                    id = "owner-002",
                    fullName = "Carlos Rodríguez",
                    email = "carlos@email.cl",
                    phone = "+56 9 8765 4321",
                    address = "Calle Los Aromos 456, Providencia"
                )
            )
            database.ownerDao().insertAllOwners(owners)

            // ============================================
            // VETERINARIOS
            // ============================================
            val veterinarians = listOf(
                VeterinarianEntity(
                    id = "vet-001",
                    name = "Dr. Pedro Sánchez",
                    specialty = "Medicina General",
                    phone = "+56 9 1111 2222",
                    avatarRes = R.drawable.vet_pedro_gonzalez
                ),
                VeterinarianEntity(
                    id = "vet-002",
                    name = "Dra. Ana Martínez",
                    specialty = "Cirugía",
                    phone = "+56 9 3333 4444",
                    avatarRes = R.drawable.vet_maria_rodriguez
                ),
                VeterinarianEntity(
                    id = "vet-003",
                    name = "Dr. Luis Torres",
                    specialty = "Dermatología",
                    phone = "+56 9 5555 6666",
                    avatarRes = R.drawable.vet_carlos_martinez
                )
            )
            database.veterinarianDao().insertAllVeterinarians(veterinarians)

            // ============================================
            // MASCOTAS
            // ============================================
            val pets = listOf(
                PetEntity(
                    id = "pet-001",
                    ownerId = "owner-001",
                    name = "Max",
                    species = PetSpecies.DOG,
                    breed = "West Highland Terrier",
                    ageYears = 3,
                    weightKg = 8.5,
                    photoRes = R.drawable.pet_max,
                    notes = "Muy activo, le gusta pasear"
                ),
                PetEntity(
                    id = "pet-002",
                    ownerId = "owner-001",
                    name = "Luna",
                    species = PetSpecies.CAT,
                    breed = "Gato Naranja",
                    ageYears = 2,
                    weightKg = 4.2,
                    photoRes = R.drawable.pet_luna,
                    notes = "Tranquila, le gusta dormir al sol"
                ),
                PetEntity(
                    id = "pet-003",
                    ownerId = "owner-002",
                    name = "Rocky",
                    species = PetSpecies.DOG,
                    breed = "Golden Retriever",
                    ageYears = 5,
                    weightKg = 32.0,
                    photoRes = R.drawable.pet_rocky,
                    notes = "Muy amigable con otros perros"
                ),
                PetEntity(
                    id = "pet-004",
                    ownerId = "owner-001",
                    name = "Michi",
                    species = PetSpecies.CAT,
                    breed = "Gato Mestizo",
                    ageYears = 4,
                    weightKg = 5.0,
                    photoRes = R.drawable.pet_michi,
                    notes = "Muy cariñoso"
                )
            )
            database.petDao().insertAllPets(pets)

            // ============================================
            // CONSULTAS MÉDICAS
            // ============================================
            val consultations = listOf(
                ConsultationEntity(
                    id = "cons-001",
                    petId = "pet-001",
                    vetId = "vet-001",
                    dateTime = LocalDateTime.now().minusDays(30),
                    diagnosis = "Control de rutina - mascota saludable",
                    treatment = "Vitaminas preventivas por 15 días",
                    notes = "Próximo control en 6 meses"
                ),
                ConsultationEntity(
                    id = "cons-002",
                    petId = "pet-001",
                    vetId = "vet-003",
                    dateTime = LocalDateTime.now().minusDays(15),
                    diagnosis = "Dermatitis leve en zona abdominal",
                    treatment = "Shampoo medicado + crema tópica",
                    notes = "Evolución favorable"
                ),
                ConsultationEntity(
                    id = "cons-003",
                    petId = "pet-002",
                    vetId = "vet-001",
                    dateTime = LocalDateTime.now().minusDays(60),
                    diagnosis = "Vacunación anual completada",
                    treatment = "Triple felina aplicada",
                    notes = "Sin reacciones adversas"
                )
            )
            database.consultationDao().insertAllConsultations(consultations)

            // ============================================
            // CITAS PROGRAMADAS
            // ============================================
            val appointments = listOf(
                AppointmentEntity(
                    id = "apt-001",
                    petId = "pet-001",
                    vetId = "vet-001",
                    dateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                    reason = "Control post-tratamiento dermatológico",
                    status = AppointmentStatus.CONFIRMED
                ),
                AppointmentEntity(
                    id = "apt-002",
                    petId = "pet-002",
                    vetId = "vet-002",
                    dateTime = LocalDateTime.now().plusDays(7).withHour(15).withMinute(30),
                    reason = "Esterilización programada",
                    status = AppointmentStatus.SCHEDULED
                ),
                AppointmentEntity(
                    id = "apt-003",
                    petId = "pet-003",
                    vetId = "vet-001",
                    dateTime = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0),
                    reason = "Vacunación anual",
                    status = AppointmentStatus.CONFIRMED
                )
            )
            database.appointmentDao().insertAllAppointments(appointments)

            // ============================================
            // REGISTROS DE VACUNAS
            // ============================================
            val vaccineRecords = listOf(
                VaccineRecordEntity(
                    id = "vac-001",
                    petId = "pet-001",
                    vaccineName = "Antirrábica",
                    lastDate = LocalDate.now().minusMonths(6),
                    nextDueDate = LocalDate.now().plusMonths(6)
                ),
                VaccineRecordEntity(
                    id = "vac-002",
                    petId = "pet-001",
                    vaccineName = "Séxtuple",
                    lastDate = LocalDate.now().minusMonths(10),
                    nextDueDate = LocalDate.now().plusDays(5)
                ),
                VaccineRecordEntity(
                    id = "vac-003",
                    petId = "pet-002",
                    vaccineName = "Triple Felina",
                    lastDate = LocalDate.now().minusMonths(2),
                    nextDueDate = LocalDate.now().plusMonths(10)
                ),
                VaccineRecordEntity(
                    id = "vac-004",
                    petId = "pet-002",
                    vaccineName = "Antirrábica",
                    lastDate = LocalDate.now().minusMonths(11),
                    nextDueDate = LocalDate.now().plusDays(3)
                )
            )
            database.vaccineRecordDao().insertAllVaccineRecords(vaccineRecords)
        }
    }
}
