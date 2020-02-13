package fi.jara.birdwatcher.screens.addobservation

import androidx.lifecycle.ViewModelProvider
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.screens.common.LambdaSavedStateViewModelFactory

class AddObservationViewModelFactoryCreator(
    private val insertNewObservationUseCase: InsertNewObservationUseCase,
    private val bitmapResolver: BitmapResolver
) : (AddObservationFragment) -> ViewModelProvider.Factory {
    override fun invoke(fragment: AddObservationFragment): ViewModelProvider.Factory {
        return LambdaSavedStateViewModelFactory(fragment, fragment.arguments) { handle ->
            AddObservationViewModel(handle, insertNewObservationUseCase, bitmapResolver)
        }
    }
}
