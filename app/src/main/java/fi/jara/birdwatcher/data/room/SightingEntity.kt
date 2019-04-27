package fi.jara.birdwatcher.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fi.jara.birdwatcher.sightings.SightingRarity
import java.util.*


@Entity(tableName = "entities")
data class SightingEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = idColumnName) val id: Long = 0,
    @ColumnInfo(name = speciesNameColumnName) val species: String,
    @ColumnInfo(name = timestampColumnName) val timestamp: Date,
    @ColumnInfo(name = locationColumnName) val location: String,
    @ColumnInfo(name = rarityColumnName) val rarity: SightingRarity,
    @ColumnInfo(name = imageColumnName) val imageName: String?,
    @ColumnInfo(name = descriptionColumnName) val description: String?
) {
    companion object {
        const val idColumnName = "id"
        const val speciesNameColumnName = "species"
        const val timestampColumnName = "timestamp"
        const val locationColumnName = "location"
        const val rarityColumnName = "rarity"
        const val imageColumnName = "image"
        const val descriptionColumnName = "description"
    }
}
