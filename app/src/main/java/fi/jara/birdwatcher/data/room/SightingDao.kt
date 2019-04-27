package fi.jara.birdwatcher.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/*
    As Room supports parameters only in the same way that SQLites compiled statements do, an order by column name
    can't be substituted with a parameter. Result: this mess... Maybe switch to raw queries?
 */

@Dao
interface SightingDao {
    @Query("SELECT * FROM entities ORDER BY species DESC")
    fun loadAllSightingsSpeciesDesc(): LiveData<List<SightingEntity>>

    @Query("SELECT * FROM entities ORDER BY species ASC")
    fun loadAllSightingsSpeciesAsc(): LiveData<List<SightingEntity>>

    @Query("SELECT * FROM entities ORDER BY timestamp DESC")
    fun loadAllSightingsTimestampDesc(): LiveData<List<SightingEntity>>

    @Query("SELECT * FROM entities ORDER BY timestamp ASC")
    fun loadAllSightingsTimestampAsc(): LiveData<List<SightingEntity>>

    @Insert
    fun insertSighting(sighting: SightingEntity): Long
}