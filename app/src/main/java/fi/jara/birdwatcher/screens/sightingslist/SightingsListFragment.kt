package fi.jara.birdwatcher.screens.sightingslist


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.jara.birdwatcher.R

class SightingsListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sightings_list_fragment, container, false)

        view.findViewById<FloatingActionButton>(R.id.add_sighting_button).setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.addSightingFragment, null)
        )

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sighting_list_menu, menu)
    }
}
