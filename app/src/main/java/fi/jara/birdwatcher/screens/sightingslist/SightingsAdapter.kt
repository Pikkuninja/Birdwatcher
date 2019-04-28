package fi.jara.birdwatcher.screens.sightingslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingRarity
import java.text.DateFormat


class SightingsAdapter(private val imageStorage: ImageStorage) :
    ListAdapter<Sighting, SightingViewHolder>(SightingDiffChecker()) {
    private val dateFormat: DateFormat = DateFormat.getDateTimeInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SightingViewHolder =
        SightingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.sightings_listitem,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SightingViewHolder, position: Int) {
        holder.bindToSighting(getItem(position), dateFormat, imageStorage)
    }

}

class SightingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.sighting_listitem_title)
    private val datetime = itemView.findViewById<TextView>(R.id.sighting_listitem_datetime)
    private val location = itemView.findViewById<TextView>(R.id.sighting_listitem_location)
    private val rarity = itemView.findViewById<TextView>(R.id.sighting_listitem_rarity)
    private val description = itemView.findViewById<TextView>(R.id.sighting_listitem_description)
    private val image = itemView.findViewById<ImageView>(R.id.sighting_listitem_image)

    fun bindToSighting(sighting: Sighting, dateFormat: DateFormat, imageStorage: ImageStorage) {
        title.text = sighting.species
        datetime.text = dateFormat.format(sighting.timestamp)
        rarity.text = itemView.resources.getString(rarityToResourceId(sighting.rarity))
        description.text = sighting.description

        location.text = sighting.location?.let {
            itemView.resources.getString(R.string.location_lat_lon, it.latitude, it.longitude)
        } ?: itemView.resources.getString(R.string.location_no_data)

        sighting.imageName?.let {
            Picasso.get().load(imageStorage.getUriFor(sighting.imageName))
                .placeholder(R.drawable.ic_imageplaceholder_black_24dp)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(image)
        } ?: run {
            image.setImageResource(R.drawable.ic_imageplaceholder_black_24dp)
        }
    }
}

class SightingDiffChecker : DiffUtil.ItemCallback<Sighting>() {
    override fun areItemsTheSame(oldItem: Sighting, newItem: Sighting): Boolean = oldItem.id == newItem.id

    // This could also use just the IDs as the sightings can't be changed after being stored
    override fun areContentsTheSame(oldItem: Sighting, newItem: Sighting): Boolean = oldItem == newItem
}

private fun rarityToResourceId(rarity: SightingRarity) = when (rarity) {
    SightingRarity.Common -> R.string.rarity_common
    SightingRarity.Rare -> R.string.rarity_rare
    SightingRarity.ExtremelyRare -> R.string.rarity_extremely_rare
}