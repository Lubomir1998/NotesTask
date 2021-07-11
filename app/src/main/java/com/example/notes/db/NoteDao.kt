package com.example.notes.db

import androidx.room.*
import com.example.notes.db.models.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note ORDER BY timestamp DESC")
    suspend fun getNotes(): List<Note>

    @Query("SELECT * FROM Note WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'")
    suspend fun searchNotes(query: String): List<Note>


}