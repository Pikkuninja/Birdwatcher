package fi.jara.birdwatcher.screens.common

import fi.jara.birdwatcher.R
import fi.jara.birdwatcher.observations.ObservationRarity


fun ObservationRarity.stringResourceId(): Int = when (this) {
    ObservationRarity.Common -> R.string.rarity_common
    ObservationRarity.Rare -> R.string.rarity_rare
    ObservationRarity.ExtremelyRare -> R.string.rarity_extremely_rare
}
