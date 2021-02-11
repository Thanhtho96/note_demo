package com.tt.noteapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tt.noteapplication.dao.NoteDao
import com.tt.noteapplication.model.AlarmOfNoteEntity
import com.tt.noteapplication.model.LabelEntity
import com.tt.noteapplication.model.LabelNoteCrossRef
import com.tt.noteapplication.model.NoteEntity

@Database(
    entities = [NoteEntity::class, LabelEntity::class, LabelNoteCrossRef::class, AlarmOfNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoteRoomDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): NoteRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteRoomDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}