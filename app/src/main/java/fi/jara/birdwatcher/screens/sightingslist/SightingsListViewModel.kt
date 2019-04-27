package fi.jara.birdwatcher.screens.sightingslist

import androidx.lifecycle.*
import fi.jara.birdwatcher.LiveEvent
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingSorting

class SightingsListViewModel : ViewModel() {

    private val sorting = MutableLiveData<SightingSorting>().apply { value = SightingSorting.TimeAscending }
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
            when (it) {
                SightingSorting.TimeAscending -> {
                    MutableLiveData<RepositoryLoadingStatus<List<Sighting>>>().apply { value = StatusLoading() }
                }
                SightingSorting.TimeDescending -> {
                    MutableLiveData<RepositoryLoadingStatus<List<Sighting>>>().apply { value = StatusLoading() }
                }
                SightingSorting.NameAscending -> {
                    MutableLiveData<RepositoryLoadingStatus<List<Sighting>>>().apply { value = StatusLoading() }
                }
                SightingSorting.NameDescending -> {
                    MutableLiveData<RepositoryLoadingStatus<List<Sighting>>>().apply { value = StatusLoading() }
                }
                null -> {
                    MutableLiveData<RepositoryLoadingStatus<List<Sighting>>>().apply { value = StatusError("Null sorting") }
                }
            }
        }

        val sightingsMediator = MediatorLiveData<List<Sighting>>()
        sightingsMediator.addSource(sightingLoadingStatuses) {
            _showLoading.value = it is StatusLoading
            _showNoSightings.value = it is StatusEmpty
            if (it is StatusSuccess) {
                sightingsMediator.value = it.value
            } else if (it is StatusError) {
                _sightingLoadErrors.value = it.message
            }
        }
        sightings = sightingsMediator
    }
}
