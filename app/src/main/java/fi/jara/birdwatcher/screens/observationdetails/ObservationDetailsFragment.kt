package fi.jara.birdwatcher.screens.observationdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.screens.common.BaseFragment
import kotlinx.android.synthetic.main.observation_details_fragment.*
import javax.inject.Inject

class ObservationDetailsFragment : BaseFragment() {
    private lateinit var viewModel: ObservationDetailsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPresentationComponent()
            .observationDetailsBuilder()
            .bindFragment(this)
            .build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(
            R.layout.observation_details_fragment, container, false
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObservationDetailsViewModel::class.java)

        viewModel.observation.observe(viewLifecycleOwner, Observer {
            @SuppressLint("SetTextI18n")
            observation_info_temp_text.text = "TODO: Actual view. Observation: $it"
            observation_info_temp_text.visibility = View.VISIBLE
            observation_loading_error_text.visibility = View.GONE
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.observationLoadErrors.observe(viewLifecycleOwner, Observer {
            if (observation_info_temp_text.visibility != View.VISIBLE) {
                if (it != null) {
                    observation_loading_error_text.visibility = View.GONE
                } else {
                    observation_loading_error_text.text = it
                    observation_loading_error_text.visibility = View.VISIBLE
                }
            }

        })
    }
}