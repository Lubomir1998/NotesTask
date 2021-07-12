package com.example.notes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Note
import com.example.notes.repositories.NoteRepository
import com.example.notes.util.SaveNoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _notes = MutableStateFlow<NotesState>(NotesState.Empty)
    val notes: StateFlow<NotesState> = _notes

    private val _saveNoteStatus = MutableStateFlow<SaveNoteState>(SaveNoteState.Empty)
    val saveNoteStatus: StateFlow<SaveNoteState> = _saveNoteStatus

    private val _deleteNoteStatus = MutableSharedFlow<DeleteNoteState>()
    val deleteNoteStatus: SharedFlow<DeleteNoteState> = _deleteNoteStatus

    fun deleteNote(note: Note) {
        viewModelScope.launch(dispatcher) {
            _deleteNoteStatus.emit(DeleteNoteState.Loading)
            repository.deleteNote(note)
            _deleteNoteStatus.emit(DeleteNoteState.Success(note, repository.getNotes()))
        }
    }

    fun saveNote(note: Note) {
        _saveNoteStatus.value = SaveNoteState.Loading
        viewModelScope.launch(dispatcher) {
            repository.saveNote(note)
            _saveNoteStatus.value = SaveNoteState.Success(repository.getNotes())
        }
    }

    fun getAllNotes() {
        _notes.value = NotesState.Loading
        viewModelScope.launch(dispatcher) {
            _notes.value = NotesState.Success(repository.getNotes())
        }
    }

    fun searchNotes(query: String) {
        _notes.value = NotesState.Loading
        viewModelScope.launch(dispatcher) {
            _notes.value = if(query.trim().isEmpty()) {
                NotesState.Success(repository.getNotes())
            } else {
                NotesState.Success(repository.searchNotes(query.trim()))
            }
        }
    }


    sealed class NotesState {
        object Empty : NotesState()
        object Loading: NotesState()
        data class Success(val data: List<Note>) : NotesState()
    }


    sealed class DeleteNoteState {
        data class Success(val note: Note, val notes: List<Note>): DeleteNoteState()
        data class Error(val message: String = "Something went wrong"): DeleteNoteState()
        object Loading: DeleteNoteState()
    }

}