package fi.jara.birdwatcher.sightings

import java.util.*

data class Sighting(val species: String,
                    val timestamp: Date,
                    val location: String,
                    val rarity: SightingRarity,
                    val imageName: String?,
                    val description: String?)

enum class SightingRarity {
    Common,
    Rare,
    ExtremelyRare
}