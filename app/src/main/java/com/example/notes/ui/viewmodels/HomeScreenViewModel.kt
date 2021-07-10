package com.example.notes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.notes.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    repository: NoteRepository
) : ViewModel() {

    val notes = repository.getNotes()

}