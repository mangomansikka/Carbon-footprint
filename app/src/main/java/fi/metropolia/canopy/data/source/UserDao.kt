package fi.metropolia.canopy.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT userRole FROM user_profile WHERE id = 1")
    fun getUserRole(): Flow<String?>

    @Query("SELECT customCo2 FROM user_profile WHERE id = 1")
    fun getCustomCo2(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUserRole(user: UserEntity)

    @Query("UPDATE user_profile SET customCo2 = :value WHERE id = 1")
    suspend fun updateCustomCo2(value: Double)
    @Query("SELECT customCo2 FROM user_profile WHERE id = 1")
     fun getCustomCo2Value(): Double?
}