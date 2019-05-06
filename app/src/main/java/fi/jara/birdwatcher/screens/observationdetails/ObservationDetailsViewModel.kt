package fi.jara.birdwatcher.screens.observationdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase

class ObservationDetailsViewModel(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
    private val observationId: Long
) : ViewModel() {

}

class ObservationDetailsViewModelFactory(
    private val useCase: ObserveSingleObservationsUseCase,
    private val observationId: Long
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ObservationDetailsViewModel(useCase, observationId) as T?
            ?: throw IllegalArgumentException("ObservationDetailsViewModelFactory can't create ViewModels of class ${modelClass.canonicalName}")
    }
}