package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import fi.jara.birdwatcher.common.LoadingInitial
import fi.jara.birdwatcher.common.NotFound
import fi.jara.birdwatcher.common.ValueFound
import fi.jara.birdwatcher.data.NewObservationData
import fi.jara.birdwatcher.mocks.AlwaysFailingMockObservationRepository
import fi.jara.birdwatcher.mocks.MockObservationRepository
import kotlinx.coroutines.runBlocking
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
        val (_, useCase) = succeedingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue(1, TimeUnit.SECONDS) // goes from loading to empty
        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is NotFound)
    }

    @Test
    fun `emits loading and value when observations exits`() {
        val (repo, useCase) = succeedingUseCase

        runBlocking {
            repo.addObservation(NewObservationData("Albatross", Date(1000), null, ObservationRarity.Rare, null, null))
            repo.addObservation(NewObservationData("Eagle", Date(2000), null, ObservationRarity.ExtremelyRare, null, null))
        }

        useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue(1, TimeUnit.SECONDS).assertValue { it.result is ValueFound }
    }

    @Test
    fun `emits new values when added to repository`() {

        val (repo, useCase) = succeedingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue(1, TimeUnit.SECONDS) // goes from loading to empty

        runBlocking {
            repo.addObservation(NewObservationData("Albatross", Date(1000), null, ObservationRarity.Rare, null, null))
            repo.addObservation(NewObservationData("Eagle", Date(2000), null, ObservationRarity.ExtremelyRare, null, null))
        }

        liveData.assertHistorySize(4) // loading, empty, 1 observation, 2 observations
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