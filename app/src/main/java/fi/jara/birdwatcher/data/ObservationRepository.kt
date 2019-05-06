package fi.jara.birdwatcher.data

import androidx.lifecycle.LiveData
import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationRarity
import fi.jara.birdwatcher.observations.ObservationSorting
import java.util.*

/*
    Using LiveData in repository layer adds dependency to Android libraries and
    that's kind of bad when RxJava could be used to achieve the same thing and has
    more options on operators and threading. RxJava isn't included in the project
    as the intention is to handle async with coroutines especially when Flows
    come out of early access, and its easier to fix this one thing than pull out RxJava
    that has been used to full extent
 */

interface ObservationRepository {
    // TODO: Use Kotlin Flows when out of early access
    fun allObservations(sorting: ObservationSorting): LiveData<RepositoryLoadingStatus<List<Observation>>>

    fun singleObservation(id: Long): LiveData<RepositoryLoadingStatus<Observation>>

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