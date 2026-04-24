package fi.metropolia.canopy.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDAO {
    @Query("SELECT COALESCE(SUM(emissionBussKg), 0.0) FROM locations")
    suspend fun getTotalBusEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionMetroKg), 0.0) FROM locations")
    suspend fun getTotalMetroEmissions(): Double

    @Query("SELECT COALESCE(SUM(emissionTrainKg), 0.0) FROM locations")
    suspend fun getTotalTrainEmissions(): Double

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

    @Query("SELECT COALESCE(SUM(walkingDistanceM), 0.0) FROM locations")
    suspend fun getTotalWalkingDistance(): Double

    @Query("SELECT COALESCE(SUM(cyclingDistanceM), 0.0) FROM locations")
    suspend fun getTotalCyclingDistance(): Double

    @Query("SELECT * FROM locations ORDER BY timestampMillis DESC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("""
        SELECT strftime('%m', timestampMillis / 1000, 'unixepoch') as month, 
               SUM(emissionBussKg + emissionMetroKg + emissionTrainKg + 
                   emissionPetrolCarKg + emissionDieselCarKg + emissionHybridCarKg + emissionUnknownCarKg + 
                   emissionElectricCarKg + emissionMopedKg) * 1000 as totalEmissionsGrams
        FROM locations 
        GROUP BY month 
        ORDER BY month ASC
    """)
    suspend fun getMonthlyEmissions(): List<MonthlyEmission>

    @Query("""
    SELECT 
        COALESCE(SUM(emissionBussKg), 0.0) as bus,
        COALESCE(SUM(emissionMetroKg), 0.0) as metro,
        COALESCE(SUM(emissionTrainKg), 0.0) as train,
        COALESCE(SUM(emissionPetrolCarKg), 0.0) as petrol,
        COALESCE(SUM(emissionDieselCarKg), 0.0) as diesel,
        COALESCE(SUM(emissionHybridCarKg), 0.0) as hybrid,
        COALESCE(SUM(emissionElectricCarKg), 0.0) as electric,
        COALESCE(SUM(emissionUnknownCarKg), 0.0) as unknown,
        COALESCE(SUM(emissionMopedKg), 0.0) as moped
    FROM locations
""")
    suspend fun getEmissionsSummary(): EmissionsSummary

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(entity: LocationEntity)

    @Query("SELECT * FROM locations WHERE timestampMillis >= :startDate AND timestampMillis <= :endDate")
    suspend fun getLocationsByDate(startDate: Long, endDate: Long): List<LocationEntity>

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationsById(id: Int)

    @Query("SELECT DISTINCT(strftime('%Y-%m-%d', timestampMillis / 1000, 'unixepoch')) FROM locations")
    suspend fun getDaysWithData(): List<String>
}
