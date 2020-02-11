package fi.jara.birdwatcher.screens.addobservation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import fi.jara.CoroutinesMainDispatcherRule
import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.common.filesystem.BitmapResolver
import fi.jara.birdwatcher.observations.InsertNewObservationUseCase
import fi.jara.birdwatcher.observations.ObservationRarity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine

class AddObservationViewModelTests {
    @get:Rule
    var instantLiveDataExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    @MockK
    lateinit var insertNewObservationUseCaseMock: InsertNewObservationUseCase
    @MockK
    lateinit var bitmapResolverMock: BitmapResolver

    lateinit var SUT: AddObservationViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        SUT = AddObservationViewModel(insertNewObservationUseCaseMock, bitmapResolverMock)
        setupUseCaseSuccess()
        setupBitmapResolverSuccess()
    }

    @Test
    fun `saveButtonEnabled set to false when inserting`() {
        setupUseCaseNotReturning()
        val liveData = SUT.saveButtonEnabled.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.assertValue(false)
    }

    @Test
    fun `saveButtonEnabled stays false after insertion success`() {
        val liveData = SUT.saveButtonEnabled.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.assertValue(false)
    }

    @Test
    fun `saveButtonEnabled set to true after insertion failue`() {
        setupUseCaseError()
        val liveData = SUT.saveButtonEnabled.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.assertValue(true)
    }

    @Test
    fun `userImageUri set after imageUriRequestFinished with proper uri`() {
        SUT.onImageUriRequestFinished(mockk())

        SUT.userImageUri.test().assertHasValue()
    }

    @Test
    fun `userImageUri null after imageUriRequestFinished with null`() {
        SUT.onImageUriRequestFinished(null)

        SUT.userImageUri.test().assertValue(null)
    }

    @Test
    fun `displayMessages event sent after insertion success`() {
        val liveData = SUT.displayMessages.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.assertHistorySize(1)
    }

    @Test
    fun `displayMessages event sent after insertion failure`() {
        val liveData = SUT.displayMessages.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.assertHistorySize(1)
    }

    @Test
    fun `gotoListScreen event sent after insertion success`() {
        val liveData = SUT.gotoListScreen.test()

        SUT.onSaveObservationClicked("species", ObservationRarity.Common, "description")

        liveData.awaitNextValue(1100, TimeUnit.MILLISECONDS).assertHistorySize(1)
    }

    @Test
    fun `addLocationToObservation set to true after onAddLocationToObservationToggled called with true`() {
        val liveData = SUT.addLocationToObservation.test()

        SUT.onAddLocationToObservationToggled(true)

        liveData.assertValue(true)
    }

    @Test
    fun `addLocationToObservation set to false after onAddLocationToObservationToggled called with false`() {
        val liveData = SUT.addLocationToObservation.test()

        SUT.onAddLocationToObservationToggled(false)

        liveData.assertValue(false)
    }

    @Test
    fun `requestLocationPermission event sent after onAddLocationToObservationToggled called with true`() {
        val liveData = SUT.requestLocationPermission.test()

        SUT.onAddLocationToObservationToggled(true)

        liveData.assertHasValue()
    }

    @Test
    fun `requestLocationPermission event not sent after onAddLocationToObservationToggled call if permission previously given`() {
        val liveData = SUT.requestLocationPermission.test()

        SUT.onLocationPermissionRequestFinished(true)
        SUT.onAddLocationToObservationToggled(true)

        liveData.assertNoValue()
    }

    @Test
    fun `addLocationToObservation set to false after onLocationPermissionRequestFinished called with false`() {
        val liveData = SUT.addLocationToObservation.test()

        SUT.onAddLocationToObservationToggled(true)
        SUT.onLocationPermissionRequestFinished(false)

        liveData.assertValue(false)
    }

    // TODO: Check if this duplication can be removed easily (or just remove the duplicated way of calling the usecase...)
    private fun setupUseCaseSuccess() {
        coEvery { insertNewObservationUseCaseMock.execute(any()) } returns Either.Left(Unit)

        coEvery {
            insertNewObservationUseCaseMock.invoke(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Either.Left(Unit)
    }

    private fun setupUseCaseError() {
        coEvery { insertNewObservationUseCaseMock.execute(any()) } returns Either.Right("error message")

        coEvery {
            insertNewObservationUseCaseMock.invoke(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Either.Right("error message")
    }

    private fun setupUseCaseNotReturning() {
        coEvery { insertNewObservationUseCaseMock.execute(any()) }.coAnswers {
            suspendCoroutine {
                // never continue
            }
        }

        coEvery {
            insertNewObservationUseCaseMock.invoke(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }.coAnswers {
            suspendCoroutine {
                // never continue
            }
        }
    }

    private fun setupBitmapResolverSuccess() {
        coEvery { bitmapResolverMock.getBitmap(any()) }.returns(mockk())
        coEvery { bitmapResolverMock.getBytes(any()) }.returns(byteArrayOf(1, 2, 3, 4))
    }


    private fun setupBitmapResolverError() {
        coEvery { bitmapResolverMock.getBitmap(any()) }.throws(IOException())
    }

    private fun setupBitmapResolverNotReturning() {
        coEvery { bitmapResolverMock.getBitmap(any()) }.coAnswers {
            suspendCoroutine {
                // never continue
            }
        }
    }
}