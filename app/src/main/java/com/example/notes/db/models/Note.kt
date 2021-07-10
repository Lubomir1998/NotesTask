package com.example.notes.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    var text: String,
    val timestamp: Long,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)
