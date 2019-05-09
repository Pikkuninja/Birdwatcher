package fi.jara.birdwatcher.screens.observationdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fi.jara.birdwatcher.observations.ObserveSingleObservationsUseCase
import io.mockk.mockk
import org.junit.Rule
import org.junit.rules.TestRule

class ObservationDetailsViewModelTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observeSingleObservationsUseCaseMock: ObserveSingleObservationsUseCase = mockk()

    private val SUT = ObservationDetailsViewModel(observeSingleObservationsUseCaseMock, 1)
}