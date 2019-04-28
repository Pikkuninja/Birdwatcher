package fi.jara.birdwatcher.screens.sightingslist

import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.sightings.*

class SightingsListViewModel(private val observeAllSightingsUseCase: ObserveAllSightingsUseCase) : ViewModel() {

    private val sorting = MutableLiveData<SightingSorting>().apply { value = SightingSorting.TimeDescending }
    var currentSorting: SightingSorting
        get() = sorting.value!! // Sorting is initialized with a value, so there's one always present
        set(value) {
            sorting.value = value
        }

    private val _showLoading = MutableLiveData<Boolean>().apply { value = false }
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _showNoSightings = MutableLiveData<Boolean>().apply { value = false }
    val showNoSightings: LiveData<Boolean>
        get() = _showNoSightings

    private val _sightingLoadErrors = LiveEvent<String>()
    val sightingLoadErrors: LiveData<String>
        get() = _sightingLoadErrors

    val sightings: LiveData<List<Sighting>>

    init {
        val sightingLoadingStatuses = Transformations.switchMap(sorting) {
            observeAllSightingsUseCase.execute(it)
        }

        val sightingsMediator = MediatorLiveData<List<Sighting>>()
        sightingsMediator.addSource(sightingLoadingStatuses) { resultOrError ->
            resultOrError.result?.let {
                _showLoading.value = it is Loading
                _showNoSightings.value = it is NoSightings
                if (it is SightingsFound) {
                    sightingsMediator.value = it.sightings
                }
            }

            resultOrError.errorMessage?.let { _sightingLoadErrors.value = it }
        }
        sightings = sightingsMediator
    }
}
