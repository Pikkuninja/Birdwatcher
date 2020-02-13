package fi.jara.birdwatcher.screens.observationslist

import android.view.MenuItem
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.screens.TestFragmentFactory
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ObservationsListFragmentTests {

    private val fragmentFactory = TestFragmentFactory(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun clickingAddGoesToAddObservationFragment() {
        val navController = TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
        navController.setGraph(R.navigation.birdwatcher_nav_graph)

        val observationsListScenario = launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        observationsListScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.add_observation_button)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.addObservationFragment)
    }

    @Test
    fun clickingAboutAppGoesToAboutFragment() {
        val navController = TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
        navController.setGraph(R.navigation.birdwatcher_nav_graph)

        val observationsListScenario = launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        observationsListScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)

            val menuItem = mockk<MenuItem>()
            every { menuItem.itemId } returns R.id.show_about_app
            fragment.onOptionsItemSelected(menuItem)
        }

        assertEquals(navController.currentDestination?.id, R.id.aboutFragment)
    }
}