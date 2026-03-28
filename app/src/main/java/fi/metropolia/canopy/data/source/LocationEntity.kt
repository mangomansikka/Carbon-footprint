package fi.metropolia.canopy.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),

    // Persistent trip results
    val transportModes: String = "",
    val carbonEmissionGrams: Float = 0f,

    // Existing fields for mode-specific totals
    val emissionBussKg: Double = 0.0,
    val emissionMetroKg: Double = 0.0,
    val emissionTrainKg: Double = 0.0,
    val emissionPetrolCarKg: Double = 0.0,
    val emissionDieselCarKg: Double = 0.0,
    val emissionHybridCarKg: Double = 0.0,
    val emissionUnknownCarKg: Double = 0.0,
    val emissionElectricCarKg: Double = 0.0,
    val emissionMopedKg: Double = 0.0
)
