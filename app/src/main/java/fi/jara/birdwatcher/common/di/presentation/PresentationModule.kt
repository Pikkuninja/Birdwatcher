package fi.jara.birdwatcher.common.di.presentation

import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.screens.observationslist.ObservationsAdapter
import java.text.DateFormat

@Module
class PresentationModule {
    @Provides
    fun provideObservationsAdapter(imageStorage: ImageStorage, dateFormat: DateFormat): ObservationsAdapter {
        return ObservationsAdapter(imageStorage, dateFormat)
    }
}
