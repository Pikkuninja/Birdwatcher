package fi.jara.birdwatcher.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fi.jara.birdwatcher.common.Coordinate
import fi.jara.birdwatcher.data.NewSightingData
import fi.jara.birdwatcher.sightings.Sighting
import fi.jara.birdwatcher.sightings.SightingRarity
import java.util.*


@Entity(tableName = "entities")
data class SightingEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = idColumnName) val id: Long = 0,
    @ColumnInfo(name = speciesNameColumnName) val species: String,
    @ColumnInfo(name = timestampColumnName) val timestamp: Date,
    @ColumnInfo(name = latitudeColumnName) val latitude: Double?,
    @ColumnInfo(name = longitudeColumnName) val longitude: Double?,
    @ColumnInfo(name = rarityColumnName) val rarity: SightingRarity,
    @ColumnInfo(name = imageColumnName) val imageName: String?,
    @ColumnInfo(name = descriptionColumnName) val description: String?
) {
    companion object {
        const val idColumnName = "id"
        const val speciesNameColumnName = "species"
        const val timestampColumnName = "timestamp"
        const val latitudeColumnName = "latitude"
        const val longitudeColumnName = "longitude"
        const val rarityColumnName = "rarity"
        const val imageColumnName = "image"
        const val descriptionColumnName = "description"

        fun fromNewSightingData(data: NewSightingData): SightingEntity =
            SightingEntity(
                0,
                data.species,
                data.timestamp,
                data.location?.latitude,
                data.location?.longitude,
                data.rarity,
                data.imageName,
                data.description
            )
    }

    fun toSightingModel(): Sighting {
        val coordinate =
            if (latitude != null && longitude != null)
                Coordinate(latitude, longitude)
            else null

        return Sighting(
            id,
            species,
            timestamp,
            coordinate,
            rarity,
            imageName,
            description
        )
    }
}