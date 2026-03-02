package fi.metropolia.canopy.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDAO {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}