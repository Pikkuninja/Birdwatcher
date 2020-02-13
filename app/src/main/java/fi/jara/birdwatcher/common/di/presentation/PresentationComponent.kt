package fi.jara.birdwatcher.common.di.presentation

import dagger.Subcomponent
import fi.jara.birdwatcher.screens.MainActivity

@Subcomponent(modules = [PresentationModule::class, ViewModelFactoryModule::class, FragmentFactoryModule::class])
interface PresentationComponent {
    fun inject(mainActivity: MainActivity)
}