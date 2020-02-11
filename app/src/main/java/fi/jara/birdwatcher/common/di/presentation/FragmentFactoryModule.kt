package fi.jara.birdwatcher.common.di.presentation

import androidx.fragment.app.FragmentFactory
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.screens.common.BirdwatcherFragmentFactory


@Module
class FragmentFactoryModule {
    @Provides
    fun provideFragmentFactory(factory: BirdwatcherFragmentFactory): FragmentFactory = factory
}