package com.example.notes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notes.db.models.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note ORDER BY timestamp DESC")
    fun getNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'")
    fun searchNotes(query: String): LiveData<List<Note>>


}