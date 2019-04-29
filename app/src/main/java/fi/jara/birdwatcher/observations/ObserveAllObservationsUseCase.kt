package fi.jara.birdwatcher.observations

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.common.ObservableUseCase
import fi.jara.birdwatcher.common.ResultOrError
import fi.jara.birdwatcher.data.*

class ObserveAllObservationsUseCase(private val observationRepository: ObservationRepository) :
    ObservableUseCase<ObservationSorting, AllObservationsLoadingStatus, String>() {

    override fun execute(params: ObservationSorting): LiveData<ResultOrError<AllObservationsLoadingStatus, String>> {
        return Transformations.map(observationRepository.allObservations(params)) {
            when (it) {
                is StatusSuccess -> ResultOrError.result<AllObservationsLoadingStatus, String>(ObservationsFound(it.value))
                is StatusLoading -> ResultOrError.result<AllObservationsLoadingStatus, String>(Loading)
                is StatusEmpty -> ResultOrError.result<AllObservationsLoadingStatus, String>(NoObservations)
                is StatusError -> ResultOrError.error(it.message)
            }
        }
    }
}

sealed class AllObservationsLoadingStatus
object Loading : AllObservationsLoadingStatus()
object NoObservations : AllObservationsLoadingStatus()
class ObservationsFound(val observations: List<Observation>) : AllObservationsLoadingStatus()