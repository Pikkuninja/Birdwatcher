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
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

class ObserveAllObservationsUseCaseTests {
    @Rule
    @JvmField
    var liveDataRule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var coroutineRule: TestRule = CoroutinesMainDispatcherRule()

    @Test
    fun `emits loading and empty on no results`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsChannel.offer(StatusLoading())
        repo.allObservationsChannel.offer(StatusEmpty())

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is NotFound)
    }

    @Test
    fun `emits loading and value when observations exits`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsChannel.offer(StatusLoading())
        repo.allObservationsChannel.offer(StatusSuccess(listOf(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null))))

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is ValueFound)
    }

    @Test
    fun `emits new values when added to repository`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsChannel.offer(StatusLoading())
        repo.allObservationsChannel.offer(StatusSuccess(listOf(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null))))
        repo.allObservationsChannel.offer(StatusSuccess(listOf(
            Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null),
            Observation(2, "Eagle", Date(2000), null, ObservationRarity.Rare, null, null))
        ))

        val history = liveData.valueHistory()
        assertEquals(3, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is ValueFound)
        assert(history[2].result is ValueFound)
    }

    @Test
    fun `emits error on repository failure`() {
        val (repo, useCase) = repoAndUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsChannel.offer(StatusLoading())
        repo.allObservationsChannel.offer(StatusError("Error message"))

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assertNotNull(history[1].errorMessage)
    }

    private val repoAndUseCase: Pair<MockObservationRepository, ObserveAllObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveAllObservationsUseCase(repository)
        }
}