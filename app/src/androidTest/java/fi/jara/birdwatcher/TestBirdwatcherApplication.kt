package fi.jara.birdwatcher

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import fi.jara.birdwatcher.common.di.TestApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationComponentOwner

class TestBirdwatcherApplication : Application(), TestApplicationComponentOwner {
    override lateinit var testApplicationComponent: TestApplicationComponent
}

interface TestApplicationComponentOwner: ApplicationComponentOwner {
    var testApplicationComponent: TestApplicationComponent
    override val applicationComponent: ApplicationComponent
        get() = testApplicationComponent
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