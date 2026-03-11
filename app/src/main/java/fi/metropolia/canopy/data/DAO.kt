package fi.metropolia.canopy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DAO {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT COALESCE(SUM(segmentDistanceMeters), 0.0) FROM locations")
    suspend fun getTotalDistanceMeters(): Double

    @Query("SELECT COALESCE(SUM(segmentEmissionKg), 0.0) FROM locations")
    suspend fun getTotalEmissionKg(): Double

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}