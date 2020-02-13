package fi.jara.birdwatcher.screens.observationslist

import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.common.filesystem.AndroidImageSaver
import fi.jara.birdwatcher.screens.TestFragmentFactory
import fi.jara.birdwatcher.screens.mockObservationsListViewModel
import fi.jara.birdwatcher.screens.mockObservationsListViewModelFactoryCreator
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ObservationsListFragmentTests {

    private lateinit var viewModel: ObservationsListViewModel
    private lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val viewModel = mockObservationsListViewModel()
        val vmfc = mockObservationsListViewModelFactoryCreator { viewModel }

        fragmentFactory =
            TestFragmentFactory(InstrumentationRegistry.getInstrumentation().targetContext,
                mapOf(ObservationsListFragment::class.java to {
                    ObservationsListFragment(
                        vmfc,
                        ObservationsAdapter(
                            AndroidImageSaver(context),
                            DateFormat.getDateInstance()
                        )
                    )
                })
            )
    }

    @Test
    fun clickingAboutAppGoesToAboutFragment() {
        val navController =
            TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
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

    @Test
    fun testClickingAddGoesToAddObservationFragment() {
        val navController =
            TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
        navController.setGraph(R.navigation.birdwatcher_nav_graph)

        val observationsListScenario = launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        observationsListScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // If run with other tests, the click listener often doesn't get called without this sleep
        // All sorts of Espresso's own synchronization things don't seem to help either
        Thread.sleep(150)

        onView(withId(R.id.add_observation_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.addObservationFragment)
    }
}