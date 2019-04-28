package fi.jara.birdwatcher.common

abstract class SingleResultUseCase<Params, Result, ErrorType> {
    abstract suspend fun execute(params: Params, onSuccess: (Result) -> Unit, onError: (ErrorType) -> Unit)
}