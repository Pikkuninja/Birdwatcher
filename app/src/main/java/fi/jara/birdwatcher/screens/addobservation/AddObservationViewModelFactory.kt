package fi.jara.birdwatcher.screens.addobservation

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase


class AddObservationViewModelFactoryCreator(
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver
): (AddObservationFragment) -> ViewModelProvider.Factory {
    override fun invoke(fragment: AddObservationFragment): ViewModelProvider.Factory {
        return AddObservationViewModelFactory(fragment, insertNewObservationUseCase, bitmapResolver)
    }
}

class AddObservationViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return AddObservationViewModel(handle, insertNewObservationUseCase, bitmapResolver) as T
    }
}