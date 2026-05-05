package fi.metropolia.canopy.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main Room database for the application.
 * Manages persisted data for trip locations and user information.
 */
@Database(
    entities = [LocationEntity::class, UserEntity::class],
    version = 17, //change version when schema changes
    exportSchema = false
)
abstract class CanopyDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: CanopyDatabase? = null

        /**
         * Provides a thread-safe singleton instance of the database.
         */
        fun getInstance(context: Context): CanopyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CanopyDatabase::class.java,
                    "canopy_db"
                )
                    // Wipes and rebuilds the database if the version is incremented without a migration path
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
