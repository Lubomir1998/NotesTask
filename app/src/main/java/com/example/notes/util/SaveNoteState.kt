package com.example.notes.util

import android.content.res.Resources
import com.example.notes.R
import com.example.notes.db.models.Note

sealed class SaveNoteState {
    data class Success(val notes: List<Note> = listOf(), val note: Note): SaveNoteState()
    data class Error(val message: String = Resources.getSystem().getString(R.string.something_went_wrong)): SaveNoteState()
    object Loading: SaveNoteState()
    object Empty: SaveNoteState()
}
