package fi.jara.birdwatcher.sightings

import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.common.SingleResultUseCase
import fi.jara.birdwatcher.data.*
import java.util.*

class InsertNewSightingUseCase(private val sightingRepository: SightingRepository) :
    SingleResultUseCase<InsertNewSightingUseCaseParams, Unit, String>() {
    override suspend fun execute(
        params: InsertNewSightingUseCaseParams,
        onSuccess: (Unit) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!checkSpeciesNameIsValid(params.species)) {
            onError("Species name is empty")
        }

        if (params.rarity == null) {
            onError("No rarity given")
            return
        }

        val timestamp = Date()
        val newSightingData = NewSightingData(
            params.species,
            timestamp,
            params.location,
            params.rarity,
            null,
            params.description
        )

        when (val result = sightingRepository.addSighting(newSightingData)) {
            is StatusSuccess -> onSuccess(Unit)
            is StatusError -> onError(result.message)
            else -> onError("Unknown error while saving sighting")
        }
    }

    private fun checkSpeciesNameIsValid(name: String): Boolean {
        return name.isNotBlank()
    }
}

data class InsertNewSightingUseCaseParams(
    val species: String,
    val location: Coordinate?,
    val rarity: SightingRarity?,
    val description: String?
)