package fi.jara.birdwatcher.screens.observationdetails

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import fi.jara.birdwatcher.common.di.observationdetails.ObservationDetailsModule
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import javax.inject.Inject

class ObservationDetailsFragment: BaseFragment() {
    private lateinit var viewModel: ObservationDetailsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPresentationComponent()
            .observationDetailsBuilder()
            .observationDetailsModule(ObservationDetailsModule(this))
            .build()
            .inject(this)

        subscribeToViewModel()
    }

    fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObservationDetailsViewModel::class.java)
    }
}