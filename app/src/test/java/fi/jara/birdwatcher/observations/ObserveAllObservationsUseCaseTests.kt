package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import fi.jara.birdwatcher.data.NewObservationData
import fi.jara.birdwatcher.data.StatusError
import fi.jara.birdwatcher.mocks.AlwaysFailingMockObservationRepository
import fi.jara.birdwatcher.mocks.MockObservationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

class ObserveAllObservationsUseCaseTests {
    // Needed for LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `emits loading and empty on no results`() {
        val (_, useCase) = succeedingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue() // goes from laoding to empty
        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assert(history[0].result is Loading)
        assert(history[1].result is NoObservations)
    }

    @Test
    fun `emits new values when added to repository`() {

        val (repo, useCase) = succeedingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue() // goes from laoding to empty

        runBlocking {
            repo.addObservation(NewObservationData("Albatross", Date(1000), null, ObservationRarity.Rare, null, null))
            repo.addObservation(NewObservationData("Eagle", Date(2000), null, ObservationRarity.ExtremelyRare, null, null))
        }

        liveData.assertHistorySize(4) // loading, empty, 1 observation, 2 observations
    }

    @Test
    fun `emits error on repository failure`() {
        val useCase = failingUseCase

        val liveData = useCase.execute(ObservationSorting.TimeDescending).test().awaitNextValue()  // goes from loading to error
        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assertNotNull(history[1].errorMessage)
    }

    val succeedingUseCase: Pair<MockObservationRepository, ObserveAllObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveAllObservationsUseCase(repository)
        }

    val failingUseCase: ObserveAllObservationsUseCase
        get() = ObserveAllObservationsUseCase(AlwaysFailingMockObservationRepository())
}