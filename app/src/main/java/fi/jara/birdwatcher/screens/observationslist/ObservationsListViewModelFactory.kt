package fi.jara.birdwatcher.screens.observationslist

import androidx.lifecycle.ViewModelProvider
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import fi.jara.birdwatcher.screens.common.LambdaSavedStateViewModelFactory

class ObservationsListViewModelFactoryCreator(
    private val observeAllObservationsUseCase: ObserveAllObservationsUseCase
) : (ObservationsListFragment) -> ViewModelProvider.Factory {
    override fun invoke(fragment: ObservationsListFragment): ViewModelProvider.Factory {
        return LambdaSavedStateViewModelFactory(
            fragment,
            fragment.arguments
        ) { ObservationsListViewModel(observeAllObservationsUseCase) }
    }
}
