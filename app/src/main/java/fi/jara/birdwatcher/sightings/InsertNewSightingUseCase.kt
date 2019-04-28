package fi.jara.birdwatcher.sightings

import fi.jara.birdwatcher.common.SingleResultUseCase
import fi.jara.birdwatcher.common.location.LocationSource
import fi.jara.birdwatcher.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class InsertNewSightingUseCase(
    private val sightingRepository: SightingRepository,
    private val locationSource: LocationSource
) :
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

        val coordinate = try {
             if (params.addLocation) {
                withContext(Dispatchers.Default) {
                    locationSource.getCurrentLocation()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            onError("Error fetching location")
            return
        }

        val insertResult = withContext(Dispatchers.IO) {
            val timestamp = Date()
            val newSightingData = NewSightingData(
                params.species,
                timestamp,
                coordinate,
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