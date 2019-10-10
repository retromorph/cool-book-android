package ru.izifak.api

import android.content.Context
import android.util.Log
import androidx.room.*
import ru.izifak.models.AudioBook
import androidx.room.Room


@Database(entities = [AudioBook::class], version = 2,  exportSchema = false)
abstract class AudioBookDatabaseApi : RoomDatabase() {

    abstract fun audioBookDao(): AudioBookDao

    companion object {
        val DATABASE_PATH: String
            get() = "audio-books-database-1"

        private lateinit var audioBookDatabase: AudioBookDatabaseApi

        @Synchronized
        fun getInstance(context: Context): AudioBookDatabaseApi {
            if (!::audioBookDatabase.isInitialized) {
                audioBookDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    AudioBookDatabaseApi::class.java, DATABASE_PATH
                )
                    .fallbackToDestructiveMigration()
                    .build()
                Log.d("FUCK", "entrance 1")
            }
            return audioBookDatabase
        }
    }

    @Dao
    interface AudioBookDao {

        @get:Query("SELECT * FROM audiobook")
        val allAudioBooks: List<AudioBook>

        @Insert
        fun insertAll(vararg audioBooks: AudioBook)

        @Delete
        fun delete(audioBook: AudioBook)
    }
}