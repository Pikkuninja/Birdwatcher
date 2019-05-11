package fi.jara.birdwatcher.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.*

// Google doesn't provide a way to test Room databases on non-device tests
// https://developer.android.com/training/data-storage/room/testing-db

class MockObservationRepository: ObservationRepository {
    val allObservationsLiveData = MutableLiveData<RepositoryLoadingStatus<List<Observation>>>()
    val singleObservationLiveData = MutableLiveData<RepositoryLoadingStatus<Observation>>()
    var addObservationReturnValue: RepositoryLoadingStatus<Observation>? = null

    override fun allObservations(sorting: ObservationSorting): LiveData<RepositoryLoadingStatus<List<Observation>>> = allObservationsLiveData

    override fun singleObservation(id: Long): LiveData<RepositoryLoadingStatus<Observation>> = singleObservationLiveData

    override suspend fun addObservation(observationData: NewObservationData) = addObservationReturnValue ?: throw IllegalStateException("Set return value for addObservation before calling it")
}