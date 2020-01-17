package fi.jara.birdwatcher.common

abstract class SingleResultUseCase<Params, Result, ErrorType> {
    abstract suspend fun execute(params: Params): Either<Result, ErrorType>
}

sealed class Either<out A, out B> {
    class Left<A>(val value: A): Either<A, Nothing>()
    class Right<B>(val value: B): Either<Nothing, B>()
}