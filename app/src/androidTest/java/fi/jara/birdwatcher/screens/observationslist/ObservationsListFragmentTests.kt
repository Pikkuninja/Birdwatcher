package fi.jara.birdwatcher.screens.observationslist

import android.view.MenuItem
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.liveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.common.LiveEvent
import fi.jara.birdwatcher.common.filesystem.AndroidImageSaver
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationRarity
import fi.jara.birdwatcher.screens.TestFragmentFactory
import fi.jara.birdwatcher.screens.mockObservationsListViewModel
import fi.jara.birdwatcher.screens.mockObservationsListViewModelFactoryCreator
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ObservationsListFragmentTests {

    private lateinit var viewModel: ObservationsListViewModel
    private lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        viewModel = mockObservationsListViewModel()
        val vmfc = mockObservationsListViewModelFactoryCreator { viewModel }

        fragmentFactory =
            TestFragmentFactory(
                InstrumentationRegistry.getInstrumentation().targetContext,
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
    fun clickingAddGoesToAddObservationFragment() {
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

        onView(withId(R.id.add_observation_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.addObservationFragment)
    }

    @Test
    @Ignore("Doesn't work properly on different screen sizes. Doesn't make much sense overall, just a sample of scrolling durin a UI test")
    fun scrollingRevealsHiddenItem() {
        every { viewModel.observations } answers {
            liveData { emit(testData) }
        }

        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        onView(withText("species9")).check(doesNotExist())
        onView(withId(R.id.observations_recyclerview)).perform(ViewActions.swipeUp())
        onView(withText("species9")).check(matches(isDisplayed()))
    }

    @Test
    fun hidesProgressIndicatorWhenShowLoadingisInactive() {
        every { viewModel.showLoading } answers {
            liveData { emit(false) }
        }

        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        onView(withId(R.id.loading_indicator)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun showsProgressIndicatorWhenShowLoadingIsActive() {
        every { viewModel.showLoading } answers {
            liveData { emit(true) }
        }

        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("The view is never found. Tried several ways to setup the livedata and waiting in the test code")
    fun showsErrorTextWhenErrorMessageIsEmited() {
        val testErrorMessage = "test-error"
        val errorLiveData = LiveEvent<String>()
        every { viewModel.observationLoadErrors } answers { errorLiveData }

        val scenario = launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )

        scenario.onFragment {
            errorLiveData.value = testErrorMessage
        }

        onView(withText(testErrorMessage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun showsNoObservationsMessageWhenShowNoObservationsActive() {
        every { viewModel.showNoObservations } answers {
            liveData { emit(true) }
        }

        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )


        onView(withId(R.id.no_observations_added_text)).check(matches(isDisplayed()))
    }

    @Test
    fun hidesNoObservationsMessageWhenShowNoObservationsInactive() {
        every { viewModel.showNoObservations } answers {
            liveData { emit(false) }
        }

        launchFragmentInContainer<ObservationsListFragment>(
            fragmentArgs = null,
            themeResId = R.style.AppTheme,
            factory = fragmentFactory
        )


        onView(withId(R.id.no_observations_added_text)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    private val testData = listOf(
        Observation(
            1,
            "species1",
            Date(1100),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            2,
            "species2",
            Date(1200),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            3,
            "species3",
            Date(1300),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            4,
            "species4",
            Date(1400),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            5,
            "species5",
            Date(1500),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            6,
            "species6",
            Date(1600),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            7,
            "species7",
            Date(1700),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            8,
            "species8",
            Date(1800),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            9,
            "species9",
            Date(1900),
            null,
            ObservationRarity.Common,
            null,
            null
        ),
        Observation(
            10,
            "species10",
            Date(2000),
            null,
            ObservationRarity.Common,
            null,
            null
        )
    )
}