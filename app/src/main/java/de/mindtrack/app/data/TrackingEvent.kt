package de.mindtrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TrackingDimension {
    ENERGY,
    TENSION
}

enum class TrackingEventType {
    LEVEL,
    INCREASE,
    DECREASE,
    BREAKDOWN
}

@Entity(tableName = "tracking_events")
data class TrackingEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dimension: TrackingDimension,
    val type: TrackingEventType,
    val level: Int? = null,
    val context: String? = null
)
