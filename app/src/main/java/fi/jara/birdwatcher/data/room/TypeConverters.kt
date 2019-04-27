package fi.jara.birdwatcher.data.room

import androidx.room.TypeConverter
import fi.jara.birdwatcher.sightings.SightingRarity
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
    fun fromSightingRarity(rarity: SightingRarity): String = rarity.name

    @TypeConverter
    fun fromSightingRarityName(name: String): SightingRarity = SightingRarity.valueOf(name)
}

