package fi.jara.birdwatcher.screens.observationdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase

class ObservationDetailsViewModelFactory(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
    private val observationId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObservationDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObservationDetailsViewModel(observeSingleObservationsUseCase, observationId) as T
        } else {
            throw IllegalArgumentException()
        }
    }

}