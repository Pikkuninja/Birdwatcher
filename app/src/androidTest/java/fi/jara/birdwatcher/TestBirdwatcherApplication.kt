package fi.jara.birdwatcher

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import fi.jara.birdwatcher.common.di.DaggerTestApplicationComponent
import fi.jara.birdwatcher.common.di.TestApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationModule

class TestBirdwatcherApplication : BirdwatcherApplication() {
    override fun createApplicationComponent(): TestApplicationComponent {
        return DaggerTestApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}

class TestBirdwatcherApplicationTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestBirdwatcherApplication::class.java.name, context)
    }
}