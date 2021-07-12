package com.example.notes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Note
import com.example.notes.repositories.NoteRepository
import com.example.notes.util.SaveNoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    private val _saveNoteStatus = MutableStateFlow<SaveNoteState>(SaveNoteState.Empty)
    val saveNoteStatus: StateFlow<SaveNoteState> = _saveNoteStatus


    fun saveNote(note: Note) {
        _saveNoteStatus.value = SaveNoteState.Loading
        viewModelScope.launch(dispatcher) {
            repository.saveNote(note)
            _saveNoteStatus.value = SaveNoteState.Success()
        }
    }


}