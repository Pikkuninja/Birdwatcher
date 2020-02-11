package fi.jara.birdwatcher.screens.observationdetails

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import javax.inject.Inject

class ObservationDetailsViewModelFactoryCreator(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase
): (ObservationDetailsFragment) -> ViewModelProvider.Factory {
    override fun invoke(fragment: ObservationDetailsFragment): ObservationDetailsViewModelFactory {
        val args = ObservationDetailsFragmentArgs.fromBundle(fragment.requireArguments())
        return ObservationDetailsViewModelFactory(observeSingleObservationsUseCase, args.observationId, fragment, null)
    }
}

class ObservationDetailsViewModelFactory(
    private val observeSingleObservationsUseCase: ObserveSingleObservationsUseCase,
    private val observationId: Long,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArguments: Bundle?
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return ObservationDetailsViewModel(observeSingleObservationsUseCase, observationId) as T
    }
}