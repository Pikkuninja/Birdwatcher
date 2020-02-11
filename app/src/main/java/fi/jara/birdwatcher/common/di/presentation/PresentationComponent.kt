package fi.jara.birdwatcher.common.di.presentation

import androidx.fragment.app.FragmentFactory
import dagger.Subcomponent
import fi.jara.birdwatcher.screens.MainActivity
import fi.jara.birdwatcher.screens.addobservation.AddObservationFragment
import fi.jara.birdwatcher.screens.observationslist.ObservationsListFragment

@Subcomponent(modules = [PresentationModule::class, ViewModelFactoryModule::class, FragmentFactoryModule::class])
interface PresentationComponent {
    fun inject(mainActivity: MainActivity)

    fun fragmentFactory(): FragmentFactory
}