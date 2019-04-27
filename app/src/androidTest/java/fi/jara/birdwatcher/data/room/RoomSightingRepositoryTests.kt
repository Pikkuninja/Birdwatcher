package fi.jara.birdwatcher.data.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.jraska.livedata.test
import fi.jara.birdwatcher.data.NewSightingData
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingRarity
import fi.jara.birdwatcher.sightings.SightingSorting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
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
    fun insertToRepository_emitsLiveDataWithAutoincrementedId() {
        val liveData = repository.allSightings(SightingSorting.TimeAscending)

        liveData.test().assertValue(emptyList())

        runBlocking {
            repository.addSighting(newSightingDatasInDatetimeAsc[0])
        }

        liveData.test().assertValue(listOf(sightingsSamplesInDatetimeAsc[0]))

        runBlocking {
            repository.addSighting(newSightingDatasInDatetimeAsc[1])
        }

        liveData.test().assertValue(listOf(sightingsSamplesInDatetimeAsc[0], sightingsSamplesInDatetimeAsc[1]))
    }

    @Test
    fun getAllInDatetimeAsc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.TimeAscending)
            .test()
            .assertValue(sightingsSamplesInDatetimeAsc)
    }

    @Test
    fun getAllInDatetimeDesc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.TimeDescending)
            .test()
            .assertValue(sightingSamplesInDatetimeDesc)
    }

    @Test
    fun getAllInNameAsc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.NameAscending)
            .test()
            .assertValue(sightingSamplesInNameAsc)
    }

    @Test
    fun getAllInNameDesc_correctOrder() {
        insertAllTestData()

        repository.allSightings(SightingSorting.NameDescending)
            .test()
            .assertValue(sightingSamplesInNameDesc)
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

