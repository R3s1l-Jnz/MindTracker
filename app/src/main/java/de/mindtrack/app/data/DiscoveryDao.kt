package de.mindtrack.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscoveryDao {
    @Query("SELECT * FROM discoveries ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<DiscoveryEntity>>

    @Insert
    suspend fun insert(discovery: DiscoveryEntity): Long
}

@Dao
interface ContextTagDao {
    @Query("SELECT * FROM context_tags ORDER BY favorite DESC, name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ContextTagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tags: List<ContextTagEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: ContextTagEntity)
}
