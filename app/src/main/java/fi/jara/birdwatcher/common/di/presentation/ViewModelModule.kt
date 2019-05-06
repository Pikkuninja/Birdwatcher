package fi.jara.birdwatcher.common.di.presentation

import dagger.Module
import androidx.lifecycle.ViewModel
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModel
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModel
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModelFactory
import javax.inject.Provider

@Module
class ViewModelModule {
    // TODO: Switch to multibinding the providers into a map instead of doing this by hand,
    // didn't work when tested it earlier
    @Suppress("UNCHECKED_CAST")
    @Provides
    fun provideViewModelFactory(
        observationsListViewModelProvider: Provider<ObservationsListViewModel>,
        addObservationViewModelProvider: Provider<AddObservationViewModel>
    ): ViewModelFactory =
        ViewModelFactory(
            mapOf(
                ObservationsListViewModel::class.java to observationsListViewModelProvider as Provider<ViewModel>,
                AddObservationViewModel::class.java to addObservationViewModelProvider as Provider<ViewModel>
            )
        )

    @Provides
    fun provideObservationsListViewModel(observeAllObservationsUseCase: ObserveAllObservationsUseCase): ObservationsListViewModel =
        ObservationsListViewModel(observeAllObservationsUseCase)

    @Provides
    fun provideAddObservationViewModel(
        insertNewObservationUseCase: InsertNewObservationUseCase,
        bitmapResolver: BitmapResolver
    ): AddObservationViewModel =
        AddObservationViewModel(insertNewObservationUseCase, bitmapResolver)
}