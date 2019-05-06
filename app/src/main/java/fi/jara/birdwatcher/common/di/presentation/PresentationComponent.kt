package fi.jara.birdwatcher.common.di.presentation

import dagger.Subcomponent
import fi.jara.birdwatcher.common.di.observationdetails.ObservationDetailsComponent
import fi.jara.birdwatcher.screens.addobservation.AddObservationFragment
import fi.jara.birdwatcher.screens.observationslist.ObservationsListFragment

@Subcomponent(modules = [PresentationModule::class, ViewModelModule::class])
interface PresentationComponent {
    fun observationDetailsBuilder(): ObservationDetailsComponent.Builder
    fun inject(observationsListFragment: ObservationsListFragment)
    fun inject(addObservationFragment: AddObservationFragment)
}