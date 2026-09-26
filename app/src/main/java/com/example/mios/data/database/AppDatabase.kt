package com.example.mios.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Song::class, Playlist::class, PlaylistSongCrossRef::class, Genre::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun genreDao(): GenreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val defaultGenres = listOf(
            "Rock", "Pop", "Reggaetón", "Electrónica", "Jazz",
            "Clásica", "Metal", "Hip-Hop", "Indie", "Otro"
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mios_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val genreDao = getDatabase(context).genreDao()
                            defaultGenres.forEach { genreDao.insertGenre(Genre(name = it)) }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}