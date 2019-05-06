package fi.jara.birdwatcher.observations


import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.common.*
import fi.jara.birdwatcher.data.*

class ObserveSingleObservationsUseCase(private val observationRepository: ObservationRepository) :
    ObservableUseCase<Long, ObservationStatus<Observation>, String>() {

    override fun execute(params: Long): LiveData<ResultOrError<ObservationStatus<Observation>, String>> {
        return Transformations.map(observationRepository.singleObservation(params)) {
            when (it) {
                is StatusSuccess -> ResultOrError.result<ObservationStatus<Observation>, String>(ValueFound(it.value))
                is StatusLoading -> ResultOrError.result<ObservationStatus<Observation>, String>(LoadingInitial())
                is StatusEmpty -> ResultOrError.result<ObservationStatus<Observation>, String>(NotFound())
                is StatusError -> ResultOrError.error(it.message)
            }
        }
    }
}