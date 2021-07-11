package com.example.notes.repositories

import com.example.notes.db.models.Note

class FakeNoteRepository: NoteRepository {

    val notes: MutableList<Note> = mutableListOf()

    override suspend fun saveNote(note: Note) {
        // we check if the note exists
        // so we can just update it
        for(item in notes) {
            if(item.id == note.id) {
                notes.apply {
                    remove(item)
                    add(note)
                    return
                }
            }
        }
        // if we reach this code it means the note is new
        // and we will insert it
        notes.add(note)
    }

    override suspend fun deleteNote(note: Note) {
        notes.remove(note)
    }

    override suspend fun getNotes(): List<Note> {
        return notes
    }

    override suspend fun searchNotes(query: String): List<Note> {
        return notes.filter { note ->
            note.title.toLowerCase().contains(query.toLowerCase())
        }
    }
}