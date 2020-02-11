package fi.jara.birdwatcher.common.di.presentation

import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModelFactoryCreator
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModelFactoryCreator
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModelFactoryCreator

@Module
class ViewModelFactoryModule {
    @Provides
    fun provideObservationsListViewModelFactoryCreator(observeAllObservationsUseCase: ObserveAllObservationsUseCase): ObservationsListViewModelFactoryCreator =
        ObservationsListViewModelFactoryCreator(observeAllObservationsUseCase)

    @Provides
    fun provideObservationDetailsViewModelFactoryCreator(observeUseCase: ObserveSingleObservationsUseCase): ObservationDetailsViewModelFactoryCreator =
        ObservationDetailsViewModelFactoryCreator(observeUseCase)

    @Provides
    fun provideAddObservationViewModelFactoryCreator(
        insertNewObservationUseCase: InsertNewObservationUseCase,
        bitmapResolver: BitmapResolver
    ): AddObservationViewModelFactoryCreator =
        AddObservationViewModelFactoryCreator(insertNewObservationUseCase, bitmapResolver)
}