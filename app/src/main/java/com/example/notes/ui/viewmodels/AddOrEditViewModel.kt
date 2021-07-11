package com.example.notes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Note
import com.example.notes.repositories.NoteRepository
import com.example.notes.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val dispatcher: DispatcherProvider
): ViewModel() {


    fun saveNote(note: Note) {
        viewModelScope.launch(dispatcher.main) {
            repository.saveNote(note)
        }
    }

}