package com.example.notes.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notes.MainCoroutineRule
import com.example.notes.db.models.Note
import com.example.notes.repositories.FakeNoteRepository
import com.example.notes.util.SaveNoteState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeScreenViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setup() {
        viewModel = HomeScreenViewModel(FakeNoteRepository(), TestCoroutineDispatcher())
    }


    @Test
    fun saveNoteWorks() {
        val note1 = Note("title", "text", 342L)
        viewModel.saveNote(note1)

        val state = viewModel.saveNoteStatus.value

        assertThat(state).isEqualTo(SaveNoteState.Success())
    }


}