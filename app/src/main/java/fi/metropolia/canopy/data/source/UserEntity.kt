package fi.metropolia.canopy.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val userRole: String = "student" // Default value
)