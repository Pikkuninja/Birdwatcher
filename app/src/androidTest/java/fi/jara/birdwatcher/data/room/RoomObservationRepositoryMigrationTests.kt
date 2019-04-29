package fi.jara.birdwatcher.data.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomObservationRepositoryMigrationTests {

    private val TEST_DB = "migration-test"

    @Rule
    @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ObservationDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate2to3() {
        var db = helper.createDatabase(TEST_DB, 2).apply {
            execSQL("""INSERT INTO entities VALUES (1, 'Albatross', 1000, 60, 20, 'common', null, null)""")
            close()
        }

        db = helper.runMigrationsAndValidate(
            TEST_DB,
            3,
            true,
            ObservationDatabaseMigration_2_to_3()
        )
    }

}