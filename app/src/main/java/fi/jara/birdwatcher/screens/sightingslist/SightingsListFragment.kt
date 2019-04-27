package fi.jara.birdwatcher.screens.sightingslist


import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.sightings.SightingSorting
import kotlinx.android.synthetic.main.sightings_list_fragment.*

class SightingsListFragment : Fragment() {
    private lateinit var viewModel: SightingsListViewModel
    private val sightingsAdapter = SightingsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sightings_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.sightings_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = sightingsAdapter

        view.findViewById<FloatingActionButton>(R.id.add_sighting_button).setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.addSightingFragment, null)
        )

        setHasOptionsMenu(true)

        subscribeToViewModel()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sighting_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sighting_sorting -> {
                activity?.findViewById<View>(R.id.sighting_sorting)?.let { itemView ->
                    val popupMenu = PopupMenu(requireContext(), itemView)
                    popupMenu.inflate(R.menu.sighting_sortings_menu)

                    val curItemId = when (viewModel.currentSorting) {
                        SightingSorting.TimeDescending -> R.id.sighting_sorting_datetime_desc
                        SightingSorting.TimeAscending -> R.id.sighting_sorting_datetime_asc
                        SightingSorting.NameAscending -> R.id.sighting_sorting_name_asc
                        SightingSorting.NameDescending -> R.id.sighting_sorting_name_desc
                    }
                    popupMenu.menu.findItem(curItemId).isChecked = true
                    popupMenu.setOnMenuItemClickListener { sortingMenuItem ->
                        val newSorting = when (sortingMenuItem.itemId) {
                            R.id.sighting_sorting_datetime_desc -> SightingSorting.TimeDescending
                            R.id.sighting_sorting_datetime_asc -> SightingSorting.TimeAscending
                            R.id.sighting_sorting_name_asc -> SightingSorting.NameAscending
                            R.id.sighting_sorting_name_desc -> SightingSorting.NameDescending
                            else -> null
                        }

                        newSorting?.let { viewModel.currentSorting = it }
                        popupMenu.dismiss()

                        true
                    }

                    popupMenu.show()

                }
                return true
            }
        }

        return false
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this).get(SightingsListViewModel::class.java)

        viewModel.sightings.observe(viewLifecycleOwner, Observer {
            sightingsAdapter.submitList(it)
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.showNoSightings.observe(viewLifecycleOwner, Observer {
            no_sightings_added_text.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.sightingLoadErrors.observe(viewLifecycleOwner, Observer { errorMessage ->
            view?.let { v ->
                Snackbar.make(v, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        })
    }
}