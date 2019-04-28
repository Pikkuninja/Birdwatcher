package fi.jara.birdwatcher.data.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.jraska.livedata.test
import fi.jara.birdwatcher.data.NewSightingData
import fi.jara.birdwatcher.data.StatusEmpty
import fi.jara.birdwatcher.data.StatusLoading
import fi.jara.birdwatcher.data.StatusSuccess
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingRarity
import fi.jara.birdwatcher.sightings.SightingSorting
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.*
import org.junit.rules.TestRule
import org.junit.Rule


/**
 * The LiveData testing library used doesn't seem to catch the Loading status being emitted as the
 * results are emitted so fast in tests, so tests not interested in it can ignore it. Tests that want to
 * ensure that the Loading is emitted need to check the value of the LiveData immediately with the .value property
 */
@RunWith(AndroidJUnit4::class)
class RoomSightingRepositoryTests {
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var database: SightingDatabase
    private lateinit var repository: RoomSightingRepository

    @Before
    fun createRepository() {
        val context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, SightingDatabase::class.java).build()
        repository = RoomSightingRepository(database)
    }

    @After
    fun closeDb() {
        database.close()
    }

    private fun insertAllTestData() {
        runBlocking {
            newSightingDatasInDatetimeAsc.forEach {
                repository.addSighting(it)
            }
        }
    }

    @Test
    fun observeEmptyRepository_emitsLoadingAndEmpty() {
        val liveData = repository.allSightings(SightingSorting.TimeAscending)
        val initialValue = liveData.value
        assert(initialValue is StatusLoading)

        liveData.test().assertValue { it is StatusEmpty }
    }

    @Test
    fun insertToRepository_emitsLiveDataWithAutoincrementedId() {
        val liveData = repository.allSightings(SightingSorting.TimeAscending).test()

        runBlocking {
            repository.addSighting(newSightingDatasInDatetimeAsc[0])
        }

        liveData.assertValue(StatusSuccess(listOf(sightingsSamplesInDatetimeAsc[0])))

        runBlocking {
            repository.addSighting(newSightingDatasInDatetimeAsc[1])
        }

        liveData.assertValue(StatusSuccess(listOf(sightingsSamplesInDatetimeAsc[0], sightingsSamplesInDatetimeAsc[1])))
    }

    @Test
    fun getAllInDatetimeAsc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.TimeAscending)
            .test()
            .assertValue(StatusSuccess(sightingsSamplesInDatetimeAsc))
    }

    @Test
    fun getAllInDatetimeDesc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.TimeDescending)
            .test()
            .assertValue(StatusSuccess(sightingSamplesInDatetimeDesc))
    }

    @Test
    fun getAllInNameAsc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.NameAscending)
            .test()
            .assertValue(StatusSuccess(sightingSamplesInNameAsc))
    }

    @Test
    fun getAllInNameDesc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.NameDescending)
            .test()
            .assertValue(StatusSuccess(sightingSamplesInNameDesc))
    }

    private val sightingsSamplesInDatetimeAsc = listOf(
        Sighting(
            1,
            "Albratross",
            Date.from(Instant.ofEpochMilli(100000)),
            "Helsinki",
            SightingRarity.ExtremelyRare,
            "somename.jpg",
            "Lorem ipsum"
        ),
        Sighting(
            2,
            "Eagle",
            Date.from(Instant.ofEpochMilli(200000)),
            "Tampere",
            SightingRarity.Rare,
            null,
            null
        ),
        Sighting(
            3,
            "Owl",
            Date.from(Instant.ofEpochMilli(300000)),
            "Pori",
            SightingRarity.Common,
            "othername.jpg",
            null
        ),
        Sighting(
            4,
            "Falcon",
            Date.from(Instant.ofEpochMilli(400000)),
            "Turku",
            SightingRarity.Rare,
            null,
            "Dolor sit amet"
        )
    )

    private val newSightingDatasInDatetimeAsc = listOf(
        NewSightingData(
            "Albratross",
            Date.from(Instant.ofEpochMilli(100000)),
            "Helsinki",
            SightingRarity.ExtremelyRare,
            "somename.jpg",
            "Lorem ipsum"
        ),
        NewSightingData(
            "Eagle",
            Date.from(Instant.ofEpochMilli(200000)),
            "Tampere",
            SightingRarity.Rare,
            null,
            null
        ),
        NewSightingData(
            "Owl",
            Date.from(Instant.ofEpochMilli(300000)),
            "Pori",
            SightingRarity.Common,
            "othername.jpg",
            null
        ),
        NewSightingData(
            "Falcon",
            Date.from(Instant.ofEpochMilli(400000)),
            "Turku",
            SightingRarity.Rare,
            null,
            "Dolor sit amet"
        )
    )

    private val sightingSamplesInDatetimeDesc: List<Sighting>
        get() = sightingsSamplesInDatetimeAsc.reversed()

    private val sightingSamplesInNameAsc: List<Sighting>
        get() = listOf(
            sightingsSamplesInDatetimeAsc[0],
            sightingsSamplesInDatetimeAsc[1],
            sightingsSamplesInDatetimeAsc[3],
            sightingsSamplesInDatetimeAsc[2]
        )

    private val sightingSamplesInNameDesc: List<Sighting>
        get() = sightingSamplesInNameAsc.reversed()
}

