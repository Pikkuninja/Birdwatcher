package fi.jara.birdwatcher.data


sealed class RepositoryLoadingStatus<T>
class StatusLoading<T>: RepositoryLoadingStatus<T>()
class StatusSuccess<T>(val value: T): RepositoryLoadingStatus<T>()
class StatusEmpty<T>: RepositoryLoadingStatus<T>()
class StatusError<T>(val message: String): RepositoryLoadingStatus<T>()
