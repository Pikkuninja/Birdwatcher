package fi.jara.birdwatcher.screens.observationslist

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase

class ObservationsListViewModelFactoryCreator(
    private val observeAllObservationsUseCase: ObserveAllObservationsUseCase
): (ObservationsListFragment) -> ObservationsListViewModelFactory {
    override fun invoke(fragment: ObservationsListFragment): ObservationsListViewModelFactory {
        return ObservationsListViewModelFactory(fragment, observeAllObservationsUseCase)
    }
}

class ObservationsListViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val observeAllObservationsUseCase: ObserveAllObservationsUseCase
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return ObservationsListViewModel(observeAllObservationsUseCase) as T
    }
}