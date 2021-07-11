package com.example.notes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Note
import com.example.notes.repositories.NoteRepository
import com.example.notes.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _notes = MutableStateFlow<State>(State.Empty)
    val notes: StateFlow<State> = _notes


    fun deleteNote(note: Note) {
        viewModelScope.launch(dispatcher.main) {
            repository.deleteNote(note)
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch(dispatcher.main) {
            repository.saveNote(note)
        }
    }

    fun getAllNotes() {
        _notes.value = State.Loading
        viewModelScope.launch(dispatcher.main) {
            _notes.value = State.Success(repository.getNotes())
        }
    }

    fun searchNotes(query: String) {
        _notes.value = State.Loading
        viewModelScope.launch(dispatcher.main) {
            _notes.value = if(query.trim().isEmpty()) {
                State.Success(repository.getNotes())
            } else {
                State.Success(repository.getNotes())
            }
        }
    }


    sealed class State {
        object Empty : State()
        object Loading : State()
        data class Success(val data: List<Note>) : State()
    }


}