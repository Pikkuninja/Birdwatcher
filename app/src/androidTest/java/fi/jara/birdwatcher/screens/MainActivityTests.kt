package fi.jara.birdwatcher.screens

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.platform.app.InstrumentationRegistry
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.TestBirdwatcherApplication
import fi.jara.birdwatcher.common.di.TestApplicationComponent
import fi.jara.birdwatcher.injectableActivityScenario
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class MainActivityTests {
    @Before
    fun setUp() {
        val app =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestBirdwatcherApplication
        app.testApplicationComponent = TestApplicationComponent()
    }

    // More for checking that the pattern used for DI works and isn't too annoying to use
    @Test
    fun startsOnListObservationsScreen() {
        val scenario = injectableActivityScenario<MainActivity> {
            injectActivity {
                fragmentFactory =
                    TestFragmentFactory(
                        applicationContext
                    )
            }
        }.launch()

        var navController: NavController? = null
        scenario.onActivity {
            navController = Navigation.findNavController(it, R.id.nav_host_fragment)
        }

        assertEquals(navController?.currentDestination?.id, R.id.observationsListFragment)

        scenario.close()
    }
}