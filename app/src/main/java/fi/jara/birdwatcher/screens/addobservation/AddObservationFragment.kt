package fi.jara.birdwatcher.screens.addobservation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.observations.ObservationRarity
import kotlinx.android.synthetic.main.add_observation_fragment.*
import javax.inject.Inject

class AddObservationFragment : BaseFragment() {
    private lateinit var viewModel: AddObservationViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresentationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.add_observation_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddObservationViewModel::class.java)

        viewModel.addLocationToObservation.observe(viewLifecycleOwner, Observer {
            add_observation_location_toggle.isChecked = it
        })

        viewModel.userImageBitmap.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                add_observation_image_preview.setImageBitmap(it)
                add_observation_image_preview.contentDescription = resources.getString(R.string.user_image_attached)
                add_observation_image_toggle.isChecked = true
            } else {
                add_observation_image_preview.setImageResource(R.drawable.ic_imageplaceholder_black_24dp)
                add_observation_image_preview.contentDescription = resources.getString(R.string.no_image_attached)
                add_observation_image_toggle.isChecked = false
            }
        })

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner, Observer {
            add_observation_save_button.isEnabled = it
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

        add_observation_location_toggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAddLocationToObservationToggled(isChecked)
        }

        add_observation_image_toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestImageFromGallery()
            } else {
                viewModel.removeImage()
            }
        }

        add_observation_save_button.setOnClickListener {
            val name = add_observation_species.text?.toString() ?: ""
            val rarity = when (add_observation_rarity_radiogroup.checkedRadioButtonId) {
                R.id.add_observation_rarity_common -> ObservationRarity.Common
                R.id.add_observation_rarity_rare -> ObservationRarity.Rare
                R.id.add_observation_rarity_extremely_rare -> ObservationRarity.ExtremelyRare
                else -> null
            }
            val description = add_observation_description.text?.toString() ?: ""

            viewModel.onSaveObservationClicked(name, rarity, description)
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun requestImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, resources.getString(R.string.select_image)),
            IMAGE_FROM_GALLERY_REQUEST_CODE
        )
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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_FROM_GALLERY_REQUEST_CODE) {
            val uri = data?.data
            if (resultCode == Activity.RESULT_OK && uri != null) {
                viewModel.setImage(uri)
            } else {
                viewModel.removeImage()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 4321
        private const val IMAGE_FROM_GALLERY_REQUEST_CODE = 4322
    }
}
