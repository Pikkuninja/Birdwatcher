package fi.jara.birdwatcher.sightings

import java.util.*

// TODO: Proper timezone support with ThreetenBP or some other solution
data class Sighting(
    val id: Long,
    val species: String,
    val timestamp: Date,
    val location: String,
    val rarity: SightingRarity,
    val imageName: String?,
    val description: String?
)

enum class SightingRarity {
    Common,
    Rare,
    ExtremelyRare
}