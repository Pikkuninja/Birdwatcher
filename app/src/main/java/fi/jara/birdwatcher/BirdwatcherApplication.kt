package fi.jara.birdwatcher

import android.app.Application
import fi.jara.birdwatcher.common.di.application.ApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationModule
import fi.jara.birdwatcher.common.di.application.DaggerApplicationComponent
import fi.jara.birdwatcher.common.testing.OpenForTesting

@OpenForTesting
class BirdwatcherApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    fun createApplicationComponent() = DaggerApplicationComponent.builder()
        .applicationModule(ApplicationModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()
        applicationComponent = createApplicationComponent()
    }
}