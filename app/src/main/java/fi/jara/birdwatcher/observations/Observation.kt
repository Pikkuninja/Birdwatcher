package fi.jara.birdwatcher.observations

import fi.jara.birdwatcher.common.Coordinate
import java.util.*

// TODO: Proper timezone support with ThreetenBP or some other solution
data class Observation(
    val id: Long,
    val species: String,
    val timestamp: Date,
    val location: Coordinate?,
    val rarity: ObservationRarity,
    val imageName: String?,
    val description: String?
)

enum class ObservationRarity {
    Common,
    Rare,
    ExtremelyRare
}