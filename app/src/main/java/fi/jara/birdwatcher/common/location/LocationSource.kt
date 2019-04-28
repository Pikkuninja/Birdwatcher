package fi.jara.birdwatcher.common.location

import fi.jara.birdwatcher.common.Coordinate

interface LocationSource {
    suspend fun getCurrentLocation(): Coordinate
}