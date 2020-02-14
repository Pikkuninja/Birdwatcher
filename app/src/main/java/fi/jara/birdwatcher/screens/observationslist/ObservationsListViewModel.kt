package fi.jara.birdwatcher.screens.observationslist

import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.observations.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest

class ObservationsListViewModel(
    private val observeAllObservationsUseCase: ObserveAllObservationsUseCase,
    uiDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val sorting =
        MutableLiveData<ObservationSorting>().apply { value = ObservationSorting.TimeDescending }
    var currentSorting: ObservationSorting
        get() = sorting.value!! // Sorting is initialized with a value, so there's one always present
        set(value) {
            sorting.value = value
        }

    private val _showLoading = MutableLiveData<Boolean>().apply { value = false }
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _showNoObservations = MutableLiveData<Boolean>().apply { value = false }
    val showNoObservations: LiveData<Boolean>
        get() = _showNoObservations

    private val _observationLoadErrors = LiveEvent<String>()
    val observationLoadErrors: LiveData<String>
        get() = _observationLoadErrors

    val observations: LiveData<List<Observation>>

    init {
        observations = liveData(uiDispatcher) {
            @Suppress("EXPERIMENTAL_API_USAGE")
            sorting
                .asFlow()
                .flatMapLatest { observeAllObservationsUseCase.execute(it) }
                .collect { resultOrError ->
                    resultOrError.result?.let {
                        _showLoading.value = it is LoadingInitial
                        _showNoObservations.value = it is NotFound

                        if (it is ValueFound) {
                            emit(it.value)
                        } else if (it is NotFound) {
                            emit(emptyList())
                        }
                    }

                    resultOrError.errorMessage?.let {
                        _showLoading.value = false
                        _observationLoadErrors.value = it
                    }
                }
        }
    }
}
