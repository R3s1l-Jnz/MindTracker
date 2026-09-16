package de.mindtrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discoveries")
data class DiscoveryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val text: String,
    val affectsEnergy: Boolean = false,
    val affectsTension: Boolean = false,
    val energyDirection: Int? = null,
    val tensionDirection: Int? = null,
    val contextTag: String? = null
)

@Entity(tableName = "context_tags")
data class ContextTagEntity(
    @PrimaryKey val name: String,
    val source: String = "default",
    val favorite: Boolean = false
)
