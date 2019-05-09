package fi.jara.birdwatcher.common.di.presentation

import dagger.Module
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Provides
import dagger.multibindings.IntoMap
import fi.jara.birdwatcher.common.di.ViewModelKey
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModel
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModel
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import javax.inject.Provider

@Module
class ViewModelModule {
    @Provides
    fun provideViewModelFactory(viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory =
        ViewModelFactory(viewModelMap)

    @Provides
    @IntoMap
    @ViewModelKey(ObservationsListViewModel::class)
    fun provideObservationsListViewModel(observeAllObservationsUseCase: ObserveAllObservationsUseCase): ViewModel =
        ObservationsListViewModel(observeAllObservationsUseCase)

    @Provides
    @IntoMap
    @ViewModelKey(AddObservationViewModel::class)
    fun provideAddObservationViewModel(
        insertNewObservationUseCase: InsertNewObservationUseCase,
        bitmapResolver: BitmapResolver
    ): ViewModel =
        AddObservationViewModel(insertNewObservationUseCase, bitmapResolver)
}