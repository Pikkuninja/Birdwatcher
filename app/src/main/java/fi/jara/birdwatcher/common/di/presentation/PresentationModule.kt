package fi.jara.birdwatcher.common.di.presentation

import androidx.recyclerview.widget.ListAdapter
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.screens.sightingslist.SightingsAdapter
import fi.jara.birdwatcher.sightings.Sighting

@Module
class PresentationModule {
    @Provides
    fun provideSightingsAdapter(imageStorage: ImageStorage): ListAdapter<Sighting, *> {
        return SightingsAdapter(imageStorage)
    }
}
