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
import fi.jara.birdwatcher.data.ObservationRepository
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObserveAllObservationsUseCase
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.text.DateFormat
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {
    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideObserveAllObservationsUseCase(observationRepository: ObservationRepository): ObserveAllObservationsUseCase =
        ObserveAllObservationsUseCase(observationRepository)

    @Provides
    fun provideObserveSingleObservationsUseCase(observationRepository: ObservationRepository): ObserveSingleObservationsUseCase =
        ObserveSingleObservationsUseCase(observationRepository)

    @Provides
    fun provideInsertNewObservationsUseCase(
        observationRepository: ObservationRepository,
        locationSource: LocationSource,
        imageStorage: ImageStorage
    ): InsertNewObservationUseCase =
        InsertNewObservationUseCase(observationRepository, locationSource, imageStorage)

    @Provides
    @Singleton
    fun provideUriToBitmapResolver(context: Context): BitmapResolver = AndroidBitmapResolver(context)

    @Provides
    @Singleton
    fun provideImageSaver(context: Context): ImageStorage = AndroidImageSaver(context)

    @Provides
    @Singleton
    fun provideDateFormat(): DateFormat = DateFormat.getDateTimeInstance()

    @Provides
    @Singleton
    fun provideUiDispatcher(): CoroutineDispatcher = Dispatchers.Main
}