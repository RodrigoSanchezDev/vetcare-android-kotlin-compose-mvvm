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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                    .fallbackToDestructiveMigration()
                    .addCallback(VetCareDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para poblar la base de datos con datos iniciales de demostración.
     *
     * Usa db.execSQL() directamente en lugar de los DAOs de Room para evitar
     * un deadlock: el callback onCreate se ejecuta dentro de una transacción
     * de Room, y usar DAOs (que requieren su propia conexión) desde runBlocking
     * provoca que se bloqueen mutuamente. Con execSQL las inserts corren de
     * forma síncrona dentro de la misma transacción de creación.
     */
    private class VetCareDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            populateDatabase(db)
        }

        private fun populateDatabase(db: SupportSQLiteDatabase) {
            // ============================================
            // USUARIOS
            // ============================================
            db.execSQL(
                """INSERT INTO users (id, name, email, passwordHash, role, ownerId)
                   VALUES ('user-admin-001', 'Administrador VetCare', 'admin@vet.cl', '123456', 'ADMIN', NULL)"""
            )
            db.execSQL(
                """INSERT INTO users (id, name, email, passwordHash, role, ownerId)
                   VALUES ('user-owner-001', 'María González', 'owner@vet.cl', '123456', 'OWNER', 'owner-001')"""
            )

            // ============================================
            // DUEÑOS DE MASCOTAS
            // ============================================
            db.execSQL(
                """INSERT INTO owners (id, fullName, email, phone, address)
                   VALUES ('owner-001', 'María González', 'owner@vet.cl', '+56 9 1234 5678', 'Av. Principal 123, Santiago')"""
            )
            db.execSQL(
                """INSERT INTO owners (id, fullName, email, phone, address)
                   VALUES ('owner-002', 'Carlos Rodríguez', 'carlos@email.cl', '+56 9 8765 4321', 'Calle Los Aromos 456, Providencia')"""
            )

            // ============================================
            // VETERINARIOS
            // ============================================
            db.execSQL(
                """INSERT INTO veterinarians (id, name, specialty, phone, avatarRes)
                   VALUES ('vet-001', 'Dr. Pedro Sánchez', 'Medicina General', '+56 9 1111 2222', ${R.drawable.vet_pedro_gonzalez})"""
            )
            db.execSQL(
                """INSERT INTO veterinarians (id, name, specialty, phone, avatarRes)
                   VALUES ('vet-002', 'Dra. Ana Martínez', 'Cirugía', '+56 9 3333 4444', ${R.drawable.vet_maria_rodriguez})"""
            )
            db.execSQL(
                """INSERT INTO veterinarians (id, name, specialty, phone, avatarRes)
                   VALUES ('vet-003', 'Dr. Luis Torres', 'Dermatología', '+56 9 5555 6666', ${R.drawable.vet_carlos_martinez})"""
            )

            // ============================================
            // MASCOTAS
            // ============================================
            db.execSQL(
                """INSERT INTO pets (id, ownerId, name, species, breed, ageYears, weightKg, photoRes, notes)
                   VALUES ('pet-001', 'owner-001', 'Max', 'DOG', 'West Highland Terrier', 3, 8.5, ${R.drawable.pet_max}, 'Muy activo, le gusta pasear')"""
            )
            db.execSQL(
                """INSERT INTO pets (id, ownerId, name, species, breed, ageYears, weightKg, photoRes, notes)
                   VALUES ('pet-002', 'owner-001', 'Luna', 'CAT', 'Gato Naranja', 2, 4.2, ${R.drawable.pet_luna}, 'Tranquila, le gusta dormir al sol')"""
            )
            db.execSQL(
                """INSERT INTO pets (id, ownerId, name, species, breed, ageYears, weightKg, photoRes, notes)
                   VALUES ('pet-003', 'owner-002', 'Rocky', 'DOG', 'Golden Retriever', 5, 32.0, ${R.drawable.pet_rocky}, 'Muy amigable con otros perros')"""
            )
            db.execSQL(
                """INSERT INTO pets (id, ownerId, name, species, breed, ageYears, weightKg, photoRes, notes)
                   VALUES ('pet-004', 'owner-001', 'Michi', 'CAT', 'Gato Mestizo', 4, 5.0, ${R.drawable.pet_michi}, 'Muy cariñoso')"""
            )

            // ============================================
            // CONSULTAS MÉDICAS
            // ============================================
            val cons1DateTime = LocalDateTime.now().minusDays(30).format(dateTimeFormatter)
            val cons2DateTime = LocalDateTime.now().minusDays(15).format(dateTimeFormatter)
            val cons3DateTime = LocalDateTime.now().minusDays(60).format(dateTimeFormatter)

            db.execSQL(
                """INSERT INTO consultations (id, petId, vetId, dateTime, diagnosis, treatment, notes)
                   VALUES ('cons-001', 'pet-001', 'vet-001', '$cons1DateTime', 'Control de rutina - mascota saludable', 'Vitaminas preventivas por 15 días', 'Próximo control en 6 meses')"""
            )
            db.execSQL(
                """INSERT INTO consultations (id, petId, vetId, dateTime, diagnosis, treatment, notes)
                   VALUES ('cons-002', 'pet-001', 'vet-003', '$cons2DateTime', 'Dermatitis leve en zona abdominal', 'Shampoo medicado + crema tópica', 'Evolución favorable')"""
            )
            db.execSQL(
                """INSERT INTO consultations (id, petId, vetId, dateTime, diagnosis, treatment, notes)
                   VALUES ('cons-003', 'pet-002', 'vet-001', '$cons3DateTime', 'Vacunación anual completada', 'Triple felina aplicada', 'Sin reacciones adversas')"""
            )

            // ============================================
            // CITAS PROGRAMADAS
            // ============================================
            val apt1DateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).format(dateTimeFormatter)
            val apt2DateTime = LocalDateTime.now().plusDays(7).withHour(15).withMinute(30).format(dateTimeFormatter)
            val apt3DateTime = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).format(dateTimeFormatter)

            db.execSQL(
                """INSERT INTO appointments (id, petId, vetId, dateTime, reason, status)
                   VALUES ('apt-001', 'pet-001', 'vet-001', '$apt1DateTime', 'Control post-tratamiento dermatológico', 'CONFIRMED')"""
            )
            db.execSQL(
                """INSERT INTO appointments (id, petId, vetId, dateTime, reason, status)
                   VALUES ('apt-002', 'pet-002', 'vet-002', '$apt2DateTime', 'Esterilización programada', 'SCHEDULED')"""
            )
            db.execSQL(
                """INSERT INTO appointments (id, petId, vetId, dateTime, reason, status)
                   VALUES ('apt-003', 'pet-003', 'vet-001', '$apt3DateTime', 'Vacunación anual', 'CONFIRMED')"""
            )

            // ============================================
            // REGISTROS DE VACUNAS
            // ============================================
            val vac1Last = LocalDate.now().minusMonths(6).format(dateFormatter)
            val vac1Next = LocalDate.now().plusMonths(6).format(dateFormatter)
            val vac2Last = LocalDate.now().minusMonths(10).format(dateFormatter)
            val vac2Next = LocalDate.now().plusDays(5).format(dateFormatter)
            val vac3Last = LocalDate.now().minusMonths(2).format(dateFormatter)
            val vac3Next = LocalDate.now().plusMonths(10).format(dateFormatter)
            val vac4Last = LocalDate.now().minusMonths(11).format(dateFormatter)
            val vac4Next = LocalDate.now().plusDays(3).format(dateFormatter)

            db.execSQL(
                """INSERT INTO vaccine_records (id, petId, vaccineName, lastDate, nextDueDate)
                   VALUES ('vac-001', 'pet-001', 'Antirrábica', '$vac1Last', '$vac1Next')"""
            )
            db.execSQL(
                """INSERT INTO vaccine_records (id, petId, vaccineName, lastDate, nextDueDate)
                   VALUES ('vac-002', 'pet-001', 'Séxtuple', '$vac2Last', '$vac2Next')"""
            )
            db.execSQL(
                """INSERT INTO vaccine_records (id, petId, vaccineName, lastDate, nextDueDate)
                   VALUES ('vac-003', 'pet-002', 'Triple Felina', '$vac3Last', '$vac3Next')"""
            )
            db.execSQL(
                """INSERT INTO vaccine_records (id, petId, vaccineName, lastDate, nextDueDate)
                   VALUES ('vac-004', 'pet-002', 'Antirrábica', '$vac4Last', '$vac4Next')"""
            )
        }
    }
}
