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
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.screens.common.BaseFragment
import fi.jara.birdwatcher.screens.common.ViewModelFactory
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingSorting
import kotlinx.android.synthetic.main.sightings_list_fragment.*
import javax.inject.Inject

class SightingsListFragment : BaseFragment() {
    private lateinit var viewModel: SightingsListViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sightingsAdapter: ListAdapter<Sighting, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresentationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sightings_list_fragment, container, false)

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.sightings_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = sightingsAdapter

        view.findViewById<FloatingActionButton>(R.id.add_sighting_button).setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.addSightingFragment, null)
        )

        subscribeToViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sighting_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sighting_sorting -> {
                showSortingOrderPopup()
                return true
            }
        }

        return false
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SightingsListViewModel::class.java)

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

    private fun showSortingOrderPopup() {
        activity?.findViewById<View>(R.id.sighting_sorting)?.let { itemView ->
            val popupMenu = PopupMenu(requireContext(), itemView)
            popupMenu.inflate(R.menu.sighting_sortings_menu)

            popupMenu.menu.findItem(sortingToMenuId(viewModel.currentSorting)).isChecked = true

            popupMenu.setOnMenuItemClickListener { sortingMenuItem ->
                menuIdToSorting(sortingMenuItem.itemId)?.let {
                    viewModel.currentSorting = it
                }
                popupMenu.dismiss()
                true
            }

            popupMenu.show()
        }
    }
}

private fun sortingToMenuId(sorting: SightingSorting): Int = when (sorting) {
    SightingSorting.TimeDescending -> R.id.sighting_sorting_datetime_desc
    SightingSorting.TimeAscending -> R.id.sighting_sorting_datetime_asc
    SightingSorting.NameAscending -> R.id.sighting_sorting_name_asc
    SightingSorting.NameDescending -> R.id.sighting_sorting_name_desc
}

private fun menuIdToSorting(id: Int): SightingSorting? = when (id) {
    R.id.sighting_sorting_datetime_desc -> SightingSorting.TimeDescending
    R.id.sighting_sorting_datetime_asc -> SightingSorting.TimeAscending
    R.id.sighting_sorting_name_asc -> SightingSorting.NameAscending
    R.id.sighting_sorting_name_desc -> SightingSorting.NameDescending
    else -> null
}