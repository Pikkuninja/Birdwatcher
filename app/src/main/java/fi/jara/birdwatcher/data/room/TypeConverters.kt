package fi.jara.birdwatcher.data.room

import androidx.room.TypeConverter
import fi.jara.birdwatcher.observations.ObservationRarity
import java.util.*

// Enums are stored to db as strings instead of ordinal ints so
// reordering cases won't break the conversion and issues created by renaming
// cases should be caught by crashing tests
class RoomTypeConverters {
    @TypeConverter
    fun fromTimestamp(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun fromObservationRarity(rarity: ObservationRarity): String = rarity.name

    @TypeConverter
    fun fromObservationRarityName(name: String): ObservationRarity = ObservationRarity.valueOf(name)
}

