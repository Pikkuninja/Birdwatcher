package fi.jara.birdwatcher.data.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.data.NewSightingData
import fi.jara.birdwatcher.data.RepositoryLoadingStatus
import fi.jara.birdwatcher.data.SightingRepository
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingSorting

class RoomSightingRepository(private val database: SightingDatabase) : SightingRepository {
    private val dao = database.sightingDao()

    override fun allSightings(sorting: SightingSorting): LiveData<List<Sighting>> {
        val liveData= when (sorting) {
            SightingSorting.TimeDescending -> dao.loadAllSightingsTimestampDesc()
            SightingSorting.TimeAscending -> dao.loadAllSightingsTimestampAsc()
            SightingSorting.NameDescending -> dao.loadAllSightingsSpeciesDesc()
            SightingSorting.NameAscending -> dao.loadAllSightingsSpeciesAsc()
        }

        return Transformations.map(liveData) { sightings ->
            sightings.map {
                Sighting(
                    it.id,
                    it.species,
                    it.timestamp,
                    it.location,
                    it.rarity,
                    it.imageName,
                    it.description
                )
            }
        }
    }

    override suspend fun addSighting(sightingData: NewSightingData): RepositoryLoadingStatus<Sighting> {
        val entity = SightingEntity(
            0,
            sightingData.species,
            sightingData.timestamp,
            sightingData.location,
            sightingData.rarity,
            sightingData.imageName,
            sightingData.description
        )

        val id = dao.insertSighting(entity)

        val sighting = Sighting(
            id,
            sightingData.species,
            sightingData.timestamp,
            sightingData.location,
            sightingData.rarity,
            sightingData.imageName,
            sightingData.description
        )

        return StatusSuccess(sighting)
    }
}
