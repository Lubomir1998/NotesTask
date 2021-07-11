package com.example.notes.util

sealed class SaveNoteState {
    object Success: SaveNoteState()
    data class Error(val message: String = "Something went wrong"): SaveNoteState()
    object Loading: SaveNoteState()
    object Empty: SaveNoteState()
}
