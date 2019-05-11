package fi.jara.birdwatcher.screens.observationdetails

import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase

class ObservationDetailsViewModel(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
    private val observationId: Long
) : ViewModel() {

    val observation: LiveData<Observation>

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading


    private val _observationLoadErrors = MutableLiveData<String?>().apply { value = null }
    val observationLoadErrors: LiveData<String?>
        get() = _observationLoadErrors

    init {
        observation = MediatorLiveData<Observation>().apply {
            addSource(observeSingleObservationsUseCase.execute(observationId)) { resultOrError ->
                if (resultOrError.result != null) {
                    when (val result = resultOrError.result) {
                        is ValueFound -> {
                            postValue(result.value)
                            _observationLoadErrors.postValue(null)
                            _showLoading.postValue(false)
                        }
                        is LoadingInitial -> {
                            _observationLoadErrors.postValue(null)
                            _showLoading.postValue(true)
                        }
                        is NotFound -> {
                            _observationLoadErrors.postValue("Observation not found")
                            _showLoading.postValue(false)
                        }
                    }
                } else {
                    _observationLoadErrors.postValue(resultOrError.errorMessage)
                    _showLoading.postValue(false)
                }
            }
        }
    }
}