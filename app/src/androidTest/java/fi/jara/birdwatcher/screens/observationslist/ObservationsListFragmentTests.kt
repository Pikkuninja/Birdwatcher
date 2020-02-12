package fi.jara.birdwatcher.screens.observationslist

import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.TestApplicationComponentOwner
import fi.jara.birdwatcher.TestBirdwatcherApplication
import fi.jara.birdwatcher.common.di.DaggerTestApplicationComponent
import fi.jara.birdwatcher.common.di.TestApplicationComponent
import fi.jara.birdwatcher.common.di.application.ApplicationModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ObservationsListFragmentTests {

    private lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setup() {
        val app =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestBirdwatcherApplication
        app.testApplicationComponent = DaggerTestApplicationComponent.builder()
            .applicationModule(ApplicationModule(app))
            .build()

        val presentationComponent = app.testApplicationComponent.newPresentationComponent()
        fragmentFactory = presentationComponent.fragmentFactory()
    }

    @Test
    fun testDisplaysFab() {
        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        onView(withId(R.id.add_observation_button)).check(matches(isDisplayed()))
    }
}