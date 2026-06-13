package com.example.musical.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musical.data.local.dao.PlaylistDao
import com.example.musical.data.local.dao.SongDao
import com.example.musical.data.local.entities.PlaylistEntity
import com.example.musical.data.local.entities.PlaylistSongCrossRef
import com.example.musical.data.local.entities.SongEntity

@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistSongCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class MusicalDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: MusicalDatabase? = null

        fun getInstance(context: Context): MusicalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicalDatabase::class.java,
                    "musical_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
