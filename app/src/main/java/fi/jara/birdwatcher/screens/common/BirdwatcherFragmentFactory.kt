package fi.jara.birdwatcher.screens.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import fi.jara.birdwatcher.screens.addobservation.AddObservationFragment
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment
import fi.jara.birdwatcher.screens.observationslist.ObservationsListFragment
import javax.inject.Inject
import javax.inject.Provider

class BirdwatcherFragmentFactory @Inject constructor(
    private val listFragProvider: Provider<ObservationsListFragment>,
    private val observationDetailsFragProvider: Provider<ObservationDetailsFragment>,
    private val addObservationFragProvider: Provider<AddObservationFragment>
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ObservationsListFragment::class.java.canonicalName -> listFragProvider.get()
            ObservationDetailsFragment::class.java.canonicalName -> observationDetailsFragProvider.get()
            AddObservationFragment::class.java.canonicalName -> addObservationFragProvider.get()
            else -> super.instantiate(classLoader, className)
        }
    }
}