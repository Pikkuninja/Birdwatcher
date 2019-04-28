package fi.jara.birdwatcher.common.di.application

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.common.location.AndroidLocationSource
import fi.jara.birdwatcher.common.location.LocationSource
import javax.inject.Singleton

@Module
class LocationModule {
    @Singleton
    @Provides
    fun provideLocationSource(context: Context): LocationSource = AndroidLocationSource(context)
}