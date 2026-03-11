package fi.metropolia.canopy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [LocationEntity::class],
    version = 2, // Was 1, incremented to 2 for schema change
    exportSchema = false
)
abstract class CanopyDatabase : RoomDatabase() {
    abstract fun locationDao(): DAO

    companion object {
        @Volatile
        private var instance: CanopyDatabase? = null

        fun getInstance(context: Context): CanopyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CanopyDatabase::class.java,
                    "canopy_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}