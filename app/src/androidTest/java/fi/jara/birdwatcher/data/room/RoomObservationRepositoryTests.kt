package fi.jara.birdwatcher.data.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.jraska.livedata.test
import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.data.*
import fi.jara.birdwatcher.observations.Observation
import fi.jara.birdwatcher.observations.ObservationRarity
import fi.jara.birdwatcher.observations.ObservationSorting
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import org.junit.rules.TestRule
import org.junit.Rule
import java.util.concurrent.TimeUnit


/**
 * The LiveData testing library used doesn't seem to catch the Loading status being emitted as the
 * results are emitted so fast in tests, so tests not interested in it can ignore it. Tests that want to
 * ensure that the Loading is emitted need to check the value of the LiveData immediately with the .value property
 */
@RunWith(AndroidJUnit4::class)
class RoomObservationRepositoryTests {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var database: ObservationDatabase
    private lateinit var repository: RoomObservationRepository

    @Before
    fun createRepository() {
        val context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, ObservationDatabase::class.java).build()
        repository = RoomObservationRepository(database)
    }

    @After
    fun closeDb() {
        database.close()
    }

    private fun insertAllTestData() {
        runBlocking {
            newObservationDatasInDatetimeAsc.forEach {
                repository.addObservation(it)
            }
        }
    }

    @Test
    fun observeAllFromEmptyRepository_emitsLoadingAndEmpty() {
        runBlocking {
            repository.allObservations(ObservationSorting.TimeAscending)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> assertTrue(value is StatusLoading<*>)
                        else -> assertTrue(value is StatusEmpty<*>)
                    }
                }
        }
    }

    @Test
    fun insertToRepository_allObservationsEmitsLiveDataWithAutoincrementedId() {
        runBlocking {
            repository.addObservation(newObservationDatasInDatetimeAsc[0])
            repository.allObservations(ObservationSorting.TimeAscending)
                .take(3)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertTrue(value is StatusLoading<*>)
                        }
                        1 -> {
                            assertEquals(StatusSuccess(listOf(obsevationSamplesInDatetimeAsc[0])), value)
                            repository.addObservation(newObservationDatasInDatetimeAsc[1])
                        }
                        2 -> {
                            assertEquals(StatusSuccess(listOf(obsevationSamplesInDatetimeAsc[0], obsevationSamplesInDatetimeAsc[1])), value)
                        }
                    }
                }
        }
    }

    @Test
    fun getAllInDatetimeAsc_correctOrder() {
        insertAllTestData()

        runBlocking {
            repository
                .allObservations(ObservationSorting.TimeAscending)
                .filter { it is StatusSuccess<*> }
                .take(1)
                .collect {
                    assertEquals(StatusSuccess(obsevationSamplesInDatetimeAsc), it)
                }
        }
    }

    @Test
    fun getAllInDatetimeDesc_correctOrder() {
        insertAllTestData()

        runBlocking {
            repository
                .allObservations(ObservationSorting.TimeDescending)
                .filter { it is StatusSuccess<*> }
                .take(1)
                .collect {
                    assertEquals(StatusSuccess(observationSamplesInDatetimeDesc), it)
                }
        }
    }

    @Test
    fun getAllInNameAsc_correctOrder() {
        insertAllTestData()

        runBlocking {
            repository
                .allObservations(ObservationSorting.NameAscending)
                .filter { it is StatusSuccess<*> }
                .take(1)
                .collect {
                    assertEquals(StatusSuccess(observationSamplesInNameAsc), it)
                }
        }
    }

    @Test
    fun getAllInNameDesc_correctOrder() {
        insertAllTestData()

        runBlocking {
            repository
                .allObservations(ObservationSorting.NameDescending)
                .filter { it is StatusSuccess<*> }
                .take(1)
                .collect {
                    assertEquals(StatusSuccess(observationSamplesInNameDesc), it)
                }
        }
    }

    @Test
    fun singleObservation_observeNonExisting_emitsLoadingAndEmpty() {
        runBlocking {
            repository.singleObservation(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> assertTrue(value is StatusLoading<*>)
                        else -> assertTrue(value is StatusEmpty<*>)
                    }
                }
        }
    }

    @Test
    fun singleObservation_observeExisting_emitsSuccess() {
        insertAllTestData()

        runBlocking {
            repository.singleObservation(1)
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> assertTrue(value is StatusLoading<*>)
                        else -> assertEquals(StatusSuccess(obsevationSamplesInDatetimeAsc[0]), value)
                    }
                }
        }
    }

    private val obsevationSamplesInDatetimeAsc = listOf(
        Observation(
            1,
            "Albratross",
            Date(100000),
            Coordinate(60.0, 20.0),
            ObservationRarity.ExtremelyRare,
            "somename.jpg",
            "Lorem ipsum"
        ),
        Observation(
            2,
            "Eagle",
            Date(200000),
            null,
            ObservationRarity.Rare,
            null,
            null
        ),
        Observation(
            3,
            "Owl",
            Date(300000),
            Coordinate(61.0, 23.0),
            ObservationRarity.Common,
            "othername.jpg",
            null
        ),
        Observation(
            4,
            "Falcon",
            Date(400000),
            Coordinate(50.0, 20.0),
            ObservationRarity.Rare,
            null,
            "Dolor sit amet"
        )
    )

    private val newObservationDatasInDatetimeAsc = listOf(
        NewObservationData(
            "Albratross",
            Date(100000),
            Coordinate(60.0, 20.0),
            ObservationRarity.ExtremelyRare,
            "somename.jpg",
            "Lorem ipsum"
        ),
        NewObservationData(
            "Eagle",
            Date(200000),
            null,
            ObservationRarity.Rare,
            null,
            null
        ),
        NewObservationData(
            "Owl",
            Date(300000),
            Coordinate(61.0, 23.0),
            ObservationRarity.Common,
            "othername.jpg",
            null
        ),
        NewObservationData(
            "Falcon",
            Date(400000),
            Coordinate(50.0, 20.0),
            ObservationRarity.Rare,
            null,
            "Dolor sit amet"
        )
    )

    private val observationSamplesInDatetimeDesc: List<Observation>
        get() = obsevationSamplesInDatetimeAsc.reversed()

    private val observationSamplesInNameAsc: List<Observation>
        get() = listOf(
            obsevationSamplesInDatetimeAsc[0],
            obsevationSamplesInDatetimeAsc[1],
            obsevationSamplesInDatetimeAsc[3],
            obsevationSamplesInDatetimeAsc[2]
        )

    private val observationSamplesInNameDesc: List<Observation>
        get() = observationSamplesInNameAsc.reversed()
}

