package com.krishnajeena.persona.data_layer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
@Dao
interface NotesDao {
    @Upsert
    suspend fun upsert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note ORDER BY dateDated ASC")
    fun getAllOrderedDataAdded(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title ASC")
    fun getAllOrderedTitle(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): Note?
}
