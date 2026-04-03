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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUserRole(user: UserEntity)
}