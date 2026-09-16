package de.mindtrack.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingEventDao {
    @Query("SELECT * FROM tracking_events ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<TrackingEventEntity>>

    @Insert
    suspend fun insert(event: TrackingEventEntity): Long

    @Query("UPDATE tracking_events SET context = :context WHERE id = :eventId")
    suspend fun updateContext(eventId: Long, context: String?)

    @Query("DELETE FROM tracking_events")
    suspend fun deleteAll()
}
