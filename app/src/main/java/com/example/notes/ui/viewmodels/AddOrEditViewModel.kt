package com.example.notes.ui.viewmodels

import android.net.Uri
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


    fun saveNote(title: String, note: Note) {
        _saveNoteStatus.value = SaveNoteState.Loading
        if(title.isEmpty()) {
            _saveNoteStatus.value = SaveNoteState.Error("Empty title")
            return
        }
        viewModelScope.launch(dispatcher) {
            repository.saveNote(note)
            _saveNoteStatus.value = SaveNoteState.Success()
        }
    }


    private val _imgUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val imgUri: StateFlow<Uri?> = _imgUri

    fun setImgUri(uri: Uri) {
        _imgUri.value = uri
    }


}