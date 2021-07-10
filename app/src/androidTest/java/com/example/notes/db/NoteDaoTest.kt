package com.example.notes.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.notes.db.models.Note
import com.example.notes.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
@ExperimentalCoroutinesApi
class NoteDaoTest {

    private lateinit var dataBase: DbHelper
    private lateinit var dao: NoteDao

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DbHelper::class.java
        ).allowMainThreadQueries().build()

        dao = dataBase.getDao()
    }

    @After
    fun tearDown() {
        dataBase.close()
    }


    @Test
    fun insertNoteIsWorking() = runBlockingTest {
        val note1 = Note("title", "text", 10L, "abv")
        val note2 = Note("title", "text", 10L, "abvfefwfw")
        val note3 = Note("title", "text", 10L, "abvcccc")

        dao.saveNote(note1)
        dao.saveNote(note2)
        dao.saveNote(note3)

        val notes = dao.getNotes().getOrAwaitValue()

        assertThat(notes.size).isEqualTo(3)
    }

    @Test
    fun deleteNoteIsWorking() = runBlockingTest {
        val note1 = Note("title", "text", 10L, "abv")
        val note2 = Note("title", "text", 10L, "abvfefwfw")
        val note3 = Note("title", "text", 10L, "abvcccc")

        dao.saveNote(note1)
        dao.saveNote(note2)
        dao.saveNote(note3)

        dao.deleteNote(note1)
        dao.deleteNote(note2)
        dao.deleteNote(note3)

        val notes = dao.getNotes().getOrAwaitValue()

        assertThat(notes).isEmpty()
    }

    @Test
    fun updateNotesIsWorking() = runBlockingTest {
        val note1 = Note("title", "text", 10L, "abv")
        val note2 = Note("title", "text", 10L, "abv")
        val note3 = Note("title", "text", 10L, "abvcccc")

        dao.saveNote(note1)
        dao.saveNote(note2)
        dao.saveNote(note3)

        val notes = dao.getNotes().getOrAwaitValue()

        assertThat(notes.size).isEqualTo(2)
    }


    @Test
    fun searchNoteIsWorking() = runBlockingTest {
        val note1 = Note("title", "text", 10L, "abv")
        val note2 = Note("aSd", "text", 10L, "abvfefwfw")
        val note3 = Note("tyuio", "text", 10L, "abvcccc")

        dao.saveNote(note1)
        dao.saveNote(note2)
        dao.saveNote(note3)

        val notes = dao.searchNotes("ASD").getOrAwaitValue()

        assertThat(notes.size).isEqualTo(1)

    }





}




