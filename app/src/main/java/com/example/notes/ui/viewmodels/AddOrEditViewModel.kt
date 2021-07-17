package com.example.notes.ui.viewmodels

import android.app.Application
import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.R
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
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val context: Application
): ViewModel() {


    private val _saveNoteStatus = MutableStateFlow<SaveNoteState>(SaveNoteState.Empty)
    val saveNoteStatus: StateFlow<SaveNoteState> = _saveNoteStatus

    private val _updateNoteImageStatus = MutableStateFlow<UpdateNoteImageState>(UpdateNoteImageState.Empty)
    val updateNoteImageStatus: StateFlow<UpdateNoteImageState> = _updateNoteImageStatus

    private val _imgUri: MutableLiveData<Uri> = MutableLiveData()
    val imgUri: LiveData<Uri> = _imgUri



    fun saveNote(title: String, note: Note) {
        _saveNoteStatus.value = SaveNoteState.Loading
        if(title.isEmpty()) {
            _saveNoteStatus.value = SaveNoteState.Error(context.resources.getString(R.string.empty_title))
            return
        }
        viewModelScope.launch(dispatcher) {
            repository.saveNote(note)
            _saveNoteStatus.value = SaveNoteState.Success(note = note)
        }
    }


    fun updateNotesImage(note: Note) {
        _updateNoteImageStatus.value = UpdateNoteImageState.Loading
        viewModelScope.launch(dispatcher) {
            repository.saveNote(note)
            _updateNoteImageStatus.value = UpdateNoteImageState.Success(note)
        }
    }



    fun setImgUri(uri: Uri) {
        _imgUri.postValue(uri)
    }

    sealed class UpdateNoteImageState {
        data class Success(val note: Note): UpdateNoteImageState()
        data class Error(val message: String = Resources.getSystem().getString(R.string.something_went_wrong)): UpdateNoteImageState()
        object Loading: UpdateNoteImageState()
        object Empty: UpdateNoteImageState()
    }



}