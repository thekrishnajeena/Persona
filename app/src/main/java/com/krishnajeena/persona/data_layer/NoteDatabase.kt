package com.krishnajeena.persona.data_layer

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(exportSchema = true, entities = arrayOf(Note::class), version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract val notedao: NotesDao
}