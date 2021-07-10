package com.example.notes.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(timestamp)
}