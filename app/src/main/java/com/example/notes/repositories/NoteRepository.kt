package com.example.notes.repositories

import androidx.lifecycle.LiveData
import com.example.notes.db.models.Note

interface NoteRepository {

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun getNotes(): List<Note>

    suspend fun searchNotes(query: String): List<Note>

}