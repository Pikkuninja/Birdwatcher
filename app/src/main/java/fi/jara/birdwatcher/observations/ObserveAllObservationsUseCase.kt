package fi.jara.birdwatcher.observations

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fi.jara.birdwatcher.common.*
import fi.jara.birdwatcher.data.*

class ObserveAllObservationsUseCase(private val observationRepository: ObservationRepository) :
    ObservableUseCase<ObservationSorting, ObservationStatus<List<Observation>>, String>() {

    override fun execute(params: ObservationSorting): LiveData<ResultOrError<ObservationStatus<List<Observation>>, String>> {
        return Transformations.map(observationRepository.allObservations(params)) {
            when (it) {
                is StatusSuccess -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(ValueFound(it.value))
                is StatusLoading -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(LoadingInitial())
                is StatusEmpty -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(NotFound())
                is StatusError -> ResultOrError.error(it.message)
            }
        }
    }
}