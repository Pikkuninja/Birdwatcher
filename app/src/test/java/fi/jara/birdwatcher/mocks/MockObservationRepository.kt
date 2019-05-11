package fi.jara.birdwatcher.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.*

// Google doesn't provide a way to test Room databases on non-device tests
// https://developer.android.com/training/data-storage/room/testing-db
class MockObservationRepository : ObservationRepository {
    private val data = MutableLiveData<RepositoryLoadingStatus<List<Observation>>>().apply { value = StatusEmpty() }

    override fun allObservations(sorting: ObservationSorting): LiveData<RepositoryLoadingStatus<List<Observation>>> {
        return MediatorLiveData<RepositoryLoadingStatus<List<Observation>>>().apply {
            value = StatusLoading()
            GlobalScope.launch(Dispatchers.IO) {
                delay(8)
                addSource(data) { status ->
                    if (status is StatusSuccess) {
                        val sorted = when (sorting) {
                            ObservationSorting.NameAscending -> status.value.sortedBy { it.species }
                            ObservationSorting.NameDescending -> status.value.sortedByDescending { it.species }
                            ObservationSorting.TimeAscending -> status.value.sortedBy { it.timestamp }
                            ObservationSorting.TimeDescending -> status.value.sortedByDescending { it.timestamp }
                        }

                        value = StatusSuccess(sorted)
                    } else {
                        value = status
                    }
                }
            }
        }
    }

    override fun singleObservation(id: Long): LiveData<RepositoryLoadingStatus<Observation>> {
        return MediatorLiveData<RepositoryLoadingStatus<Observation>>().apply {
            postValue(StatusLoading())
            GlobalScope.launch(Dispatchers.IO) {
                delay(8)
                addSource(data) { status ->
                    when (status) {
                        is StatusSuccess -> {
                            val queried = status.value.find { it.id == id }
                            if (queried != null) {
                                value = StatusSuccess(queried)
                            } else {
                                value = StatusEmpty()
                            }
                        }
                        is StatusError -> {
                            value = StatusError(status.message)
                        }
                        is StatusEmpty -> {
                            value = StatusEmpty()
                        }
                        is StatusLoading -> {
                            // Loading is only for initial, and we manually post it already. This happening would be
                            // failure in mocking logic
                        }
                    }
                }
            }
        }
    }

    override suspend fun addObservation(observationData: NewObservationData): RepositoryLoadingStatus<Observation> {
        val curData = data.value
        val prevs = if (curData is StatusSuccess) {
            curData.value
        } else {
            emptyList()
        }

        val newId = (prevs.lastOrNull()?.id ?: 0) + 1
        val observationModel = Observation(
            newId,
            observationData.species,
            observationData.timestamp,
            observationData.location,
            observationData.rarity,
            observationData.imageName,
            observationData.description
        )

        data.value = StatusSuccess(prevs + observationModel)
        return StatusSuccess(observationModel)
    }
}

class AlwaysFailingMockObservationRepository : ObservationRepository {
   override fun allObservations(sorting: ObservationSorting): LiveData<RepositoryLoadingStatus<List<Observation>>> {
        return MutableLiveData<RepositoryLoadingStatus<List<Observation>>>().apply {
            value = StatusLoading()

            GlobalScope.launch(Dispatchers.IO) {
                delay(8)
                value = StatusError(MOCK_OBSERVATION_REPOSITORY_ERROR_MESSAGE)
            }
        }
    }

    override fun singleObservation(id: Long): LiveData<RepositoryLoadingStatus<Observation>> {
        return MutableLiveData<RepositoryLoadingStatus<Observation>>().apply {
            value = StatusLoading()

            GlobalScope.launch(Dispatchers.IO) {
                delay(8)
                value = StatusError(MOCK_OBSERVATION_REPOSITORY_ERROR_MESSAGE)
            }
        }
    }

    override suspend fun addObservation(observationData: NewObservationData): RepositoryLoadingStatus<Observation> {
        return StatusError(MOCK_OBSERVATION_REPOSITORY_ERROR_MESSAGE)
    }

    companion object {
        const val MOCK_OBSERVATION_REPOSITORY_ERROR_MESSAGE =
            "[AlwaysFailingMockObservationRepository]Error storing observation"
    }
}