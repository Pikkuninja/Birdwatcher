package fi.jara.birdwatcher.screens.addsighting


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import fi.jara.birdwatcher.sightings.SightingRarity
import kotlinx.android.synthetic.main.add_sighting_fragment.*
import javax.inject.Inject

class AddSightingFragment : BaseFragment() {
    private lateinit var viewModel: AddSightingViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresentationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.add_sighting_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddSightingViewModel::class.java)

        viewModel.addLocationToSighting.observe(viewLifecycleOwner, Observer {
            add_sighting_location_toggle.isChecked = it
        })

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner, Observer {
            add_sighting_save_button.isEnabled = it
        })


        viewModel.displayMessages.observe(viewLifecycleOwner, Observer { message ->
            view?.let { v ->
                Snackbar.make(v, message, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer {
            requestLocationPermission()
        })

        viewModel.gotoListScreen.observe(viewLifecycleOwner, Observer {
            gotoListScreen()
        })

        add_sighting_location_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onAddLocationToSightingToggled(isChecked)
        }

        add_sighting_save_button.setOnClickListener {
            val name = add_sighting_species.text?.toString() ?: ""
            val rarity = when (add_sighting_rarity_radiogroup.checkedRadioButtonId) {
                R.id.add_sighting_rarity_common -> SightingRarity.Common
                R.id.add_sighting_rarity_rare -> SightingRarity.Rare
                R.id.add_sighting_rarity_extremely_rare -> SightingRarity.ExtremelyRare
                else -> null
            }
            val description = add_sighting_description.text?.toString() ?: ""

            viewModel.onSaveSightingClicked(name, rarity, description)
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun gotoListScreen() {
        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val granted = grantResults.getOrNull(0) ?: PackageManager.PERMISSION_DENIED
            viewModel.onLocationPermissionRequestFinished(granted == PackageManager.PERMISSION_GRANTED)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 4321
    }
}
