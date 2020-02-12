package fi.jara.birdwatcher.screens.observationdetails

import androidx.lifecycle.*
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class ObservationDetailsViewModel(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
    private val observationId: Long,
    uiDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    val observation: LiveData<Observation>

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading


    private val _observationLoadErrors = MutableLiveData<String?>().apply { value = null }
    val observationLoadErrors: LiveData<String?>
        get() = _observationLoadErrors

    init {
        observation = liveData(uiDispatcher) {
            observeSingleObservationsUseCase.execute(observationId)
                .collect { resultOrError ->
                    when (val result = resultOrError.result) {
                        is ValueFound -> {
                            emit(result.value)
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
                        null -> {
                            _observationLoadErrors.postValue(resultOrError.errorMessage)
                            _showLoading.postValue(false)
                        }
                    }
                }
        }
    }
}