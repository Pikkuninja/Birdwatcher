package fi.jara.birdwatcher

import android.app.Application
import fi.jara.birdwatcher.common.di.application.ApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationComponentOwner
import fi.jara.birdwatcher.common.di.application.ApplicationModule
import fi.jara.birdwatcher.common.di.application.DaggerApplicationComponent

class BirdwatcherApplication : Application(), ApplicationComponentOwner {
    override lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}