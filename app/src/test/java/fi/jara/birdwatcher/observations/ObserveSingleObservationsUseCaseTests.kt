package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import fi.jara.CoroutinesMainDispatcherRule
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.data.StatusEmpty
import fi.jara.birdwatcher.data.StatusError
import fi.jara.birdwatcher.data.StatusLoading
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.mocks.MockObservationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ObserveSingleObservationsUseCaseTests {
    @Rule
    @JvmField
    var liveDataRule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var coroutineRule: TestRule = CoroutinesMainDispatcherRule()

    @Test
    fun `emits loading and empty on no results`() {
        val (repo, useCase) = repoAndUseCase

        runBlocking {
            repo.singleObservationChannel.send(StatusLoading())
            useCase.execute(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assert(value.result is LoadingInitial)
                            repo.singleObservationChannel.send(StatusEmpty())
                        }
                        1 -> {
                            assertNotNull(value.result is NotFound)
                        }
                    }
                }
        }
    }

    @Test
    fun `emits value if initially found in repository`() {
        val (repo, useCase) = repoAndUseCase

        runBlocking {
            repo.singleObservationChannel.send(StatusLoading())

            useCase.execute(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assert(value.result is LoadingInitial)
                            repo.singleObservationChannel.send(
                                StatusSuccess(
                                    Observation(
                                        1,
                                        "Albatross",
                                        Date(1000),
                                        null,
                                        ObservationRarity.Rare,
                                        null,
                                        null
                                    )
                                )
                            )
                        }
                        1 -> {
                            assertNotNull(value.result is ValueFound)
                        }
                    }
                }
        }
    }

    @Test
    fun `emits value if added to repository later`() {
        val (repo, useCase) = repoAndUseCase

        runBlocking {
            repo.singleObservationChannel.send(StatusLoading())
            useCase.execute(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assert(value.result is LoadingInitial)
                            repo.singleObservationChannel.send(StatusEmpty())
                        }
                        1 -> {
                            assert(value.result is NotFound)
                            repo.singleObservationChannel.send(
                                StatusSuccess(
                                    Observation(
                                        1,
                                        "Albatross",
                                        Date(1000),
                                        null,
                                        ObservationRarity.Rare,
                                        null,
                                        null
                                    )
                                )
                            )
                        }
                        2 -> {
                            assert(value.result is ValueFound)
                        }
                    }
                }
        }
    }

    @Test
    fun `emits error on repository failure`() {
        val (repo, useCase) = repoAndUseCase

        runBlocking {
            repo.singleObservationChannel.send(StatusLoading())

            useCase.execute(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assert(value.result is LoadingInitial)
                            repo.singleObservationChannel.send(StatusError("Error message"))
                        }
                        1 -> {
                            assertNotNull(value.errorMessage)
                        }
                    }
                }
        }
    }


    private val repoAndUseCase: Pair<MockObservationRepository, ObserveSingleObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveSingleObservationsUseCase(repository)
        }
}