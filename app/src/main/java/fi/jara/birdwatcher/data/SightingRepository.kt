package fi.jara.birdwatcher.data

import androidx.lifecycle.LiveData
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingRarity
import fi.jara.birdwatcher.sightings.SightingSorting
import java.util.*

/*
    Using LiveData in repository layer adds dependency to Android libraries and
    that's kind of bad when RxJava could be used to achieve the same thing and has
    more options on operators and threading. RxJava isn't included in the project
    as the intention is to handle async with coroutines especially when Flows
    come out of early access, and its easier to fix this one thing than pull out RxJava
    that has been used to full extent
 */

interface SightingRepository {
    // TODO: Use Kotlin Flows when out of early access
    fun allSightings(sorting: SightingSorting): LiveData<RepositoryLoadingStatus<List<Sighting>>>

    suspend fun addSighting(sightingData: NewSightingData): RepositoryLoadingStatus<Sighting>
}

data class NewSightingData(
    val species: String,
    val timestamp: Date,
    val location: String,
    val rarity: SightingRarity,
    val imageName: String?,
    val description: String?
)