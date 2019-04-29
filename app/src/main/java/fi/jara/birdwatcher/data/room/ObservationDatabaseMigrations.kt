package fi.jara.birdwatcher.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class ObservationDatabaseMigration_2_to_3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entities RENAME TO observations")
    }

}
