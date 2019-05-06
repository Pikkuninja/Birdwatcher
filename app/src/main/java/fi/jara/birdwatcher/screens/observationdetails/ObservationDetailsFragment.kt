package fi.jara.birdwatcher.screens.observationdetails

import android.os.Bundle
import fi.jara.birdwatcher.common.di.observationdetails.ObservationDetailsModule
import fi.jara.birdwatcher.screens.common.BaseFragment
import javax.inject.Inject

class ObservationDetailsFragment: BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ObservationDetailsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPresentationComponent()
            .observationDetailsBuilder()
            .observationDetailsModule(ObservationDetailsModule(this))
            .build()
            .inject(this)
    }
}