package fi.jara.birdwatcher.common.di

import dagger.Component
import fi.jara.birdwatcher.common.di.application.ApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationModule
import fi.jara.birdwatcher.common.di.application.LocationModule
import fi.jara.birdwatcher.common.di.application.RepositoryModule
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class, LocationModule::class, RepositoryModule::class])
interface TestApplicationComponent: ApplicationComponent {
    override fun newPresentationComponent(): TestPresentationComponent

}