package fi.jara.birdwatcher.screens

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.liveData
import fi.jara.birdwatcher.common.filesystem.AndroidImageSaver
import fi.jara.birdwatcher.observations.ObservationSorting
import fi.jara.birdwatcher.screens.about.AboutFragment
import fi.jara.birdwatcher.screens.addobservation.AddObservationFragment
import fi.jara.birdwatcher.screens.addobservation.AddObservationViewModel
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsViewModel
import fi.jara.birdwatcher.screens.observationslist.ObservationsListFragment
import fi.jara.birdwatcher.screens.observationslist.ObservationsListViewModel
import io.mockk.every
import io.mockk.mockk
import java.text.DateFormat

/// FragmentFactory that can create all of the apps Fragments
/// with mocked deps in a way that the Fragment can be safely launched
/// To override the default config, provide a lambda for it in the constructor
class TestFragmentFactory(private val context: Context, nonDefaults: Map<Class<Fragment>, () -> Fragment> = emptyMap()) :
    FragmentFactory() {
    private val classNamedNonDefaults = nonDefaults.mapKeys { it.key.name }

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val nonDefaultCreator = classNamedNonDefaults[className]
        if (nonDefaultCreator != null) {
            return nonDefaultCreator()
        }

        return when (className) {
            ObservationsListFragment::class.java.name -> createDefaultObservationsListFragment()
            AddObservationFragment::class.java.name -> createDefaultAddObservationFragment()
            ObservationDetailsFragment::class.java.name -> createDefaultObservationDetailsFragment()
            AboutFragment::class.java.name -> AboutFragment()
            else -> super.instantiate(classLoader, className)
        }
    }

    private fun createDefaultObservationsListFragment(): ObservationsListFragment {
        val viewModel = mockObservationsListViewModel()
        val vmFactoryCreator = mockObservationsListViewModelFactoryCreator {
            viewModel
        }

        return ObservationsListFragment(vmFactoryCreator, mockk(relaxed = true))
    }

    private fun createDefaultAddObservationFragment(): AddObservationFragment {
        val viewModel = mockAddObservationViewModel()

        val vmFactoryCreator = mockAddObservationViewModelFactoryCreator {
            viewModel
        }

        return AddObservationFragment(vmFactoryCreator)
    }

    private fun createDefaultObservationDetailsFragment(): ObservationDetailsFragment {
        val viewModel = mockObservationDetailsViewModel()

        val vmFactoryCreator = mockObservationDetailsViewModelFactoryCreator {
            viewModel
        }

        return ObservationDetailsFragment(vmFactoryCreator, DateFormat.getDateTimeInstance(), AndroidImageSaver(context))
    }
}