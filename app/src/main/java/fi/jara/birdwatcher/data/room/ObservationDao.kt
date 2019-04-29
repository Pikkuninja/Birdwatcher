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
interface ObservationDao {
    @Query("SELECT * FROM entities ORDER BY species DESC")
    fun loadAllObservationsSpeciesDesc(): LiveData<List<ObservationEntity>>

    @Query("SELECT * FROM entities ORDER BY species ASC")
    fun loadAllObservationsSpeciesAsc(): LiveData<List<ObservationEntity>>

    @Query("SELECT * FROM entities ORDER BY timestamp DESC")
    fun loadAllObservationsTimestampDesc(): LiveData<List<ObservationEntity>>

    @Query("SELECT * FROM entities ORDER BY timestamp ASC")
    fun loadAllObservationsTimestampAsc(): LiveData<List<ObservationEntity>>

    @Insert
    fun insertObservation(observation: ObservationEntity): Long
}