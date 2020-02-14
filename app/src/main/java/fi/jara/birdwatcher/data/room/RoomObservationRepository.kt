package fi.jara.birdwatcher.data.room

import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class RoomObservationRepository(private val database: ObservationDatabase) : ObservationRepository {
    private val dao = database.observationDao()

    override fun allObservations(sorting: ObservationSorting): Flow<RepositoryLoadingStatus<List<Observation>>> {
        val daoFlow = when (sorting) {
            ObservationSorting.TimeDescending -> dao.observeAllObservationsTimestampDesc()
            ObservationSorting.TimeAscending -> dao.observeAllObservationsTimestampAsc()
            ObservationSorting.NameDescending -> dao.observeAllObservationsSpeciesDesc()
            ObservationSorting.NameAscending -> dao.observeAllObservationsSpeciesAsc()
        }

        @Suppress("EXPERIMENTAL_API_USAGE")
        return daoFlow
            .map { entities -> entities.map { it.toObservationModel() } }
            .map {
                if (it.isNotEmpty()) {
                    StatusSuccess(it)
                } else {
                    StatusEmpty<List<Observation>>()
                }
            }
            .onStart { emit(StatusLoading()) }
    }

    override fun singleObservation(id: Long): Flow<RepositoryLoadingStatus<Observation>> {
        @Suppress("EXPERIMENTAL_API_USAGE")
        return dao.observeObservation(id)
            .map { it?.toObservationModel() }
            .map {
                if (it != null) {
                    StatusSuccess(it)
                } else {
                    StatusEmpty<Observation>()
                }
            }
            .onStart { emit(StatusLoading()) }
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
