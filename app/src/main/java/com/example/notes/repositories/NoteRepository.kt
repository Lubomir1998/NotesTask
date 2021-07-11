package com.example.notes.repositories

import androidx.lifecycle.LiveData
import com.example.notes.db.models.Note

interface NoteRepository {

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(note: Note)

    fun getNotes(): LiveData<List<Note>>

    fun searchNotes(query: String): LiveData<List<Note>>

}