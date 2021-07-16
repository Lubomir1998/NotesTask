package com.example.notes.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
        val title: String,
        val text: String,
        val timestamp: Long,
        var imgUri: String? = null,
        @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)