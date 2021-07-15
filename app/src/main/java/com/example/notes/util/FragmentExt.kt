package com.example.notes.util

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun Fragment.snackbar(@StringRes res: Int) {
    Snackbar.make(requireView(), res, Snackbar.LENGTH_LONG).show()
}