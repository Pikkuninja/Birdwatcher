package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.common.Either
import fi.jara.birdwatcher.data.StatusError
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.mocks.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

// TODO: Fix the usecases error reporting mechanism to support i18n and to be more testing friendly
// for finding out specific error causes. Or maybe checking for the error cause is too specific?
class InsertNewObservationUseCaseTests {
    // Needed for LiveData
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `valid input succeeds`() {
        val useCase = succeedingUseCase

        runBlocking {
            val result = useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ))

            assertTrue(result is Either.Left)
        }
    }

    @Test
    fun `empty name fails`() {
        val useCase = succeedingUseCase

        runBlocking {
            val result = useCase.execute(InsertNewObservationUseCaseParams(
                "",
                true,
                ObservationRarity.Rare,
                "There's no Albatrosses in Finland, right?",
                ByteArray(1000) { it.toByte() }
            ))

            assertTrue(result is Either.Right)
            assertEquals("Species name is empty", (result as Either.Right).value)
        }
    }

    @Test
    fun `no rarity fails`() {
        val useCase = succeedingUseCase

        runBlocking {
            val result = useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    null,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ))

            assertTrue(result is Either.Right)
            assertEquals("No rarity given", (result as Either.Right).value)
        }
    }

    @Test
    fun `valid input with add location fails on location error`() {
        val useCase = locationFailingUseCase

        runBlocking {
            val result = useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ))

            assertTrue(result is Either.Right)
            assertEquals("Error fetching location", (result as Either.Right).value)
        }
    }

    @Test
    fun `valid input with image data fails on image storage error`() {
        val useCase = imageStoreFailingUseCase

        runBlocking {
            val result = useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ))

            assertTrue(result is Either.Right)
            assertEquals("Error storing image", (result as Either.Right).value)
        }
    }

    @Test
    fun `valid input fails on repository error`() {
        val useCase = repositoryFailingUseCase

        runBlocking {
            val result = useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ))

            assertTrue(result is Either.Right)
            assertEquals(repositoryErrorMessage, (result as Either.Right).value)
        }
    }

    // TODO: inspect setuping these with Dagger?
    private val succeedingUseCase: InsertNewObservationUseCase
        get() {
            val repo = MockObservationRepository()
            repo.addObservationReturnValue = StatusSuccess(mockk())
            return InsertNewObservationUseCase(
                repo,
                MockLocationSource(Coordinate(60.0, 20.0)),
                AlwaysSucceedingImageStrorage()
            )
        }


    private val repositoryFailingUseCase: InsertNewObservationUseCase
        get() {
            val repo = MockObservationRepository()
            repo.addObservationReturnValue = StatusError(repositoryErrorMessage)
            return InsertNewObservationUseCase(
                repo,
                MockLocationSource(Coordinate(60.0, 20.0)),
                AlwaysSucceedingImageStrorage()
            )
        }

    private val locationFailingUseCase: InsertNewObservationUseCase
        get() = InsertNewObservationUseCase(
            MockObservationRepository(),
            MockLocationSource(null),
            AlwaysSucceedingImageStrorage()
        )

    private val imageStoreFailingUseCase: InsertNewObservationUseCase
        get() = InsertNewObservationUseCase(
            MockObservationRepository(),
            MockLocationSource(Coordinate(60.0, 20.0)),
            AlwaysFailingImageStorage()
        )

    private val repositoryErrorMessage = "Error message from repository"
}