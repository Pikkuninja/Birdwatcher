package fi.jara.birdwatcher.mocks

import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

// Google doesn't provide a way to test Room databases on non-device tests
// https://developer.android.com/training/data-storage/room/testing-db

class MockObservationRepository: ObservationRepository {
    val allObservationsChannel = Channel<RepositoryLoadingStatus<List<Observation>>>(Channel.CONFLATED)
    val singleObservationChannel = Channel<RepositoryLoadingStatus<Observation>>(Channel.CONFLATED)
    var addObservationReturnValue: RepositoryLoadingStatus<Observation>? = null

    override fun allObservations(sorting: ObservationSorting): Flow<RepositoryLoadingStatus<List<Observation>>> = allObservationsChannel.consumeAsFlow()

    override fun singleObservation(id: Long): Flow<RepositoryLoadingStatus<Observation>> = singleObservationChannel.consumeAsFlow()

    override suspend fun addObservation(observationData: NewObservationData) = addObservationReturnValue ?: throw IllegalStateException("Set return value for addObservation before calling it")
}