package fi.jara.birdwatcher.screens.sightingslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.sightings.Sighting


class SightingsAdapter : ListAdapter<Sighting, SightingViewHolder>(SightingDiffChecker()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SightingViewHolder =
        SightingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.sightings_listitem,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SightingViewHolder, position: Int) {
        holder.bindToSighting(getItem(position))
    }

}

class SightingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.sighting_listitem_title)
    private val datetime = itemView.findViewById<TextView>(R.id.sighting_listitem_datetime)
    private val location = itemView.findViewById<TextView>(R.id.sighting_listitem_location)
    private val rarity = itemView.findViewById<TextView>(R.id.sighting_listitem_rarity)
    private val description = itemView.findViewById<TextView>(R.id.sighting_listitem_description)

    fun bindToSighting(sighting: Sighting) {
        title.text = sighting.species
        datetime.text = sighting.timestamp.toString()
        location.text = sighting.location
        rarity.text = sighting.rarity.toString()
        description.text = sighting.description
    }
}

// Sightings have only simple data types and they can't be modified after they have been created
// so the DiffChecker implementation is trivial
class SightingDiffChecker : DiffUtil.ItemCallback<Sighting>() {
    override fun areItemsTheSame(oldItem: Sighting, newItem: Sighting): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Sighting, newItem: Sighting): Boolean = oldItem == newItem
}

