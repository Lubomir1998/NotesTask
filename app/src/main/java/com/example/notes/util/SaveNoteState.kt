package com.example.notes.util

import com.example.notes.db.models.Note

sealed class SaveNoteState {
    data class Success(val notes: List<Note> = listOf(), val note: Note): SaveNoteState()
    data class Error(val message: String = "Something went wrong"): SaveNoteState()
    object Loading: SaveNoteState()
    object Empty: SaveNoteState()
}
