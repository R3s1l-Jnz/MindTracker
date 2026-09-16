package de.mindtrack.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDimension(value: TrackingDimension): String = value.name

    @TypeConverter
    fun toDimension(value: String): TrackingDimension = TrackingDimension.valueOf(value)

    @TypeConverter
    fun fromEventType(value: TrackingEventType): String = value.name

    @TypeConverter
    fun toEventType(value: String): TrackingEventType = TrackingEventType.valueOf(value)
}
