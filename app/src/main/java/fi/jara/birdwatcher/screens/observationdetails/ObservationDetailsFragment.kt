package fi.jara.birdwatcher.screens.observationdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.screens.common.stringResourceId
import kotlinx.android.synthetic.main.observation_details_fragment.*
import java.text.DateFormat
import javax.inject.Inject

class ObservationDetailsFragment : BaseFragment() {
    private lateinit var viewModel: ObservationDetailsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dateFormat: DateFormat

    @Inject
    lateinit var imageStorage: ImageStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presentationComponent
            .observationDetailsBuilder()
            .bindFragment(this)
            .build()
            .inject(this)
    }

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(
            R.layout.observation_details_fragment, container, false
        )

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObservationDetailsViewModel::class.java)

        viewModel.observation.observe(viewLifecycleOwner, Observer { observation ->
            observation_species.text = observation.species
            observation_datetime.text = dateFormat.format(observation.timestamp)
            observation_rarity.text = resources.getString(observation.rarity.stringResourceId())
            observation_location.text = observation.location?.let {
                resources.getString(R.string.location_lat_lon, it.latitude, it.longitude)
            } ?: resources.getString(R.string.location_no_data)
            observation_description.text = observation.description

            observation.imageName?.let {
                Picasso.get().load(imageStorage.getUriFor(observation.imageName))
                    .placeholder(R.drawable.ic_imageplaceholder_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(observation_image)
            } ?: run {
                observation_image.setImageDrawable(null)
            }

            observation_loading_error_text.visibility = View.GONE
            observation_detailscontainer.visibility = View.VISIBLE
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.observationLoadErrors.observe(viewLifecycleOwner, Observer {
            if (observation_detailscontainer.visibility != View.VISIBLE) {
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