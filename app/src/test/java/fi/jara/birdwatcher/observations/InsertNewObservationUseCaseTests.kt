package fi.jara.birdwatcher.observations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.mocks.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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
            useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ),
                { },
                {
                    fail(it)
                })
        }
    }

    @Test
    fun `empty name fails`() {
        val useCase = succeedingUseCase

        runBlocking {
            useCase.execute(
                InsertNewObservationUseCaseParams(
                    "",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ),
                { fail("Succeeded when needed to fail") },
                {
                    assertEquals("Species name is empty", it)
                })
        }
    }

    @Test
    fun `no rarity fails`() {
        val useCase = succeedingUseCase

        runBlocking {
            runBlocking {
                useCase.execute(
                    InsertNewObservationUseCaseParams(
                        "Albatross",
                        true,
                        null,
                        "There's no Albatrosses in Finland, right?",
                        ByteArray(1000) { it.toByte() }
                    ),
                    { fail("Succeeded when needed to fail") },
                    {
                        assertEquals("No rarity given", it)
                    })
            }
        }
    }

    @Test
    fun `valid input with add location fails on location error`() {
        val useCase = locationFailingUseCase

        runBlocking {
            useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ),
                { fail("Succeeded when needed to fail") },
                {
                    assertEquals("Error fetching location", it)
                })
        }
    }

    @Test
    fun `valid input with image data fails on image storage error`() {
        val useCase = imageStoreFailingUseCase

        runBlocking {
            useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ),
                { fail("Succeeded when needed to fail") },
                {
                    assertEquals("Error storing image", it)
                })
        }
    }

    @Test
    fun `valid input fails on repository error`() {
        val useCase = repositoryFailingUseCase

        runBlocking {
            useCase.execute(
                InsertNewObservationUseCaseParams(
                    "Albatross",
                    true,
                    ObservationRarity.Rare,
                    "There's no Albatrosses in Finland, right?",
                    ByteArray(1000) { it.toByte() }
                ),
                { fail("Succeeded when needed to fail") },
                {
                    assertEquals(AlwaysFailingMockObservationRepository.MOCK_OBSERVATION_REPOSITORY_ERROR_MESSAGE, it)
                })
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
        get() = InsertNewObservationUseCase(
            AlwaysFailingMockObservationRepository(),
            MockLocationSource(Coordinate(60.0, 20.0)),
            AlwaysSucceedingImageStrorage()
        )

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
}