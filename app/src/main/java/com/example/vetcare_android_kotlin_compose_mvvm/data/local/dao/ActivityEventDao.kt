package com.example.vetcare_android_kotlin_compose_mvvm.data.local.dao

import androidx.room.*
import com.example.vetcare_android_kotlin_compose_mvvm.data.local.entity.ActivityEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de ActivityEvents
 * Proporciona acceso a la tabla activity_events en SQLite
 */
@Dao
interface ActivityEventDao {

    @Query("SELECT * FROM activity_events ORDER BY timestamp DESC")
    fun getAllActivityEvents(): Flow<List<ActivityEventEntity>>

    @Query("SELECT * FROM activity_events ORDER BY timestamp DESC")
    suspend fun getAllActivityEventsList(): List<ActivityEventEntity>

    @Query("SELECT * FROM activity_events WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivityEventsByUser(userId: String): Flow<List<ActivityEventEntity>>

    @Query("SELECT * FROM activity_events WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getActivityEventsByUserList(userId: String): List<ActivityEventEntity>

    @Query("SELECT * FROM activity_events WHERE screen = :screen ORDER BY timestamp DESC")
    fun getActivityEventsByScreen(screen: String): Flow<List<ActivityEventEntity>>

    @Query("SELECT * FROM activity_events WHERE screen = :screen ORDER BY timestamp DESC")
    suspend fun getActivityEventsByScreenList(screen: String): List<ActivityEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityEvent(activityEvent: ActivityEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActivityEvents(activityEvents: List<ActivityEventEntity>)

    @Delete
    suspend fun deleteActivityEvent(activityEvent: ActivityEventEntity)

    @Query("DELETE FROM activity_events")
    suspend fun deleteAllActivityEvents()

    @Query("SELECT COUNT(*) FROM activity_events")
    suspend fun getActivityEventsCount(): Int
}
