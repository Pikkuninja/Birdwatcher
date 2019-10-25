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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*
import java.util.concurrent.TimeUnit

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

        val liveData = useCase.execute(1).test()

        repo.singleObservationChannel.offer(StatusLoading())
        repo.singleObservationChannel.offer(StatusEmpty())

        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is NotFound)
    }

    @Test
    fun `emits value if initially found in repository`() {
        val (repo, useCase) = repoAndUseCase
        repo.singleObservationChannel.offer(StatusLoading())

        val liveData = useCase.execute(1).test()

        repo.singleObservationChannel.offer(StatusSuccess(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null)))

        liveData.assertValue { it.result is ValueFound }
    }

    @Test
    fun `emits value if added to repository later`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(1).test()

        repo.singleObservationChannel.offer(StatusLoading())
        repo.singleObservationChannel.offer(StatusEmpty())
        repo.singleObservationChannel.offer(StatusSuccess(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null)))

        liveData.assertHistorySize(3) // loading, empty, found
    }

    @Test
    fun `emits error on repository failure`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(1).test()

        repo.singleObservationChannel.offer(StatusLoading())
        repo.singleObservationChannel.offer(StatusError("Error message"))

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assertNotNull(history[1].errorMessage)
    }


    private val repoAndUseCase: Pair<MockObservationRepository, ObserveSingleObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveSingleObservationsUseCase(repository)
        }
}