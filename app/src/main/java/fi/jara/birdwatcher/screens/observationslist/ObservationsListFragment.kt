package fi.jara.birdwatcher.screens.observationslist

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.observations.ObservationSorting
import fi.jara.birdwatcher.screens.observationdetails.ObservationDetailsFragmentArgs
import kotlinx.android.synthetic.main.observations_list_fragment.*
import javax.inject.Inject

class ObservationsListFragment @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val observationsAdapter: ObservationsAdapter
) : Fragment() {
    private val viewModel: ObservationsListViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.observations_list_fragment, container, false)

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observations_recyclerview.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        observations_recyclerview.adapter = observationsAdapter
        observationsAdapter.clickListener = {
            findNavController().navigate(
                R.id.action_observationsListFragment_to_observationDetailsFragment,
                ObservationDetailsFragmentArgs(it.id).toBundle()
            )
        }

        add_observation_button.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_observationsListFragment_to_addObservationFragment,
                null
            )
        )

        subscribeToViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.observations_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.observation_sorting -> {
                showSortingOrderPopup()
                return true
            }
            R.id.show_about_app -> {
                showAboutPage()
                return true
            }
        }

        return false
    }

    private fun subscribeToViewModel() {
        viewModel.observations.observe(viewLifecycleOwner, Observer {
            observationsAdapter.submitList(it)
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.showNoObservations.observe(viewLifecycleOwner, Observer {
            no_observations_added_text.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.observationLoadErrors.observe(viewLifecycleOwner, Observer { errorMessage ->
            view?.let { v ->
                Snackbar.make(v, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun showSortingOrderPopup() {
        activity?.findViewById<View>(R.id.observation_sorting)?.let { itemView ->
            val popupMenu = PopupMenu(requireContext(), itemView)
            popupMenu.inflate(R.menu.observation_sortings_menu)

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

    private fun showAboutPage() {
        findNavController().navigate(R.id.action_observationsListFragment_to_aboutFragment)
    }
}

private fun sortingToMenuId(sorting: ObservationSorting): Int = when (sorting) {
    ObservationSorting.TimeDescending -> R.id.observation_sorting_datetime_desc
    ObservationSorting.TimeAscending -> R.id.observation_sorting_datetime_asc
    ObservationSorting.NameAscending -> R.id.observation_sorting_name_asc
    ObservationSorting.NameDescending -> R.id.observation_sorting_name_desc
}

private fun menuIdToSorting(id: Int): ObservationSorting? = when (id) {
    R.id.observation_sorting_datetime_desc -> ObservationSorting.TimeDescending
    R.id.observation_sorting_datetime_asc -> ObservationSorting.TimeAscending
    R.id.observation_sorting_name_asc -> ObservationSorting.NameAscending
    R.id.observation_sorting_name_desc -> ObservationSorting.NameDescending
    else -> null
}