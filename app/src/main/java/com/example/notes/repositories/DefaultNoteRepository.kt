package com.example.notes.repositories

import androidx.lifecycle.LiveData
import com.example.notes.db.NoteDao
import com.example.notes.db.models.Note
import javax.inject.Inject

class DefaultNoteRepository @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override suspend fun saveNote(note: Note) {
        dao.saveNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

    override fun getNotes(): LiveData<List<Note>> {
        return dao.getNotes()
    }
}