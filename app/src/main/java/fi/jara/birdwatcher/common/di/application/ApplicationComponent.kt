package fi.jara.birdwatcher.common.di.application

import dagger.Component
import fi.jara.birdwatcher.common.di.presentation.PresentationComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun newPresentationComponent(): PresentationComponent
}