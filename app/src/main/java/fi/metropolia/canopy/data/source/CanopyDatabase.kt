package fi.metropolia.canopy.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class CanopyDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDAO

    companion object {
        @Volatile
        private var instance: CanopyDatabase? = null

        fun getInstance(context: Context): CanopyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CanopyDatabase::class.java,
                    "canopy_db"
                ).build().also { instance = it }
            }
    }
}