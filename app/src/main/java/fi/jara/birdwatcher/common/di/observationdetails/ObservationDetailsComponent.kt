package fi.jara.birdwatcher.common.di.observationdetails

import dagger.BindsInstance
import dagger.Subcomponent
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragment

// This package seems a bit out of place, it's under 'common' but then there's a screen specific subpackage?
// Maybe move this to observationdetails package and while at it, presentation component & module to screens/common?

// Another thing to consider would be to get rid of this component, pass BaseFragment to presentation component
// and make the Providers check if the Fragment is of appropriate type
@Subcomponent(modules = [ObservationDetailsModule::class])
interface ObservationDetailsComponent {
    fun inject(observationDetailsFragment: ObservationDetailsFragment)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun bindFragment(fragment: ObservationDetailsFragment): Builder
        fun build(): ObservationDetailsComponent
    }
}