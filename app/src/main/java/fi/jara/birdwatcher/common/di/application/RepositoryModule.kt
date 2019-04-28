package fi.jara.birdwatcher.common.di.application

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fi.jara.birdwatcher.data.SightingRepository
import fi.jara.birdwatcher.data.room.RoomSightingRepository
import fi.jara.birdwatcher.data.room.SightingDatabase
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideSightingRepository(context: Context): SightingRepository {
        val database = Room.databaseBuilder(context, SightingDatabase::class.java, "sightings.sqlite").build()
        return RoomSightingRepository(database)
    }
}