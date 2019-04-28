package fi.jara.birdwatcher.sightings

import fi.jara.birdwatcher.common.Coordinate
import java.util.*

// TODO: Proper timezone support with ThreetenBP or some other solution
data class Sighting(
    val id: Long,
    val species: String,
    val timestamp: Date,
    val location: Coordinate?,
    val rarity: SightingRarity,
    val imageName: String?,
    val description: String?
)

enum class SightingRarity {
    Common,
    Rare,
    ExtremelyRare
}