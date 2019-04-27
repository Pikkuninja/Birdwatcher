package fi.jara.birdwatcher.screens.addsighting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import fi.jara.birdwatcher.R

class AddSightingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_sighting_fragment, container, false)

        view.findViewById<Button>(R.id.add_sighting_save_button).setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        return view
    }
}
