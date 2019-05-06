package fi.jara.birdwatcher.screens.observationslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import fi.jara.birdwatcher.common.*
import fi.jara.birdwatcher.observations.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

class ObservationsListViewModelTests {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val useCaseResults = MutableLiveData<ResultOrError<ObservationStatus<List<Observation>>, String>>()

    private lateinit var observeAllObservationsUseCaseMock: ObserveAllObservationsUseCase


    private lateinit var SUT: ObservationsListViewModel

    @Before
    fun setup() {
        useCaseResults.postValue(ResultOrError.result(LoadingInitial()))
        observeAllObservationsUseCaseMock = mockk()
        every { observeAllObservationsUseCaseMock.execute(any()) } returns useCaseResults

        SUT = ObservationsListViewModel(observeAllObservationsUseCaseMock)
    }

    @Test
    fun `changing sorting re-executes usecase`() {
        val observationsLiveData = SUT.observations.test()
        
        SUT.currentSorting = ObservationSorting.NameDescending

        verify(exactly = 2) { observeAllObservationsUseCaseMock.execute(allAny()) }
    }

    @Test
    fun `showNoObservations set to true when no observations found`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.result(NotFound()))

        SUT.showNoObservations.test().assertValue(true)
    }

    @Test
    fun `showNoObservations set to false when observations found`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.result(ValueFound(listOf(observation1))))

        SUT.showNoObservations.test().assertValue(false)
    }

    @Test
    fun `showLoading set to true when loading`() {
        val observationsLiveData = SUT.observations.test()

        SUT.showLoading.test().assertValue(true)
    }

    @Test
    fun `showLoading set to false when no observations found`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.result(NotFound()))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showLoading set to false when observations found`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.result(ValueFound(listOf(observation1))))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `observationLoadError is sent on error`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.error("error"))

        SUT.observationLoadErrors.test().assertHasValue()
    }

    @Test
    fun `showLoading set to false on error`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.error("error"))

        SUT.showLoading.test().assertValue(false)
    }

    @Test
    fun `showNoObservations not reset on error`() {
        val observationsLiveData = SUT.observations.test()
        val showNoObservationsLiveData = SUT.showNoObservations.test()

        useCaseResults.postValue(ResultOrError.result(NotFound()))
        val historyBeforeError = showNoObservationsLiveData.valueHistory().size
        useCaseResults.postValue(ResultOrError.error("error"))
        val historyAfterError = showNoObservationsLiveData.valueHistory().size

        assertEquals(historyBeforeError, historyAfterError)
    }

    @Test
    fun `observations updated when found`() {
        val observationsLiveData = SUT.observations.test()

        val foundValue = listOf(observation1)
        useCaseResults.postValue(ResultOrError.result(ValueFound(foundValue)))

        observationsLiveData.assertValue(foundValue)
    }

    @Test
    fun `old observations removed when updated to no observations`() {
        val observationsLiveData = SUT.observations.test()

        useCaseResults.postValue(ResultOrError.result(ValueFound(listOf(observation1))))
        useCaseResults.postValue(ResultOrError.result(NotFound()))

        observationsLiveData.assertValue(emptyList())
    }

    @Test
    fun `observations not reset on loading`() {
        val observationsLiveData = SUT.observations.test()

        val foundValue = listOf(observation1)
        useCaseResults.postValue(ResultOrError.result(ValueFound(foundValue)))
        useCaseResults.postValue(ResultOrError.error("error"))

        observationsLiveData.assertValue(foundValue)
    }

    @Test
    fun `observations not reset on error`() {
        val observationsLiveData = SUT.observations.test()

        val foundValue = listOf(observation1)
        useCaseResults.postValue(ResultOrError.result(ValueFound(foundValue)))
        useCaseResults.postValue(ResultOrError.result(LoadingInitial()))

        observationsLiveData.assertValue(foundValue)
    }

    val observation1 = Observation(1, "species", Date(1000), null, ObservationRarity.Common, null, null)
}