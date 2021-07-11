package com.example.notes.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Note
import com.example.notes.repositories.NoteRepository
import com.example.notes.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    var notes = repository.getNotes()


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


    fun searchNotes(query: String) {
        viewModelScope.launch(dispatcher.main) {
            notes = if(query.trim().isEmpty()) {
                repository.getNotes()
            } else {
                repository.searchNotes(query)
            }
        }
    }


}