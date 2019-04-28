package fi.jara.birdwatcher.sightings

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.common.ObservableUseCase
import fi.jara.birdwatcher.common.ResultOrError
import fi.jara.birdwatcher.data.*

class ObserveAllSightingsUseCase(private val sightingRepository: SightingRepository) :
    ObservableUseCase<SightingSorting, AllSightingsLoadingStatus, String>() {

    override fun execute(params: SightingSorting): LiveData<ResultOrError<AllSightingsLoadingStatus, String>> {
        return Transformations.map(sightingRepository.allSightings(params)) {
            when (it) {
                is StatusSuccess -> ResultOrError.result<AllSightingsLoadingStatus, String>(SightingsFound(it.value))
                is StatusLoading -> ResultOrError.result<AllSightingsLoadingStatus, String>(Loading)
                is StatusEmpty -> ResultOrError.result<AllSightingsLoadingStatus, String>(NoSightings)
                is StatusError -> ResultOrError.error(it.message)
            }
        }
    }
}

sealed class AllSightingsLoadingStatus
object Loading : AllSightingsLoadingStatus()
object NoSightings : AllSightingsLoadingStatus()
class SightingsFound(val sightings: List<Sighting>) : AllSightingsLoadingStatus()