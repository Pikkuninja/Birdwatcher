package fi.jara.birdwatcher.common.di

import dagger.Subcomponent
import fi.jara.birdwatcher.common.di.presentation.FragmentFactoryModule
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent
import fi.jara.birdwatcher.common.di.presentation.PresentationModule
import fi.jara.birdwatcher.common.di.presentation.ViewModelFactoryModule

@Subcomponent(modules = [PresentationModule::class, ViewModelFactoryModule::class, FragmentFactoryModule::class])
interface TestPresentationComponent: PresentationComponent {

}