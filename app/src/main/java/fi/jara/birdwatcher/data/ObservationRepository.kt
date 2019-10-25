package fi.jara.birdwatcher.data

import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationRarity
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ObservationRepository {
    fun allObservations(sorting: ObservationSorting): Flow<RepositoryLoadingStatus<List<Observation>>>

    fun singleObservation(id: Long): Flow<RepositoryLoadingStatus<Observation>>

    suspend fun addObservation(observationData: NewObservationData): RepositoryLoadingStatus<Observation>
}

data class NewObservationData(
    val species: String,
    val timestamp: Date,
    val location: Coordinate?,
    val rarity: ObservationRarity,
    val imageName: String?,
    val description: String?
)