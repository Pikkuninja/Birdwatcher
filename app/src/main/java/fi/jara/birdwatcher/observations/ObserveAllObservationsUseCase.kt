package fi.jara.birdwatcher.observations

import fi.jara.birdwatcher.common.*
import fi.jara.birdwatcher.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveAllObservationsUseCase(private val observationRepository: ObservationRepository) :
    ObservableUseCase<ObservationSorting, ObservationStatus<List<Observation>>, String>() {

    override fun execute(params: ObservationSorting): Flow<ResultOrError<ObservationStatus<List<Observation>>, String>> =
        observationRepository.allObservations(params)
            .map {
                when (it) {
                    is StatusSuccess -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(
                        ValueFound(it.value)
                    )
                    is StatusLoading -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(
                        LoadingInitial()
                    )
                    is StatusEmpty -> ResultOrError.result<ObservationStatus<List<Observation>>, String>(
                        NotFound()
                    )
                    is StatusError -> ResultOrError.error(it.message)
                }
            }
}