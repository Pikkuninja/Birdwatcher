package fi.jara.birdwatcher.common.di.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import fi.jara.birdwatcher.common.di.ViewModelKey
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModel
import fi.jara.birdwatcher.screens.common.NoArgumentsViewModelFactory
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragmentArgs
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModelFactory
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModel
import javax.inject.Provider

@Module
class ViewModelFactoryModule {
    @Provides
    fun provideDefaultViewModelFactory(viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory =
        NoArgumentsViewModelFactory(viewModelMap)

    @Provides
    @IntoMap
    @ViewModelKey(ObservationsListViewModel::class)
    fun provideObservationsListViewModel(observeAllObservationsUseCase: ObserveAllObservationsUseCase): ViewModel =
        ObservationsListViewModel(observeAllObservationsUseCase)

    @Provides
    fun provideObservationDetailsViewModelCreator(observeUseCase: ObserveSingleObservationsUseCase): (@JvmWildcard ObservationDetailsFragment) -> @JvmWildcard ViewModelProvider.Factory {
        return { frag ->
            val id = frag.arguments?.let {
                ObservationDetailsFragmentArgs.fromBundle(it).observationId
            } ?: throw IllegalArgumentException()

            ObservationDetailsViewModelFactory(observeUseCase, id)
        }
    }

    @Provides
    @IntoMap
    @ViewModelKey(AddObservationViewModel::class)
    fun provideAddObservationViewModel(
        insertNewObservationUseCase: InsertNewObservationUseCase,
        bitmapResolver: BitmapResolver
    ): ViewModel =
        AddObservationViewModel(insertNewObservationUseCase, bitmapResolver)
}