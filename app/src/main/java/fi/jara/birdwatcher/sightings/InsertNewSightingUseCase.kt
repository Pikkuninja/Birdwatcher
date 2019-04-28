package fi.jara.birdwatcher.sightings

import fi.jara.birdwatcher.common.SingleResultUseCase
import fi.jara.birdwatcher.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            return
        }

        if (params.rarity == null) {
            onError("No rarity given")
            return
        }

        val insertResult = withContext(Dispatchers.IO) {
            val timestamp = Date()
            val newSightingData = NewSightingData(
                params.species,
                timestamp,
                null,
                params.rarity,
                null,
                params.description
            )

            sightingRepository.addSighting(newSightingData)
        }

        when (insertResult) {
            is StatusSuccess -> onSuccess(Unit)
            is StatusError -> onError(insertResult.message)
            else -> onError("Unknown error while saving sighting")
        }
    }

    private fun checkSpeciesNameIsValid(name: String): Boolean {
        return name.isNotBlank()
    }
}

data class InsertNewSightingUseCaseParams(
    val species: String,
    val addLocation: Boolean,
    val rarity: SightingRarity?,
    val description: String?
)