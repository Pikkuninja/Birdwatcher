package fi.jara.birdwatcher.screens.observationdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import fi.jara.CoroutinesMainDispatcherRule
import fi.jara.birdwatcher.common.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ObservationDetailsViewModelTests {
    @Rule
    @JvmField
    var liveDataRule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var coroutineRule: TestRule = CoroutinesMainDispatcherRule()

    private val observeSingleObservationsUseCaseMock: ObserveSingleObservationsUseCase = mockk()
    private val mockUseCaseResults = Channel<ResultOrError<ObservationStatus<Observation>, String>>(Channel.CONFLATED)

    lateinit var SUT: ObservationDetailsViewModel

    @Before
    fun setup() {
        mockUseCaseResults.offer(ResultOrError.result(LoadingInitial()))
        coEvery { observeSingleObservationsUseCaseMock.execute(any()) } answers { mockUseCaseResults.consumeAsFlow() }

        SUT = ObservationDetailsViewModel(observeSingleObservationsUseCaseMock, 1)
    }

    @Test
    fun `observation has value when found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.result(ValueFound(mockk())))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading true when observation is initially loading`() {
        val observationSubscription = SUT.observation.test()

        SUT.showLoading.test().assertValue(true)
    }

    @Test
    fun `showLoading false when observation is found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.result(ValueFound(mockk())))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading false when observation is not found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.result(NotFound()))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading false when observation errors`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.error("Error"))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `observationLoadError null when observation is initially loading`() {
        val observationSubscription = SUT.observation.test()

        SUT.observationLoadErrors.test().assertValue(null)
    }

    @Test
    fun `observationLoadError null when observation is found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.result(ValueFound(mockk())))

        SUT.observationLoadErrors.test().assertValue(null)
    }

    @Test
    fun `observationLoadError 'Observation not found' when observation is not found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.result(NotFound()))

        SUT.observationLoadErrors.test().assertValue("Observation not found")
    }

    @Test
    fun `observationLoadError has value when observation errors`() {
        val errorMessage = "Error"
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.offer(ResultOrError.error(errorMessage))

        SUT.observationLoadErrors.test().assertValue(errorMessage)
    }
}