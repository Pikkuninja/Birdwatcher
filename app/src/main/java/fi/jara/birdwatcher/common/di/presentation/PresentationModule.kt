package fi.jara.birdwatcher.common.di.presentation

import androidx.recyclerview.widget.ListAdapter
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.screens.observationslist.ObservationsAdapter
import fi.jara.birdwatcher.observations.Observation

@Module
class PresentationModule {
    @Provides
    fun provideObservationsAdapter(imageStorage: ImageStorage): ObservationsAdapter {
        return ObservationsAdapter(imageStorage)
    }
}
