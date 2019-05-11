package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.data.StatusEmpty
import fi.jara.birdwatcher.data.StatusLoading
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.mocks.AlwaysFailingMockObservationRepository
import fi.jara.birdwatcher.mocks.MockObservationRepository
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*
import java.util.concurrent.TimeUnit

class ObserveAllObservationsUseCaseTests {
    // Needed for LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `emits loading and empty on no results`() {
        val (repo, useCase) = succeedingUseCase
        repo.allObservationsLiveData.value = StatusLoading()

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsLiveData.value = StatusEmpty()

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is NotFound)
    }

    @Test
    fun `emits loading and value when observations exits`() {
        val (repo, useCase) = succeedingUseCase
        repo.allObservationsLiveData.value = StatusLoading()

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsLiveData.value = StatusSuccess(listOf(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null)))

        val history = liveData.valueHistory()
        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is ValueFound)
    }

    @Test
    fun `emits new values when added to repository`() {
        val (repo, useCase) = succeedingUseCase
        repo.allObservationsLiveData.value = StatusLoading()

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test()

        repo.allObservationsLiveData.value = StatusSuccess(listOf(Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null)))
        repo.allObservationsLiveData.value = StatusSuccess(listOf(
            Observation(1, "Albatross", Date(1000), null, ObservationRarity.Rare, null, null),
            Observation(2, "Eagle", Date(2000), null, ObservationRarity.Rare, null, null))
        )

        val history = liveData.valueHistory()
        assertEquals(3, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is ValueFound)
        assert(history[2].result is ValueFound)
    }

    @Test
    fun `emits error on repository failure`() {
        val useCase = failingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue(1, TimeUnit.SECONDS)  // goes from loading to error
        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assertNotNull(history[1].errorMessage)
    }

    private val succeedingUseCase: Pair<MockObservationRepository, ObserveAllObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveAllObservationsUseCase(repository)
        }

    private val failingUseCase: ObserveAllObservationsUseCase
        get() = ObserveAllObservationsUseCase(AlwaysFailingMockObservationRepository())
}