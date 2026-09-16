package de.mindtrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val evidenceLabel: String,
    val recommended: Boolean = false,
    val tried: Boolean = false,
    val helpful: Boolean = false,
    val tensionLow: Boolean = false,
    val tensionMedium: Boolean = false,
    val tensionHigh: Boolean = false,
    val tensionBreakdown: Boolean = false,
    val inToolbox: Boolean = false,
    val sortOrder: Int = 100
)

@Entity(tableName = "skill_usage")
data class SkillUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val skillId: String,
    val skillName: String
)
