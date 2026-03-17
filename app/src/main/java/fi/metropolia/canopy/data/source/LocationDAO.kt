package fi.metropolia.canopy.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDAO {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT COALESCE(SUM(emissionBussKg), 0.0) FROM locations")
    suspend fun getTotalBusEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionMetroKg), 0.0) FROM locations")
    suspend fun getTotalMetroEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionPetrolCarKg), 0.0) FROM locations")
    suspend fun getTotalPetrolCarEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionDieselCarKg), 0.0) FROM locations")
    suspend fun getTotalDieselCarEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionHybridCarKg), 0.0) FROM locations")
    suspend fun getTotalHybridCarEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionElectricCarKg), 0.0) FROM locations")
    suspend fun getTotalElectricCarEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionUnknownCarKg), 0.0) FROM locations")
    suspend fun getTotalUnknownCarEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionMopedKg), 0.0) FROM locations")
    suspend fun getTotalMopedEmissions(): Double

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}