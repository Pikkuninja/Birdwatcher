package fi.jara.birdwatcher.common.di.presentation

import dagger.Module
import androidx.lifecycle.ViewModel
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.screens.addsighting.AddSightingViewModel
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import fi.jara.birdwatcher.screens.sightingslist.SightingsListViewModel
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCase
import fi.jara.birdwatcher.sightings.ObserveAllSightingsUseCase
import javax.inject.Provider


@Module
class ViewModelModule {
    // TODO: Switch to multibinding the providers into a map instead of doing this by hand,
    // didn't work when tested it earlier
    @Suppress("UNCHECKED_CAST")
    @Provides
    fun provideViewModelFactory(
        sightingsListViewModelProvider: Provider<SightingsListViewModel>,
        addSightingViewModelProvider: Provider<AddSightingViewModel>
    ): ViewModelFactory =
        ViewModelFactory(
            mapOf(
                SightingsListViewModel::class.java to sightingsListViewModelProvider as Provider<ViewModel>,
                AddSightingViewModel::class.java to addSightingViewModelProvider as Provider<ViewModel>
            )
        )

    @Provides
    fun provideSightingsListViewModel(observeAllSightingsUseCase: ObserveAllSightingsUseCase): SightingsListViewModel =
        SightingsListViewModel(observeAllSightingsUseCase)

    @Provides
    fun provideAddSightingViewModel(
        insertNewSightingUseCase: InsertNewSightingUseCase,
        bitmapResolver: BitmapResolver
    ): AddSightingViewModel =
        AddSightingViewModel(insertNewSightingUseCase, bitmapResolver)

}