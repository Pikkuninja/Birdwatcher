package fi.jara.birdwatcher.observations

import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.common.SingleResultUseCase
import fi.jara.birdwatcher.common.filesystem.ImageStorage
import fi.jara.birdwatcher.common.location.LocationSource
import fi.jara.birdwatcher.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

// TODO: make the failure states do something better than send hardcoded english messages
class InsertNewObservationUseCase(
    private val observationRepository: ObservationRepository,
    private val locationSource: LocationSource,
    private val imageStorage: ImageStorage
) : SingleResultUseCase<InsertNewObservationUseCaseParams, Unit, String>() {

    suspend operator fun invoke(
        species: String,
        addLocation: Boolean,
        rarity: ObservationRarity?,
        description: String?,
        imageBytes: ByteArray?
    ) = execute(
        InsertNewObservationUseCaseParams(
            species,
            addLocation,
            rarity,
            description,
            imageBytes
        )
    )

    override suspend fun execute(
        params: InsertNewObservationUseCaseParams
    ): Either<Unit, String> = withContext(Dispatchers.Default) {
        if (!checkSpeciesNameIsValid(params.species)) {
            return@withContext Either.Right("Species name is empty")
        }

        if (params.rarity == null) {
            return@withContext Either.Right("No rarity given")
        }

        val timestamp = Date()

        val coordinate = try {
            if (params.addLocation) {
                locationSource.getCurrentLocation()
            } else {
                null
            }
        } catch (e: Exception) {
            return@withContext Either.Right("Error fetching location")
        }

        val imagePath = params.imageBytes?.let {
            try {
                withContext(Dispatchers.IO) {
                    imageStorage.saveImageBytes(it, "${params.species}-$timestamp.jpg")
                }
            } catch (e: Exception) {
                return@withContext Either.Right("Error storing image")
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

        return@withContext when (insertResult) {
            is StatusSuccess -> Either.Left(Unit)
            is StatusError -> Either.Right(insertResult.message)
            else -> Either.Right("Unknown error while saving observation")
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