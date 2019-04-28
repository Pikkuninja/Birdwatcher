package fi.jara.birdwatcher.common.di.application

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.location.LocationSource
import fi.jara.birdwatcher.data.SightingRepository
import fi.jara.birdwatcher.data.room.RoomSightingRepository
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCase
import fi.jara.birdwatcher.sightings.ObserveAllSightingsUseCase

@Module
class ApplicationModule(private val application: Application) {
    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideObserveAllSightingsUseCase(sightingRepository: SightingRepository): ObserveAllSightingsUseCase =
        ObserveAllSightingsUseCase(sightingRepository)


    @Provides
    fun provideInsertNewSightingsUseCase(
        sightingRepository: SightingRepository,
        locationSource: LocationSource
    ): InsertNewSightingUseCase =
        InsertNewSightingUseCase(sightingRepository, locationSource)
}