package fi.jara.birdwatcher.screens.observationdetails

import androidx.lifecycle.ViewModelProvider
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import fi.jara.birdwatcher.screens.common.LambdaSavedStateViewModelFactory

class ObservationDetailsViewModelFactoryCreator(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase
) : (ObservationDetailsFragment) -> ViewModelProvider.Factory {
    override fun invoke(fragment: ObservationDetailsFragment): ViewModelProvider.Factory {
        val observationId =
            fragment.arguments?.let { ObservationDetailsFragmentArgs.fromBundle(it).observationId }
                ?: -1

        return LambdaSavedStateViewModelFactory(fragment, fragment.arguments) {
            ObservationDetailsViewModel(observeSingleObservationsUseCase, observationId)
        }
    }
}