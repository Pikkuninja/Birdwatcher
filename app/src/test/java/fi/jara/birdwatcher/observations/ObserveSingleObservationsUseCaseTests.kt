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
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*
import java.util.concurrent.TimeUnit

class ObserveSingleObservationsUseCaseTests {
    // Needed for LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `emits loading and empty on no results`() {
        val (_, useCase) = succeedingUseCase

        val liveData = useCase.execute(1).test().awaitNextValue(1, TimeUnit.SECONDS)
        val history = liveData.valueHistory()

        assertEquals(2, history.size)
        assert(history[0].result is LoadingInitial)
        assert(history[1].result is NotFound)
    }

    @Test
    fun `emits value if initially found in repository`() {
        val (repo, useCase) = succeedingUseCase

        runBlocking {
            repo.addObservation(NewObservationData("Albatross", Date(1000), null, ObservationRarity.Rare, null, null))
        }

        val liveData = useCase.execute(1).test().awaitNextValue(1, TimeUnit.SECONDS) // goes from loading to value

        liveData.assertValue { it.result is ValueFound }
    }

    @Ignore("Doesn't work in CI, works locally?")
    @Test
    fun `emits value if added to repository later`() {
        val (repo, useCase) = succeedingUseCase

        val liveData = useCase.execute(1).test().awaitNextValue(1, TimeUnit.SECONDS) // goes from loading to empty

        runBlocking {
            repo.addObservation(NewObservationData("Albatross", Date(1000), null, ObservationRarity.Rare, null, null))
        }

        liveData.assertHistorySize(3) // loading, empty, found
    }

    @Test
    fun `emits error on repository failure`() {
        val useCase = failingUseCase

        useCase.execute(1).test().awaitNextValue(1, TimeUnit.SECONDS).assertValue { it.errorMessage != null }
    }

    private val succeedingUseCase: Pair<MockObservationRepository, ObserveSingleObservationsUseCase>
        get() {
            val repository = MockObservationRepository()
            return repository to ObserveSingleObservationsUseCase(repository)
        }

    private val failingUseCase: ObserveSingleObservationsUseCase
        get() = ObserveSingleObservationsUseCase(AlwaysFailingMockObservationRepository())
}