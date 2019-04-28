package fi.jara.birdwatcher.common.di.presentation


import dagger.Subcomponent
import fi.jara.birdwatcher.screens.addsighting.AddSightingFragment
import fi.jara.birdwatcher.screens.sightingslist.SightingsListFragment

@Subcomponent(modules = [ViewModelModule::class])
interface PresentationComponent {
    fun inject(sightingsListFragment: SightingsListFragment)
    fun inject(addSightingFragment: AddSightingFragment)
}