package fi.jara.birdwatcher.common

import kotlinx.coroutines.flow.Flow

abstract class ObservableUseCase<Params, ResultType, ErrorType> {
    abstract fun execute(params: Params): Flow<ResultOrError<ResultType, ErrorType>>
}

class ResultOrError<ResultType, ErrorType> private constructor(
    val result: ResultType?,
    val errorMessage: ErrorType?
) {
    companion object {
        fun <ResultType, ErrorType> result(result: ResultType): ResultOrError<ResultType, ErrorType> = ResultOrError(result, null)
        fun <ResultType, ErrorType> error(error: ErrorType): ResultOrError<ResultType, ErrorType> = ResultOrError(null, error)
    }
}

sealed class ObservationStatus<out ResultType>
class LoadingInitial<out ResultType>: ObservationStatus<ResultType>()
class NotFound<out ResultType> : ObservationStatus<ResultType>()
class ValueFound<out ResultType>(val value: ResultType) : ObservationStatus<ResultType>()
