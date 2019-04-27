package fi.jara.birdwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [SightingEntity::class],
    version = 1,
    exportSchema = true)
@TypeConverters(RoomTypeConverters::class)
abstract class SightingDatabase: RoomDatabase() {
    abstract fun sightingDao(): SightingDao
}