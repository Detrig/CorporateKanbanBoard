package github.detrig.corporatekanbanboard.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import github.detrig.corporatekanbanboard.data.local.dao.BoardsDao
import github.detrig.corporatekanbanboard.data.local.model.BoardEntity
import github.detrig.corporatekanbanboard.data.local.model.BoardTypeConverter

@Database(
    entities = [BoardEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BoardTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boardsDao(): BoardsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "boards"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}