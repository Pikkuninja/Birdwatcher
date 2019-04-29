package fi.jara.birdwatcher.screens.observationslist

import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.observations.*

class ObservationsListViewModel(private val observeAllObservationsUseCase: ObserveAllObservationsUseCase) : ViewModel() {

    private val sorting = MutableLiveData<ObservationSorting>().apply { value = ObservationSorting.TimeDescending }
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
        val observationLoadingStatuses = Transformations.switchMap(sorting) {
            observeAllObservationsUseCase.execute(it)
        }

        val observationsMediator = MediatorLiveData<List<Observation>>()
        observationsMediator.addSource(observationLoadingStatuses) { resultOrError ->
            resultOrError.result?.let {
                _showLoading.value = it is Loading
                _showNoObservations.value = it is NoObservations
                if (it is ObservationsFound) {
                    observationsMediator.value = it.observations
                }
            }

            resultOrError.errorMessage?.let { _observationLoadErrors.value = it }
        }
        observations = observationsMediator
    }
}
