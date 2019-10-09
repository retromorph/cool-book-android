package ru.izifak.api

import android.content.Context
import androidx.room.*
import ru.izifak.models.AudioBook
import androidx.room.Room


class AudioBookDatabaseApi {

    companion object {
        val DATABASE_PATH: String
            get() = "audio-books-database"

        private lateinit var audioBookDatabase: AudioBookDatabase

        fun getInstance(context: Context): AudioBookDatabase {
            if (!::audioBookDatabase.isInitialized) {
                audioBookDatabase = Room.databaseBuilder(
                    context,
                    AudioBookDatabase::class.java, DATABASE_PATH
                ).build()
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

    @Database(entities = [AudioBook::class], version = 1)
    abstract inner class AudioBookDatabase : RoomDatabase() {
        abstract val audioBookDao: AudioBookDao
    }
}