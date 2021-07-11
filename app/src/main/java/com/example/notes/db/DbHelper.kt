package com.example.notes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notes.db.models.Note

@Database(entities = [Note::class], version = 2)
abstract class DbHelper: RoomDatabase() {

    abstract fun getDao(): NoteDao

}