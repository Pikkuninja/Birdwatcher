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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ObservationDetailsViewModelTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    private val observeSingleObservationsUseCaseMock: ObserveSingleObservationsUseCase = mockk()
    private val mockUseCaseResults = MutableLiveData<ResultOrError<ObservationStatus<Observation>, String>>()

    lateinit var SUT: ObservationDetailsViewModel

    @Before
    fun setup() {
        mockUseCaseResults.value = ResultOrError.result(LoadingInitial())
        coEvery { observeSingleObservationsUseCaseMock.execute(any()) }.returns(mockUseCaseResults)

        SUT = ObservationDetailsViewModel(observeSingleObservationsUseCaseMock, 1)
    }

    @Test
    fun `observation has value when found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.value = ResultOrError.result(ValueFound(mockk()))

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
        mockUseCaseResults.value = ResultOrError.result(ValueFound(mockk()))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading false when observation is not found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.value = ResultOrError.result(NotFound())

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading false when observation errors`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.value = ResultOrError.error("Error")

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
        mockUseCaseResults.value = ResultOrError.result(ValueFound(mockk()))

        SUT.observationLoadErrors.test().assertValue(null)
    }

    @Test
    fun `observationLoadError 'Observation not found' when observation is not found`() {
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.value = ResultOrError.result(NotFound())

        SUT.observationLoadErrors.test().assertValue("Observation not found")
    }

    @Test
    fun `observationLoadError has value when observation errors`() {
        val errorMessage = "Error"
        val observationSubscription = SUT.observation.test()
        mockUseCaseResults.value = ResultOrError.error(errorMessage)

        SUT.observationLoadErrors.test().assertValue(errorMessage)
    }
}