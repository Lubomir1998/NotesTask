package com.example.notes.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.databinding.AddOrEditNoteFragmentBinding
import com.example.notes.db.models.Note
import com.example.notes.ui.viewmodels.AddOrEditViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddOrEditNoteFragment: Fragment(R.layout.add_or_edit_note_fragment) {

    private var _binding: AddOrEditNoteFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOrEditViewModel by viewModels()
    private val args: AddOrEditNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddOrEditNoteFragmentBinding.bind(view)


        setTextFields(args.title, args.text)

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.trim().toString()
            if(title.isEmpty()) {
                Snackbar.make(requireView(), "Empty title", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val text = binding.etText.text.trim().toString()

            val timestamp = if(args.date != 0L) {
                args.date
            } else {
                System.currentTimeMillis()
            }

            val id = if(args.id.isNotEmpty()) {
                args.id
            } else {
                UUID.randomUUID().toString()
            }


            val note = Note(title, text, timestamp, id)
            viewModel.saveNote(note)
            findNavController().navigate(AddOrEditNoteFragmentDirections.actionAddOrEditNoteFragmentToHomeScreenFragment())
        }

    }



    private fun setTextFields(title: String, text: String? = null) {
        binding.etTitle.setText(title)
        text?.let {
            binding.etText.setText(it)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}