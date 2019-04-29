package fi.jara.birdwatcher.data.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationSorting

class RoomObservationRepository(private val database: ObservationDatabase) : ObservationRepository {
    private val dao = database.observationDao()

    override fun allObservations(sorting: ObservationSorting): LiveData<RepositoryLoadingStatus<List<Observation>>> {
        val liveData = when (sorting) {
            ObservationSorting.TimeDescending -> dao.loadAllObservationsTimestampDesc()
            ObservationSorting.TimeAscending -> dao.loadAllObservationsTimestampAsc()
            ObservationSorting.NameDescending -> dao.loadAllObservationsSpeciesDesc()
            ObservationSorting.NameAscending -> dao.loadAllObservationsSpeciesAsc()
        }

        val observationsLists = Transformations.map(liveData) { entities ->
            entities.map { it.toObservationModel() }
        }

        return MediatorLiveData<RepositoryLoadingStatus<List<Observation>>>().apply {
            postValue(StatusLoading())
            addSource(observationsLists) { observations ->
                if (observations.isNotEmpty()) {
                    postValue(StatusSuccess(observations))
                } else {
                    postValue(StatusEmpty())
                }
            }
        }
    }

    override suspend fun addObservation(observationData: NewObservationData): RepositoryLoadingStatus<Observation> {
        val entity = ObservationEntity.fromNewObservationData(observationData)

        val id = dao.insertObservation(entity)

        val observation = Observation(
            id,
            observationData.species,
            observationData.timestamp,
            observationData.location,
            observationData.rarity,
            observationData.imageName,
            observationData.description
        )

        return StatusSuccess(observation)
    }
}
