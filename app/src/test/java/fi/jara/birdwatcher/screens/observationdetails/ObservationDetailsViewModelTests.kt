package fi.jara.birdwatcher.screens.observationdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

class ObservationDetailsViewModelTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var SUT: ObservationDetailsViewModel


    @Before
    fun setup() {
        SUT = ObservationDetailsViewModel()
    }
}