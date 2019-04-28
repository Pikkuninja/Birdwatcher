package fi.jara.birdwatcher.common.di.application

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.AndroidBitmapResolver
import fi.jara.birdwatcher.common.filesystem.AndroidImageSaver
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.common.location.LocationSource
import fi.jara.birdwatcher.data.SightingRepository
import fi.jara.birdwatcher.sightings.InsertNewSightingUseCase
import fi.jara.birdwatcher.sightings.ObserveAllSightingsUseCase
import javax.inject.Singleton

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
        locationSource: LocationSource,
        imageStorage: ImageStorage
    ): InsertNewSightingUseCase =
        InsertNewSightingUseCase(sightingRepository, locationSource, imageStorage)

    @Provides
    @Singleton
    fun provideUriToBitmapResolver(context: Context): BitmapResolver = AndroidBitmapResolver(context)

    @Provides
    @Singleton
    fun provideImageSaver(context: Context): ImageStorage = AndroidImageSaver(context)
}