package fi.jara.birdwatcher.observations

import fi.jara.birdwatcher.common.SingleResultUseCase
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.common.location.LocationSource
import fi.jara.birdwatcher.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class InsertNewObservationUseCase(
    private val observationRepository: ObservationRepository,
    private val locationSource: LocationSource,
    private val imageStorage: ImageStorage
) :
    SingleResultUseCase<InsertNewObservationUseCaseParams, Unit, String>() {
    override suspend fun execute(
        params: InsertNewObservationUseCaseParams,
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

        val timestamp = Date()

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

        val imagePath = params.imageBytes?.let {
            try {
                withContext(Dispatchers.IO) {
                    imageStorage.saveImageBytes(it, "${params.species}-$timestamp.jpg")
                }
            } catch (e: Exception) {
                onError("Error storing image")
                return
            }
        }

        val insertResult = withContext(Dispatchers.IO) {
            val newObservationData = NewObservationData(
                params.species,
                timestamp,
                coordinate,
                params.rarity,
                imagePath,
                params.description
            )

            observationRepository.addObservation(newObservationData)
        }

        when (insertResult) {
            is StatusSuccess -> onSuccess(Unit)
            is StatusError -> onError(insertResult.message)
            else -> onError("Unknown error while saving observation")
        }
    }

    private fun checkSpeciesNameIsValid(name: String): Boolean {
        return name.isNotBlank()
    }
}

class InsertNewObservationUseCaseParams(
    val species: String,
    val addLocation: Boolean,
    val rarity: ObservationRarity?,
    val description: String?,
    val imageBytes: ByteArray?
)