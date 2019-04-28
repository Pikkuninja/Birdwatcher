package fi.jara.birdwatcher.common

import androidx.lifecycle.LiveData

// TODO: switch to Kotlin Flows

abstract class ObservableUseCase<Params, ResultType, ErrorType> {
    abstract fun execute(params: Params): LiveData<ResultOrError<ResultType, ErrorType>>
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