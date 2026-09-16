package de.mindtrack.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills ORDER BY recommended DESC, sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<SkillEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(skills: List<SkillEntity>)

    @Query("UPDATE skills SET tried = :value WHERE id = :id")
    suspend fun setTried(id: String, value: Boolean)

    @Query("UPDATE skills SET helpful = :value WHERE id = :id")
    suspend fun setHelpful(id: String, value: Boolean)

    @Query("UPDATE skills SET inToolbox = :value WHERE id = :id")
    suspend fun setInToolbox(id: String, value: Boolean)

    @Query("UPDATE skills SET tensionLow = :low, tensionMedium = :medium, tensionHigh = :high, tensionBreakdown = :breakdown WHERE id = :id")
    suspend fun setTensionLevels(id: String, low: Boolean, medium: Boolean, high: Boolean, breakdown: Boolean)
}

@Dao
interface SkillUsageDao {
    @Query("SELECT * FROM skill_usage ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<SkillUsageEntity>>

    @Insert
    suspend fun insert(usage: SkillUsageEntity): Long
}
