package fi.jara.birdwatcher.mocks

import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.common.location.LocationSource
import java.lang.Exception

class MockLocationSource(val result: Coordinate?): LocationSource {
    override suspend fun getCurrentLocation(): Coordinate {
        return result ?: throw Exception("Fetching location failed")
    }
}