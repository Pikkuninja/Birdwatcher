package fi.jara.birdwatcher.screens.addsighting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import javax.inject.Inject

class AddSightingFragment : BaseFragment() {
    private lateinit var viewModel: AddSightingViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresentationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_sighting_fragment, container, false)


        view.findViewById<Button>(R.id.add_sighting_save_button).setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        return view
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddSightingViewModel::class.java)
    }
}
