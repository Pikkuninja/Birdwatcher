package fi.jara.birdwatcher.screens.observationslist

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
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationRarity
import java.text.DateFormat


class ObservationsAdapter(private val imageStorage: ImageStorage) :
    ListAdapter<Observation, ObservationViewHolder>(ObservationDiffChecker()) {
    private val dateFormat: DateFormat = DateFormat.getDateTimeInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationViewHolder =
        ObservationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.observations_listitem,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) {
        holder.bindToObservation(getItem(position), dateFormat, imageStorage)
    }

}

class ObservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.observation_listitem_title)
    private val datetime = itemView.findViewById<TextView>(R.id.observation_listitem_datetime)
    private val location = itemView.findViewById<TextView>(R.id.observation_listitem_location)
    private val rarity = itemView.findViewById<TextView>(R.id.observation_listitem_rarity)
    private val description = itemView.findViewById<TextView>(R.id.observation_listitem_description)
    private val image = itemView.findViewById<ImageView>(R.id.observation_listitem_image)

    fun bindToObservation(observation: Observation, dateFormat: DateFormat, imageStorage: ImageStorage) {
        title.text = observation.species
        datetime.text = dateFormat.format(observation.timestamp)
        rarity.text = itemView.resources.getString(rarityToResourceId(observation.rarity))
        description.text = observation.description

        location.text = observation.location?.let {
            itemView.resources.getString(R.string.location_lat_lon, it.latitude, it.longitude)
        } ?: itemView.resources.getString(R.string.location_no_data)

        observation.imageName?.let {
            Picasso.get().load(imageStorage.getUriFor(observation.imageName))
                .placeholder(R.drawable.ic_imageplaceholder_black_24dp)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(image)
        } ?: run {
            image.setImageResource(R.drawable.ic_imageplaceholder_black_24dp)
        }
    }
}

class ObservationDiffChecker : DiffUtil.ItemCallback<Observation>() {
    override fun areItemsTheSame(oldItem: Observation, newItem: Observation): Boolean = oldItem.id == newItem.id

    // This could also use just the IDs as the observations can't be changed after being stored
    override fun areContentsTheSame(oldItem: Observation, newItem: Observation): Boolean = oldItem == newItem
}

private fun rarityToResourceId(rarity: ObservationRarity) = when (rarity) {
    ObservationRarity.Common -> R.string.rarity_common
    ObservationRarity.Rare -> R.string.rarity_rare
    ObservationRarity.ExtremelyRare -> R.string.rarity_extremely_rare
}